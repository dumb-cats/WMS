-- =================================================================================
-- 库房管理系统 (WMS) 核心数据库 Schema - V2.0
-- 设计意图：
-- 1. 解决货物乱摆乱放：通过任务驱动扫码验证 (work_tasks + task_checkpoints)
-- 2. 解决货物找不到：精确到货位的实时库存记录 (inventory)
-- 3. 解决责任追溯：所有库存变更强制关联任务与执行人 (inventory_movements)
-- 4. 解决合理规划：引入坐标系、ABC分类、占用率等维度 (locations + products)
-- =================================================================================

-- 1. 用户表
-- 职责：追溯行为的主体，区分管理员与普通工人
CREATE TABLE users (
    user_id VARCHAR(255) PRIMARY KEY, -- 用户唯一标识（通常为工号或系统分配ID）
    username VARCHAR(100) UNIQUE NOT NULL, -- 登录用户名
    password VARCHAR(255) NOT NULL, -- 加密后的登录密码
    name VARCHAR(100), -- 姓名
    role_id INTEGER NOT NULL DEFAULT 0, -- 角色ID：0-普通工人, 1-管理员
    created_at TIMESTAMPTZ DEFAULT NOW(), -- 创建时间
    updated_at TIMESTAMPTZ DEFAULT NOW() -- 最后更新时间
);

-- 2. 物料表 (SKU)
-- 职责：定义存储在库房中的各种物料的基本信息
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL, -- 货物条码/SKU编码，这是工人扫码验证的核心字段
    name VARCHAR(255) NOT NULL, -- 货物名称
    category VARCHAR(100), -- 货物类别
    unit VARCHAR(20), -- 计量单位：如 件、箱、托、千克
    description TEXT, -- 货物详细描述
    min_stock INTEGER DEFAULT 0, -- 安全库存下限：低于此值系统应预警补货
    max_stock INTEGER, -- 库存上限：用于库位规划，防止过度堆积
    abc_class CHAR(1), -- ABC分类：A-高频流动(应放近处), B-中频, C-低频(可放远处)
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. 库位表
-- 职责：定义库房的层级结构，每个库位视为一个“停车位”
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL, -- 库位编码（如 A-01-05），工人扫码验证的库位标识
    type VARCHAR(20) NOT NULL, -- 库位类型：ZONE(区域), BIN(具体货位)
    parent_id INTEGER REFERENCES locations(id), -- 层级关系：例如 货位 属于 货架，货架 属于 区域
    description VARCHAR(255), -- 库位备注信息
    is_active BOOLEAN DEFAULT TRUE, -- 库位是否可用（如损坏可设为false）
    coordinate_x DECIMAL(10, 2), -- 物理空间坐标X：用于计算最优拣货路径
    coordinate_y DECIMAL(10, 2), -- 物理空间坐标Y：用于计算最优拣货路径
    max_weight DECIMAL(10, 2), -- 承重限制
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE, -- 库位是否已被占用 (true/false)
    current_product_id INTEGER REFERENCES products(id), -- 当前停放在该库位的商品ID
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. 工作任务表
-- 职责：实现“先有任务，后有操作”的强约束，所有库存变更必须有源头任务
CREATE TABLE work_tasks (
    id SERIAL PRIMARY KEY,
    task_no VARCHAR(50) UNIQUE NOT NULL, -- 唯一业务单号，如 T202403030001
    task_type VARCHAR(20) NOT NULL, -- 任务类型：INBOUND(入库), OUTBOUND(出库), TRANSFER(移库)
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- 任务状态流转：PENDING(待指派) -> ASSIGNED(已派单) -> IN_PROGRESS(执行中) -> COMPLETED(已完成) | ABORTED(已放弃)
    
    product_id INTEGER REFERENCES products(id), -- 关联要操作的货物
    target_quantity INTEGER NOT NULL, -- 任务计划数量
    actual_quantity INTEGER DEFAULT 0, -- 实际完成数量
    
    from_location_id INTEGER REFERENCES locations(id), -- 源库位：出库或移库时的起始位置
    to_location_id INTEGER REFERENCES locations(id),   -- 目标库位：入库或移库时的存放位置
    
    assigned_user_id VARCHAR(255) REFERENCES users(user_id), -- 任务执行人：责任追溯的关键点
    start_time TIMESTAMPTZ, -- 工人点击“开始执行”的时间
    end_time TIMESTAMPTZ, -- 工人完成任务并提交的时间
    abort_reason TEXT, -- 放弃原因：工人无法完成任务时必须填写，用于追查货物缺失或乱放的源头
    
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    version INTEGER DEFAULT 1 -- 乐观锁版本号：处理多名工人抢单或并发操作冲突
);

-- 5. 任务结果审查
-- 职责：解决“乱摆乱放”的取证，确保工人确实在正确的库位操作了正确的货物
CREATE TABLE task_checkpoints (
    id SERIAL PRIMARY KEY,
    task_id INTEGER REFERENCES work_tasks(id), -- 关联的任务
    user_id VARCHAR(255) REFERENCES users(user_id), -- 操作人
    operation_type VARCHAR(50), -- 操作类型：SCAN_SKU(扫货物码), SCAN_LOCATION(扫库位码)
    scanned_code VARCHAR(100), -- 工人实际扫码获得的值
    is_correct BOOLEAN, -- 系统自动比对结果：工人是否扫对了？
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. 实时库存表
-- 职责：回答“什么货、在哪里、有多少”的实时状态
CREATE TABLE inventory (
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES products(id), -- 货物
    location_id INTEGER REFERENCES locations(id), -- 库位
    quantity INTEGER NOT NULL DEFAULT 0, -- 当前实际库存量
    locked_quantity INTEGER DEFAULT 0, -- 锁定库存量：已被分配出库任务但尚未实际搬出的货物量，防止超分
    last_updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(product_id, location_id) -- 约束：同一个库位同一种货只有一条汇总记录
);

-- 7. 库存流水表
-- 职责：记录每一次库存变动的历史细节，是所有追溯报告的数据源
CREATE TABLE inventory_movements (
    id SERIAL PRIMARY KEY,
    work_task_id INTEGER REFERENCES work_tasks(id), -- 每一笔流水必须关联到一个具体的任务单
    user_id VARCHAR(255) REFERENCES users(user_id), -- 记录是谁执行的变动
    product_id INTEGER NOT NULL, -- 货物ID
    from_location_id INTEGER, -- 变动源库位
    to_location_id INTEGER, -- 变动目标库位
    change_quantity INTEGER NOT NULL, -- 变动数量（正数为增，负数为减）
    movement_type VARCHAR(20), -- 变动类型：IN(入库), OUT(出库), ADJ(盘点调整)
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. 任务分配规则表
-- 职责：定义货物入库后自动分配到目标区域的规则
CREATE TABLE assignment_rules (
    id SERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL, -- 规则名称，如“A类摩托车分配到A区”
    product_category VARCHAR(100) NOT NULL, -- 匹配的产品类别
    target_zone_id INTEGER REFERENCES locations(id) NOT NULL, -- 目标区域ID
    priority INTEGER NOT NULL DEFAULT 0, -- 规则优先级，数字越大优先级越高
    is_active BOOLEAN DEFAULT TRUE, -- 规则是否生效
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- =================================================================================
-- 索引设计 (Performance Index)
-- ===================================================================================

CREATE INDEX idx_products_sku ON products(sku); -- 扫码查询货物的速度
CREATE INDEX idx_locations_code ON locations(code); -- 扫码查询库位速度
CREATE INDEX idx_work_tasks_status ON work_tasks(status); -- 工人查看待办任务列表的速度
CREATE INDEX idx_work_tasks_user ON work_tasks(assigned_user_id); -- 按人查看任务的速度
CREATE INDEX idx_inventory_product ON inventory(product_id); -- 询某货物在所有库位分布的速度
CREATE INDEX idx_inventory_location ON inventory(location_id); -- 查询某库位下所有货物明细的速度
CREATE INDEX idx_movements_task ON inventory_movements(work_task_id); -- 追溯审计查询速度
CREATE INDEX idx_assignment_rules_category_active ON assignment_rules(product_category, is_active); -- 查询激活的分配规则的速度
