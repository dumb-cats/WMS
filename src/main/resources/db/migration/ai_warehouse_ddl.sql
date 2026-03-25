-- ============================================================
-- 基于人工智能的库房管理系统 - MySQL 建表脚本
-- 数据库版本：MySQL 8.0+
-- 字符集：utf8mb4    排序规则：utf8mb4_general_ci
-- 设计原则：企业级规范，全字段注释，逻辑删除，审计字段
-- ============================================================

-- -----------------------------------------------------------
-- 一、系统管理模块
-- -----------------------------------------------------------

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '用户主键ID',
    `username`        VARCHAR(64)   NOT NULL                 COMMENT '登录用户名（唯一）',
    `password`        VARCHAR(255)  NOT NULL                 COMMENT '登录密码（BCrypt加密存储）',
    `real_name`       VARCHAR(64)   DEFAULT NULL             COMMENT '用户真实姓名',
    `employee_no`     VARCHAR(32)   DEFAULT NULL             COMMENT '员工工号',
    `phone`           VARCHAR(20)   DEFAULT NULL             COMMENT '手机号码',
    `email`           VARCHAR(128)  DEFAULT NULL             COMMENT '电子邮箱',
    `avatar`          VARCHAR(512)  DEFAULT NULL             COMMENT '用户头像URL',
    `gender`          TINYINT       DEFAULT 0                COMMENT '性别（0-未知 1-男 2-女）',
    `status`          TINYINT       DEFAULT 1                COMMENT '账号状态（0-停用 1-正常 2-锁定）',
    `last_login_time` DATETIME      DEFAULT NULL             COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(64)   DEFAULT NULL             COMMENT '最后登录IP地址',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_employee_no` (`employee_no`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统用户表';

-- 2. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '角色主键ID',
    `role_code`       VARCHAR(64)   NOT NULL                 COMMENT '角色编码（唯一，如 ADMIN/WAREHOUSE_MANAGER）',
    `role_name`       VARCHAR(64)   NOT NULL                 COMMENT '角色名称',
    `sort_order`      INT           DEFAULT 0                COMMENT '显示排序',
    `status`          TINYINT       DEFAULT 1                COMMENT '角色状态（0-停用 1-正常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统角色表';

-- 3. 用户-角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `user_id`         BIGINT        NOT NULL                 COMMENT '用户ID',
    `role_id`         BIGINT        NOT NULL                 COMMENT '角色ID',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- 4. 菜单权限表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '菜单主键ID',
    `parent_id`       BIGINT        DEFAULT 0                COMMENT '父菜单ID（0表示顶级菜单）',
    `menu_name`       VARCHAR(64)   NOT NULL                 COMMENT '菜单名称',
    `menu_type`       TINYINT       NOT NULL                 COMMENT '菜单类型（1-目录 2-菜单 3-按钮/权限）',
    `permission_code` VARCHAR(128)  DEFAULT NULL             COMMENT '权限标识符（如 warehouse:bin:list）',
    `path`            VARCHAR(255)  DEFAULT NULL             COMMENT '前端路由路径',
    `component`       VARCHAR(255)  DEFAULT NULL             COMMENT '前端组件路径',
    `icon`            VARCHAR(128)  DEFAULT NULL             COMMENT '菜单图标',
    `sort_order`      INT           DEFAULT 0                COMMENT '显示排序',
    `visible`         TINYINT       DEFAULT 1                COMMENT '是否可见（0-隐藏 1-显示）',
    `status`          TINYINT       DEFAULT 1                COMMENT '菜单状态（0-停用 1-正常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统菜单权限表';

-- 5. 角色-菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `role_id`         BIGINT        NOT NULL                 COMMENT '角色ID',
    `menu_id`         BIGINT        NOT NULL                 COMMENT '菜单ID',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关联表';

-- 6. 字典类型表
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '字典类型主键ID',
    `dict_type`       VARCHAR(128)  NOT NULL                 COMMENT '字典类型编码（唯一，如 task_abandon_reason）',
    `dict_name`       VARCHAR(128)  NOT NULL                 COMMENT '字典类型名称',
    `status`          TINYINT       DEFAULT 1                COMMENT '状态（0-停用 1-正常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- 7. 字典数据表
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '字典数据主键ID',
    `dict_type`       VARCHAR(128)  NOT NULL                 COMMENT '所属字典类型编码',
    `dict_label`      VARCHAR(128)  NOT NULL                 COMMENT '字典项显示标签',
    `dict_value`      VARCHAR(128)  NOT NULL                 COMMENT '字典项值',
    `sort_order`      INT           DEFAULT 0                COMMENT '显示排序',
    `css_class`       VARCHAR(128)  DEFAULT NULL             COMMENT '前端样式属性',
    `status`          TINYINT       DEFAULT 1                COMMENT '状态（0-停用 1-正常）',
    `is_default`      TINYINT       DEFAULT 0                COMMENT '是否默认（0-否 1-是）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

-- 8. 系统操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '日志主键ID',
    `user_id`         BIGINT        DEFAULT NULL             COMMENT '操作用户ID',
    `username`        VARCHAR(64)   DEFAULT NULL             COMMENT '操作用户名',
    `module`          VARCHAR(64)   DEFAULT NULL             COMMENT '操作模块（如 库位管理、任务管理）',
    `operation`       VARCHAR(64)   DEFAULT NULL             COMMENT '操作类型（如 新增、修改、删除、查询）',
    `method`          VARCHAR(255)  DEFAULT NULL             COMMENT '请求方法（全限定类名.方法名）',
    `request_url`     VARCHAR(512)  DEFAULT NULL             COMMENT '请求URL',
    `request_method`  VARCHAR(10)   DEFAULT NULL             COMMENT 'HTTP请求方式（GET/POST/PUT/DELETE）',
    `request_params`  TEXT          DEFAULT NULL             COMMENT '请求参数（JSON格式）',
    `response_result` TEXT          DEFAULT NULL             COMMENT '响应结果（JSON格式）',
    `ip_address`      VARCHAR(64)   DEFAULT NULL             COMMENT '操作IP地址',
    `user_agent`      VARCHAR(512)  DEFAULT NULL             COMMENT '浏览器UserAgent',
    `execution_time`  INT           DEFAULT NULL             COMMENT '执行耗时（毫秒）',
    `status`          TINYINT       DEFAULT 1                COMMENT '操作状态（0-失败 1-成功）',
    `error_msg`       TEXT          DEFAULT NULL             COMMENT '错误信息（失败时记录）',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统操作日志表';


-- -----------------------------------------------------------
-- 二、仓库空间管理模块（三级空间层级：仓库-区域-货架-货位）
-- -----------------------------------------------------------

-- 9. 仓库表
DROP TABLE IF EXISTS `wms_warehouse`;
CREATE TABLE `wms_warehouse` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '仓库主键ID',
    `warehouse_code`  VARCHAR(32)   NOT NULL                 COMMENT '仓库编码（唯一）',
    `warehouse_name`  VARCHAR(128)  NOT NULL                 COMMENT '仓库名称',
    `address`         VARCHAR(512)  DEFAULT NULL             COMMENT '仓库地址',
    `contact_person`  VARCHAR(64)   DEFAULT NULL             COMMENT '负责人姓名',
    `contact_phone`   VARCHAR(20)   DEFAULT NULL             COMMENT '负责人联系电话',
    `area_size`       DECIMAL(12,2) DEFAULT NULL             COMMENT '仓库面积（平方米）',
    `total_capacity`  INT           DEFAULT NULL             COMMENT '总库位容量（货位总数）',
    `status`          TINYINT       DEFAULT 1                COMMENT '仓库状态（0-停用 1-正常 2-维护中）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库信息表';

-- 10. 区域表（ZONE）
DROP TABLE IF EXISTS `wms_zone`;
CREATE TABLE `wms_zone` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '区域主键ID',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID',
    `zone_code`       VARCHAR(32)   NOT NULL                 COMMENT '区域编码（如 A、B、C）',
    `zone_name`       VARCHAR(128)  NOT NULL                 COMMENT '区域名称（如 A区域）',
    `zone_type`       VARCHAR(32)   DEFAULT NULL             COMMENT '区域类型（如 存储区、暂存区、出库缓冲区）',
    `coordinate_x`    DECIMAL(10,2) DEFAULT NULL             COMMENT '区域左上角X坐标（用于地图渲染）',
    `coordinate_y`    DECIMAL(10,2) DEFAULT NULL             COMMENT '区域左上角Y坐标（用于地图渲染）',
    `width`           DECIMAL(10,2) DEFAULT NULL             COMMENT '区域宽度（米）',
    `height`          DECIMAL(10,2) DEFAULT NULL             COMMENT '区域高度/深度（米）',
    `sort_order`      INT           DEFAULT 0                COMMENT '显示排序',
    `status`          TINYINT       DEFAULT 1                COMMENT '区域状态（0-停用 1-正常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_zone` (`warehouse_id`, `zone_code`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库区域表（空间层级第一级）';

-- 11. 货架表（RACK）
DROP TABLE IF EXISTS `wms_rack`;
CREATE TABLE `wms_rack` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '货架主键ID',
    `zone_id`         BIGINT        NOT NULL                 COMMENT '所属区域ID',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID（冗余字段，便于查询）',
    `rack_code`       VARCHAR(32)   NOT NULL                 COMMENT '货架编码（如 01、02）',
    `rack_name`       VARCHAR(128)  DEFAULT NULL             COMMENT '货架名称',
    `layer_count`     INT           DEFAULT 1                COMMENT '货架层数',
    `coordinate_x`    DECIMAL(10,2) DEFAULT NULL             COMMENT '货架中心X坐标（路径规划锚点）',
    `coordinate_y`    DECIMAL(10,2) DEFAULT NULL             COMMENT '货架中心Y坐标（路径规划锚点）',
    `max_weight`      DECIMAL(10,2) DEFAULT NULL             COMMENT '最大承重（千克）',
    `sort_order`      INT           DEFAULT 0                COMMENT '显示排序',
    `status`          TINYINT       DEFAULT 1                COMMENT '货架状态（0-停用 1-正常 2-维修中）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_zone_rack` (`zone_id`, `rack_code`),
    KEY `idx_zone_id` (`zone_id`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库货架表（空间层级第二级，路径规划坐标锚点）';

-- 12. 货位表（BIN）—— 最小作业单元（停车位模型）
DROP TABLE IF EXISTS `wms_bin`;
CREATE TABLE `wms_bin` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '货位主键ID',
    `rack_id`         BIGINT        NOT NULL                 COMMENT '所属货架ID',
    `zone_id`         BIGINT        NOT NULL                 COMMENT '所属区域ID（冗余字段，便于查询）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID（冗余字段，便于查询）',
    `bin_code`        VARCHAR(32)   NOT NULL                 COMMENT '货位编码（如 A-01-03 表示A区第01货架第03层）',
    `layer_no`        INT           NOT NULL                 COMMENT '所在层号（从1开始）',
    `bin_type`        VARCHAR(32)   DEFAULT 'BIN'            COMMENT '货位类型（BIN-标准货位 FLOOR-地面位 BUFFER-缓冲位）',
    `is_occupied`     TINYINT       DEFAULT 0                COMMENT '是否被占用（0-空闲 1-占用）',
    `current_motorcycle_id` BIGINT  DEFAULT NULL             COMMENT '当前存放的摩托车记录ID（空闲时为NULL）',
    `qr_code`         VARCHAR(255)  DEFAULT NULL             COMMENT '货位二维码标签内容',
    `coordinate_x`    DECIMAL(10,2) DEFAULT NULL             COMMENT '货位X坐标',
    `coordinate_y`    DECIMAL(10,2) DEFAULT NULL             COMMENT '货位Y坐标',
    `coordinate_z`    DECIMAL(10,2) DEFAULT NULL             COMMENT '货位Z坐标（层高）',
    `distance_to_exit` DECIMAL(10,2) DEFAULT NULL            COMMENT '距出库口距离（米，用于智能分配）',
    `max_weight`      DECIMAL(10,2) DEFAULT NULL             COMMENT '最大承重（千克）',
    `sort_order`      INT           DEFAULT 0                COMMENT '显示排序',
    `status`          TINYINT       DEFAULT 1                COMMENT '货位状态（0-停用 1-正常 2-维修中 3-锁定）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bin_code` (`bin_code`),
    KEY `idx_rack_id` (`rack_id`),
    KEY `idx_zone_id` (`zone_id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_is_occupied` (`is_occupied`),
    KEY `idx_bin_type_status` (`bin_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库货位表（空间层级第三级，最小作业单元/停车位）';


-- -----------------------------------------------------------
-- 三、摩托车基础数据模块
-- -----------------------------------------------------------

-- 13. 摩托车型号表
DROP TABLE IF EXISTS `wms_motorcycle_model`;
CREATE TABLE `wms_motorcycle_model` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '型号主键ID',
    `model_code`      VARCHAR(64)   NOT NULL                 COMMENT '型号编码（唯一，SKU编码）',
    `model_name`      VARCHAR(128)  NOT NULL                 COMMENT '型号名称',
    `brand`           VARCHAR(64)   DEFAULT NULL             COMMENT '品牌',
    `category`        VARCHAR(64)   DEFAULT NULL             COMMENT '分类（如 踏板车、街车、越野车）',
    `specification`   VARCHAR(255)  DEFAULT NULL             COMMENT '规格描述（排量、颜色等）',
    `unit_price`      DECIMAL(12,2) DEFAULT NULL             COMMENT '单价（元）',
    `weight`          DECIMAL(10,2) DEFAULT NULL             COMMENT '单车重量（千克）',
    `length`          DECIMAL(10,2) DEFAULT NULL             COMMENT '车身长度（厘米）',
    `width`           DECIMAL(10,2) DEFAULT NULL             COMMENT '车身宽度（厘米）',
    `height`          DECIMAL(10,2) DEFAULT NULL             COMMENT '车身高度（厘米）',
    `safety_stock`    INT           DEFAULT 0                COMMENT '安全库存数量（补货预警阈值）',
    `status`          TINYINT       DEFAULT 1                COMMENT '状态（0-停用 1-正常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_code` (`model_code`),
    KEY `idx_category` (`category`),
    KEY `idx_brand` (`brand`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='摩托车型号表（SKU基础数据）';

-- 14. 摩托车实例表（单车级追踪）
DROP TABLE IF EXISTS `wms_motorcycle`;
CREATE TABLE `wms_motorcycle` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '摩托车实例主键ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '所属型号ID',
    `vin`             VARCHAR(64)   NOT NULL                 COMMENT '车架号/VIN码（唯一标识）',
    `engine_no`       VARCHAR(64)   DEFAULT NULL             COMMENT '发动机号',
    `rfid_tag`        VARCHAR(128)  DEFAULT NULL             COMMENT 'RFID标签编号',
    `barcode`         VARCHAR(128)  DEFAULT NULL             COMMENT '条形码编号',
    `color`           VARCHAR(32)   DEFAULT NULL             COMMENT '车身颜色',
    `production_date` DATE          DEFAULT NULL             COMMENT '生产日期',
    `current_bin_id`  BIGINT        DEFAULT NULL             COMMENT '当前所在货位ID（未入库时为NULL）',
    `warehouse_status` TINYINT      DEFAULT 0                COMMENT '仓储状态（0-未入库 1-已入库 2-已出库 3-移库中）',
    `quality_status`  TINYINT       DEFAULT 1                COMMENT '质量状态（0-异常 1-正常 2-待检）',
    `inbound_time`    DATETIME      DEFAULT NULL             COMMENT '入库时间',
    `outbound_time`   DATETIME      DEFAULT NULL             COMMENT '出库时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_vin` (`vin`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_rfid_tag` (`rfid_tag`),
    KEY `idx_current_bin_id` (`current_bin_id`),
    KEY `idx_warehouse_status` (`warehouse_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='摩托车实例表（单车级追踪，一车一记录）';


-- -----------------------------------------------------------
-- 四、出入库管理模块
-- -----------------------------------------------------------

-- 15. 入库单表
DROP TABLE IF EXISTS `wms_inbound_order`;
CREATE TABLE `wms_inbound_order` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '入库单主键ID',
    `order_no`        VARCHAR(64)   NOT NULL                 COMMENT '入库单号（唯一，系统自动生成）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '目标仓库ID',
    `order_type`      TINYINT       DEFAULT 1                COMMENT '入库类型（1-采购入库 2-退货入库 3-调拨入库 4-其他）',
    `source_no`       VARCHAR(64)   DEFAULT NULL             COMMENT '来源单号（如采购单号、ERP单号）',
    `supplier_name`   VARCHAR(128)  DEFAULT NULL             COMMENT '供应商名称',
    `total_quantity`  INT           DEFAULT 0                COMMENT '计划入库总数量',
    `actual_quantity` INT           DEFAULT 0                COMMENT '实际入库数量',
    `order_status`    TINYINT       DEFAULT 0                COMMENT '单据状态（0-草稿 1-待入库 2-部分入库 3-已完成 4-已取消）',
    `plan_time`       DATETIME      DEFAULT NULL             COMMENT '计划入库时间',
    `actual_time`     DATETIME      DEFAULT NULL             COMMENT '实际完成时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单主表';

-- 16. 入库单明细表
DROP TABLE IF EXISTS `wms_inbound_order_detail`;
CREATE TABLE `wms_inbound_order_detail` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '明细主键ID',
    `order_id`        BIGINT        NOT NULL                 COMMENT '所属入库单ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '摩托车实例ID（扫码确认后关联）',
    `vin`             VARCHAR(64)   DEFAULT NULL             COMMENT '车架号/VIN码',
    `target_bin_id`   BIGINT        DEFAULT NULL             COMMENT '目标货位ID（智能分配后填入）',
    `plan_quantity`   INT           DEFAULT 1                COMMENT '计划数量',
    `actual_quantity` INT           DEFAULT 0                COMMENT '实际入库数量',
    `detail_status`   TINYINT       DEFAULT 0                COMMENT '明细状态（0-待处理 1-已分配库位 2-已入库 3-异常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_motorcycle_id` (`motorcycle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单明细表';

-- 17. 出库单表
DROP TABLE IF EXISTS `wms_outbound_order`;
CREATE TABLE `wms_outbound_order` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '出库单主键ID',
    `order_no`        VARCHAR(64)   NOT NULL                 COMMENT '出库单号（唯一，系统自动生成）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '来源仓库ID',
    `order_type`      TINYINT       DEFAULT 1                COMMENT '出库类型（1-销售出库 2-调拨出库 3-退供出库 4-其他）',
    `source_no`       VARCHAR(64)   DEFAULT NULL             COMMENT '来源单号（如销售订单号、ERP单号）',
    `customer_name`   VARCHAR(128)  DEFAULT NULL             COMMENT '客户名称',
    `total_quantity`  INT           DEFAULT 0                COMMENT '计划出库总数量',
    `actual_quantity` INT           DEFAULT 0                COMMENT '实际出库数量',
    `order_status`    TINYINT       DEFAULT 0                COMMENT '单据状态（0-草稿 1-待出库 2-部分出库 3-已完成 4-已取消）',
    `plan_time`       DATETIME      DEFAULT NULL             COMMENT '计划出库时间',
    `actual_time`     DATETIME      DEFAULT NULL             COMMENT '实际完成时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库单主表';

-- 18. 出库单明细表
DROP TABLE IF EXISTS `wms_outbound_order_detail`;
CREATE TABLE `wms_outbound_order_detail` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '明细主键ID',
    `order_id`        BIGINT        NOT NULL                 COMMENT '所属出库单ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '摩托车实例ID（拣货确认后关联）',
    `vin`             VARCHAR(64)   DEFAULT NULL             COMMENT '车架号/VIN码',
    `source_bin_id`   BIGINT        DEFAULT NULL             COMMENT '来源货位ID',
    `plan_quantity`   INT           DEFAULT 1                COMMENT '计划数量',
    `actual_quantity` INT           DEFAULT 0                COMMENT '实际出库数量',
    `detail_status`   TINYINT       DEFAULT 0                COMMENT '明细状态（0-待处理 1-已分配 2-已出库 3-异常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_motorcycle_id` (`motorcycle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库单明细表';


-- -----------------------------------------------------------
-- 五、工作任务管理模块（系统核心聚合根）
-- -----------------------------------------------------------

-- 19. 工作任务表
DROP TABLE IF EXISTS `wms_work_task`;
CREATE TABLE `wms_work_task` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '任务主键ID',
    `task_no`         VARCHAR(64)   NOT NULL                 COMMENT '任务编号（唯一，系统自动生成）',
    `task_type`       TINYINT       NOT NULL                 COMMENT '任务类型（1-入库 2-出库 3-移库）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID',
    `inbound_order_id`  BIGINT      DEFAULT NULL             COMMENT '关联入库单ID（入库任务时）',
    `outbound_order_id` BIGINT      DEFAULT NULL             COMMENT '关联出库单ID（出库任务时）',
    `model_id`        BIGINT        DEFAULT NULL             COMMENT '目标摩托车型号ID',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '目标摩托车实例ID',
    `vin`             VARCHAR(64)   DEFAULT NULL             COMMENT '目标车架号',
    `source_bin_id`   BIGINT        DEFAULT NULL             COMMENT '源货位ID（出库/移库时）',
    `target_bin_id`   BIGINT        DEFAULT NULL             COMMENT '目标货位ID（入库/移库时）',
    `assigned_user_id` BIGINT       DEFAULT NULL             COMMENT '指派执行人ID（叉车司机/工人）',
    `task_status`     TINYINT       DEFAULT 0                COMMENT '任务状态（0-待分配 1-已分配 2-执行中 3-已完成 4-已放弃 5-异常）',
    `priority`        TINYINT       DEFAULT 5                COMMENT '优先级（1-最高 10-最低，默认5）',
    `wave_id`         BIGINT        DEFAULT NULL             COMMENT '所属波次ID（波次拣货时关联）',
    `assigned_time`   DATETIME      DEFAULT NULL             COMMENT '任务分配时间',
    `start_time`      DATETIME      DEFAULT NULL             COMMENT '任务开始执行时间',
    `complete_time`   DATETIME      DEFAULT NULL             COMMENT '任务完成时间',
    `abandon_reason`  VARCHAR(500)  DEFAULT NULL             COMMENT '放弃原因（放弃任务时必填）',
    `abandon_time`    DATETIME      DEFAULT NULL             COMMENT '放弃时间',
    `last_operator_id` BIGINT       DEFAULT NULL             COMMENT '最后操作人ID（用于追溯）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_no` (`task_no`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_task_status` (`task_status`),
    KEY `idx_assigned_user_id` (`assigned_user_id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_wave_id` (`wave_id`),
    KEY `idx_inbound_order_id` (`inbound_order_id`),
    KEY `idx_outbound_order_id` (`outbound_order_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作任务表（系统核心聚合根，所有出入库/移库操作的唯一入口）';

-- 20. 任务检查点表（双重扫码验证记录）
DROP TABLE IF EXISTS `wms_task_checkpoint`;
CREATE TABLE `wms_task_checkpoint` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '检查点主键ID',
    `task_id`         BIGINT        NOT NULL                 COMMENT '所属工作任务ID',
    `checkpoint_type` TINYINT       NOT NULL                 COMMENT '检查点类型（1-库位扫码 2-车架号扫码 3-RFID识别 4-人工确认）',
    `scan_content`    VARCHAR(255)  NOT NULL                 COMMENT '扫码/识别内容',
    `expected_content` VARCHAR(255) DEFAULT NULL             COMMENT '预期内容（用于校验比对）',
    `verify_result`   TINYINT       NOT NULL                 COMMENT '校验结果（0-失败 1-成功）',
    `operator_id`     BIGINT        NOT NULL                 COMMENT '操作人ID',
    `device_id`       VARCHAR(64)   DEFAULT NULL             COMMENT '操作设备ID（PDA设备编号）',
    `location_info`   VARCHAR(255)  DEFAULT NULL             COMMENT '定位信息（GPS/室内定位坐标）',
    `scan_time`       DATETIME      NOT NULL                 COMMENT '扫码/操作时间',
    `error_msg`       VARCHAR(500)  DEFAULT NULL             COMMENT '校验失败原因',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_scan_time` (`scan_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务检查点表（双重扫码验证记录，不可修改/删除）';


-- -----------------------------------------------------------
-- 六、库存管理模块
-- -----------------------------------------------------------

-- 21. 实时库存表
DROP TABLE IF EXISTS `wms_inventory`;
CREATE TABLE `wms_inventory` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '库存主键ID',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `bin_id`          BIGINT        DEFAULT NULL             COMMENT '货位ID（精确到货位的库存）',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '摩托车实例ID（单车追踪）',
    `quantity`        INT           NOT NULL DEFAULT 0       COMMENT '库存数量（摩托车场景通常为0或1）',
    `frozen_quantity` INT           DEFAULT 0                COMMENT '冻结数量（已分配出库任务但未实际出库）',
    `available_quantity` INT        DEFAULT 0                COMMENT '可用数量（= quantity - frozen_quantity）',
    `last_inbound_time` DATETIME    DEFAULT NULL             COMMENT '最近入库时间',
    `last_outbound_time` DATETIME   DEFAULT NULL             COMMENT '最近出库时间',
    `version`         INT           DEFAULT 0                COMMENT '乐观锁版本号',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bin_motorcycle` (`bin_id`, `motorcycle_id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_bin_id` (`bin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='实时库存表（库存快照）';

-- 22. 库存流水表（不可篡改的审计日志）
DROP TABLE IF EXISTS `wms_inventory_movement`;
CREATE TABLE `wms_inventory_movement` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '流水主键ID',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '摩托车实例ID',
    `vin`             VARCHAR(64)   DEFAULT NULL             COMMENT '车架号（冗余存储便于审计）',
    `movement_type`   TINYINT       NOT NULL                 COMMENT '变动类型（1-入库 2-出库 3-移库转入 4-移库转出 5-盘盈 6-盘亏）',
    `quantity_change` INT           NOT NULL                 COMMENT '变动数量（正数增加，负数减少）',
    `before_quantity` INT           NOT NULL                 COMMENT '变动前库存数量',
    `after_quantity`  INT           NOT NULL                 COMMENT '变动后库存数量',
    `source_bin_id`   BIGINT        DEFAULT NULL             COMMENT '来源货位ID',
    `target_bin_id`   BIGINT        DEFAULT NULL             COMMENT '目标货位ID',
    `task_id`         BIGINT        DEFAULT NULL             COMMENT '关联工作任务ID（强制关联）',
    `order_no`        VARCHAR(64)   DEFAULT NULL             COMMENT '关联单据号（入库单/出库单号）',
    `operator_id`     BIGINT        NOT NULL                 COMMENT '操作人ID',
    `operator_name`   VARCHAR(64)   DEFAULT NULL             COMMENT '操作人姓名（冗余存储便于审计）',
    `movement_time`   DATETIME      NOT NULL                 COMMENT '变动时间（精确到秒）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_motorcycle_id` (`motorcycle_id`),
    KEY `idx_movement_type` (`movement_type`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_movement_time` (`movement_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存流水表（不可篡改审计日志，仅追加不修改不删除）';

-- 23. 盘点任务表
DROP TABLE IF EXISTS `wms_stocktake`;
CREATE TABLE `wms_stocktake` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '盘点任务主键ID',
    `stocktake_no`    VARCHAR(64)   NOT NULL                 COMMENT '盘点单号（唯一）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '盘点仓库ID',
    `stocktake_type`  TINYINT       DEFAULT 1                COMMENT '盘点类型（1-全盘 2-区域盘 3-抽盘）',
    `zone_id`         BIGINT        DEFAULT NULL             COMMENT '盘点区域ID（区域盘时指定）',
    `stocktake_status` TINYINT      DEFAULT 0                COMMENT '盘点状态（0-待执行 1-盘点中 2-待审核 3-已完成 4-已取消）',
    `total_bin_count` INT           DEFAULT 0                COMMENT '应盘库位总数',
    `checked_bin_count` INT         DEFAULT 0                COMMENT '已盘库位数量',
    `profit_count`    INT           DEFAULT 0                COMMENT '盘盈数量',
    `loss_count`      INT           DEFAULT 0                COMMENT '盘亏数量',
    `assigned_user_id` BIGINT       DEFAULT NULL             COMMENT '执行人ID',
    `reviewer_id`     BIGINT        DEFAULT NULL             COMMENT '审核人ID',
    `plan_time`       DATETIME      DEFAULT NULL             COMMENT '计划盘点时间',
    `start_time`      DATETIME      DEFAULT NULL             COMMENT '实际开始时间',
    `complete_time`   DATETIME      DEFAULT NULL             COMMENT '完成时间',
    `review_time`     DATETIME      DEFAULT NULL             COMMENT '审核时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stocktake_no` (`stocktake_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_stocktake_status` (`stocktake_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点任务表';

-- 24. 盘点明细表
DROP TABLE IF EXISTS `wms_stocktake_detail`;
CREATE TABLE `wms_stocktake_detail` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '盘点明细主键ID',
    `stocktake_id`    BIGINT        NOT NULL                 COMMENT '所属盘点任务ID',
    `bin_id`          BIGINT        NOT NULL                 COMMENT '盘点货位ID',
    `bin_code`        VARCHAR(32)   DEFAULT NULL             COMMENT '货位编码（冗余存储）',
    `model_id`        BIGINT        DEFAULT NULL             COMMENT '摩托车型号ID',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '摩托车实例ID',
    `system_quantity` INT           DEFAULT 0                COMMENT '系统账面数量',
    `actual_quantity` INT           DEFAULT 0                COMMENT '实盘数量',
    `difference`      INT           DEFAULT 0                COMMENT '差异数量（= actual - system，正为盘盈，负为盘亏）',
    `diff_type`       TINYINT       DEFAULT 0                COMMENT '差异类型（0-无差异 1-盘盈 2-盘亏）',
    `check_status`    TINYINT       DEFAULT 0                COMMENT '盘点状态（0-未盘 1-已盘 2-复盘）',
    `scan_vin`        VARCHAR(64)   DEFAULT NULL             COMMENT '实际扫描到的车架号',
    `operator_id`     BIGINT        DEFAULT NULL             COMMENT '盘点操作人ID',
    `check_time`      DATETIME      DEFAULT NULL             COMMENT '盘点时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_stocktake_id` (`stocktake_id`),
    KEY `idx_bin_id` (`bin_id`),
    KEY `idx_diff_type` (`diff_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点明细表';


-- -----------------------------------------------------------
-- 七、智能库位分配模块
-- -----------------------------------------------------------

-- 25. 库位分配规则表
DROP TABLE IF EXISTS `wms_allocation_rule`;
CREATE TABLE `wms_allocation_rule` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '规则主键ID',
    `rule_name`       VARCHAR(128)  NOT NULL                 COMMENT '规则名称',
    `rule_code`       VARCHAR(64)   NOT NULL                 COMMENT '规则编码（唯一）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '适用仓库ID',
    `model_id`        BIGINT        DEFAULT NULL             COMMENT '适用摩托车型号ID（NULL表示通用规则）',
    `category`        VARCHAR(64)   DEFAULT NULL             COMMENT '适用摩托车分类（NULL表示所有分类）',
    `target_zone_id`  BIGINT        DEFAULT NULL             COMMENT '目标区域ID',
    `target_rack_id`  BIGINT        DEFAULT NULL             COMMENT '目标货架ID（更精细的规则）',
    `priority`        INT           DEFAULT 0                COMMENT '规则优先级（数值越大优先级越高）',
    `rule_type`       TINYINT       DEFAULT 1                COMMENT '规则类型（1-固定分区 2-周转率优先 3-距离优先 4-均衡分布）',
    `rule_config`     JSON          DEFAULT NULL             COMMENT '规则配置参数（JSON格式，扩展用）',
    `status`          TINYINT       DEFAULT 1                COMMENT '规则状态（0-停用 1-启用）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_model_id` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库位分配规则表（用户自定义分配策略）';

-- 26. 库位推荐记录表
DROP TABLE IF EXISTS `wms_allocation_recommendation`;
CREATE TABLE `wms_allocation_recommendation` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '推荐记录主键ID',
    `task_id`         BIGINT        NOT NULL                 COMMENT '关联工作任务ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `recommended_bin_id` BIGINT     NOT NULL                 COMMENT '推荐货位ID',
    `recommend_rank`  INT           DEFAULT 1                COMMENT '推荐排序（1=最优推荐）',
    `score`           DECIMAL(10,4) DEFAULT NULL             COMMENT '推荐评分（AI算法综合评分）',
    `recommend_source` TINYINT      DEFAULT 1                COMMENT '推荐来源（1-规则引擎 2-AI算法）',
    `turnover_rate`   DECIMAL(10,4) DEFAULT NULL             COMMENT '该型号周转率（算法参考因子）',
    `zone_utilization` DECIMAL(5,2) DEFAULT NULL             COMMENT '目标区域利用率（算法参考因子）',
    `distance_score`  DECIMAL(10,4) DEFAULT NULL             COMMENT '距离评分（算法参考因子）',
    `is_accepted`     TINYINT       DEFAULT NULL             COMMENT '是否被采纳（0-拒绝 1-接受 NULL-待确认）',
    `reject_reason`   VARCHAR(500)  DEFAULT NULL             COMMENT '拒绝原因',
    `operator_id`     BIGINT        DEFAULT NULL             COMMENT '确认人ID',
    `confirm_time`    DATETIME      DEFAULT NULL             COMMENT '确认时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_recommended_bin_id` (`recommended_bin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库位推荐记录表（AI/规则推荐结果及采纳记录）';


-- -----------------------------------------------------------
-- 八、叉车路径规划与波次拣货模块
-- -----------------------------------------------------------

-- 27. 叉车信息表
DROP TABLE IF EXISTS `wms_forklift`;
CREATE TABLE `wms_forklift` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '叉车主键ID',
    `forklift_code`   VARCHAR(32)   NOT NULL                 COMMENT '叉车编号（唯一）',
    `forklift_name`   VARCHAR(64)   DEFAULT NULL             COMMENT '叉车名称',
    `forklift_type`   VARCHAR(32)   DEFAULT NULL             COMMENT '叉车类型（如 电动叉车、柴油叉车）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID',
    `max_load`        DECIMAL(10,2) DEFAULT NULL             COMMENT '最大载重（千克）',
    `current_x`       DECIMAL(10,2) DEFAULT NULL             COMMENT '当前X坐标',
    `current_y`       DECIMAL(10,2) DEFAULT NULL             COMMENT '当前Y坐标',
    `driver_user_id`  BIGINT        DEFAULT NULL             COMMENT '当前驾驶员用户ID',
    `work_status`     TINYINT       DEFAULT 0                COMMENT '工作状态（0-空闲 1-作业中 2-充电中 3-维修中）',
    `status`          TINYINT       DEFAULT 1                COMMENT '叉车状态（0-停用 1-正常 2-报废）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_forklift_code` (`forklift_code`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_driver_user_id` (`driver_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='叉车信息表';

-- 28. 路径规划记录表
DROP TABLE IF EXISTS `wms_path_plan`;
CREATE TABLE `wms_path_plan` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '路径规划主键ID',
    `plan_no`         VARCHAR(64)   NOT NULL                 COMMENT '规划编号（唯一）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '仓库ID',
    `forklift_id`     BIGINT        DEFAULT NULL             COMMENT '执行叉车ID',
    `wave_id`         BIGINT        DEFAULT NULL             COMMENT '关联波次ID',
    `algorithm_type`  VARCHAR(32)   DEFAULT 'A_STAR'         COMMENT '算法类型（A_STAR/TSP_OR_TOOLS/GENETIC）',
    `start_x`         DECIMAL(10,2) DEFAULT NULL             COMMENT '起点X坐标',
    `start_y`         DECIMAL(10,2) DEFAULT NULL             COMMENT '起点Y坐标',
    `waypoints`       JSON          DEFAULT NULL             COMMENT '途经点坐标列表（JSON数组）',
    `path_data`       JSON          DEFAULT NULL             COMMENT '完整路径点序列（JSON数组，用于动画渲染）',
    `smoothed_path`   JSON          DEFAULT NULL             COMMENT '平滑后路径数据（JSON数组）',
    `total_distance`  DECIMAL(12,2) DEFAULT NULL             COMMENT '总行驶距离（米）',
    `estimated_time`  INT           DEFAULT NULL             COMMENT '预计耗时（秒）',
    `actual_time`     INT           DEFAULT NULL             COMMENT '实际耗时（秒）',
    `bin_count`       INT           DEFAULT 0                COMMENT '途经货位数量',
    `plan_status`     TINYINT       DEFAULT 0                COMMENT '规划状态（0-已生成 1-执行中 2-已完成 3-已取消）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_plan_no` (`plan_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_forklift_id` (`forklift_id`),
    KEY `idx_wave_id` (`wave_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='路径规划记录表（TSP/A*算法规划结果）';

-- 29. 波次拣货表
DROP TABLE IF EXISTS `wms_wave`;
CREATE TABLE `wms_wave` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '波次主键ID',
    `wave_no`         VARCHAR(64)   NOT NULL                 COMMENT '波次编号（唯一）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '仓库ID',
    `wave_type`       TINYINT       DEFAULT 1                COMMENT '波次类型（1-出库波次 2-移库波次）',
    `task_count`      INT           DEFAULT 0                COMMENT '包含任务数量',
    `forklift_id`     BIGINT        DEFAULT NULL             COMMENT '指派叉车ID',
    `assigned_user_id` BIGINT       DEFAULT NULL             COMMENT '指派执行人ID',
    `wave_status`     TINYINT       DEFAULT 0                COMMENT '波次状态（0-待执行 1-执行中 2-已完成 3-已取消）',
    `start_time`      DATETIME      DEFAULT NULL             COMMENT '开始执行时间',
    `complete_time`   DATETIME      DEFAULT NULL             COMMENT '完成时间',
    `total_distance`  DECIMAL(12,2) DEFAULT NULL             COMMENT '波次总行驶距离（米）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `is_deleted`      TINYINT       DEFAULT 0                COMMENT '逻辑删除标志（0-未删除 1-已删除）',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wave_no` (`wave_no`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_wave_status` (`wave_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='波次拣货表（聚合多个出库任务减少叉车空跑）';


-- -----------------------------------------------------------
-- 九、动态库存预测与预警模块（AI模块）
-- -----------------------------------------------------------

-- 30. 每日消耗快照表（ETL数据源）
DROP TABLE IF EXISTS `ai_daily_consumption`;
CREATE TABLE `ai_daily_consumption` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '快照主键ID',
    `snapshot_date`   DATE          NOT NULL                 COMMENT '快照日期',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '仓库ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `outbound_qty`    INT           DEFAULT 0                COMMENT '当日出库数量',
    `inbound_qty`     INT           DEFAULT 0                COMMENT '当日入库数量',
    `closing_stock`   INT           DEFAULT 0                COMMENT '日终库存数量',
    `is_promotion`    TINYINT       DEFAULT 0                COMMENT '是否促销日（0-否 1-是，辅助特征）',
    `season_tag`      VARCHAR(16)   DEFAULT NULL             COMMENT '季节标签（spring/summer/autumn/winter）',
    `day_of_week`     TINYINT       DEFAULT NULL             COMMENT '星期几（1-7，辅助特征）',
    `is_cleaned`      TINYINT       DEFAULT 0                COMMENT '数据是否已清洗（0-未清洗 1-已清洗）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_date_warehouse_model` (`snapshot_date`, `warehouse_id`, `model_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_snapshot_date` (`snapshot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='每日消耗快照表（LSTM模型训练数据源，ETL自动采集）';

-- 31. AI预测结果表
DROP TABLE IF EXISTS `ai_prediction_result`;
CREATE TABLE `ai_prediction_result` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '预测结果主键ID',
    `prediction_batch` VARCHAR(64)  NOT NULL                 COMMENT '预测批次号（同一次预测的唯一标识）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '仓库ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `predict_date`    DATE          NOT NULL                 COMMENT '预测目标日期',
    `predicted_demand` DECIMAL(10,2) NOT NULL                COMMENT '预测需求量',
    `lower_bound`     DECIMAL(10,2) DEFAULT NULL             COMMENT '预测置信区间下界',
    `upper_bound`     DECIMAL(10,2) DEFAULT NULL             COMMENT '预测置信区间上界',
    `confidence_level` DECIMAL(5,4) DEFAULT NULL             COMMENT '置信水平（如0.9000表示90%）',
    `model_version`   VARCHAR(64)   DEFAULT NULL             COMMENT '使用的AI模型版本号',
    `algorithm_type`  VARCHAR(32)   DEFAULT 'LSTM'           COMMENT '算法类型（LSTM/PROPHET/RULE_ENGINE）',
    `mape`            DECIMAL(8,4)  DEFAULT NULL             COMMENT '平均绝对百分比误差（模型评估指标）',
    `actual_demand`   DECIMAL(10,2) DEFAULT NULL             COMMENT '实际需求量（事后回填用于评估）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '预测生成时间',
    PRIMARY KEY (`id`),
    KEY `idx_prediction_batch` (`prediction_batch`),
    KEY `idx_warehouse_model` (`warehouse_id`, `model_id`),
    KEY `idx_predict_date` (`predict_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI预测结果表（LSTM需求预测输出）';

-- 32. 补货预警表
DROP TABLE IF EXISTS `ai_replenishment_alert`;
CREATE TABLE `ai_replenishment_alert` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '预警主键ID',
    `alert_no`        VARCHAR(64)   NOT NULL                 COMMENT '预警编号（唯一）',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '仓库ID',
    `model_id`        BIGINT        NOT NULL                 COMMENT '摩托车型号ID',
    `model_name`      VARCHAR(128)  DEFAULT NULL             COMMENT '型号名称（冗余存储便于通知）',
    `current_stock`   INT           NOT NULL                 COMMENT '当前库存数量',
    `predicted_demand` DECIMAL(10,2) NOT NULL                COMMENT '预测未来需求量',
    `safety_stock`    INT           DEFAULT 0                COMMENT '安全库存数量',
    `suggested_qty`   INT           DEFAULT 0                COMMENT '建议补货数量',
    `suggested_date`  DATE          DEFAULT NULL             COMMENT '建议采购时间节点',
    `alert_level`     TINYINT       DEFAULT 1                COMMENT '预警等级（1-提示 2-警告 3-紧急）',
    `alert_status`    TINYINT       DEFAULT 0                COMMENT '处理状态（0-待处理 1-已确认 2-已采购 3-已忽略）',
    `confirmed_by`    BIGINT        DEFAULT NULL             COMMENT '确认人ID',
    `confirmed_time`  DATETIME      DEFAULT NULL             COMMENT '确认时间',
    `prediction_id`   BIGINT        DEFAULT NULL             COMMENT '关联预测结果ID',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '预警生成时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_alert_no` (`alert_no`),
    KEY `idx_warehouse_model` (`warehouse_id`, `model_id`),
    KEY `idx_alert_status` (`alert_status`),
    KEY `idx_alert_level` (`alert_level`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='补货预警表（AI预测驱动的智能补货建议）';

-- 33. AI模型配置表
DROP TABLE IF EXISTS `ai_model_config`;
CREATE TABLE `ai_model_config` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '配置主键ID',
    `model_name`      VARCHAR(64)   NOT NULL                 COMMENT '模型名称（如 LSTM_demand_predict）',
    `model_version`   VARCHAR(32)   NOT NULL                 COMMENT '模型版本号',
    `algorithm_type`  VARCHAR(32)   NOT NULL                 COMMENT '算法类型（LSTM/PROPHET/RULE_ENGINE）',
    `model_file_path` VARCHAR(512)  DEFAULT NULL             COMMENT '模型文件存储路径',
    `input_window`    INT           DEFAULT 30               COMMENT '输入窗口大小（历史天数，如30-90）',
    `output_window`   INT           DEFAULT 7                COMMENT '预测窗口大小（未来天数，如7-14）',
    `hyperparameters` JSON          DEFAULT NULL             COMMENT '超参数配置（JSON格式）',
    `train_data_start` DATE         DEFAULT NULL             COMMENT '训练数据起始日期',
    `train_data_end`  DATE          DEFAULT NULL             COMMENT '训练数据截止日期',
    `train_mape`      DECIMAL(8,4)  DEFAULT NULL             COMMENT '训练集MAPE',
    `test_mape`       DECIMAL(8,4)  DEFAULT NULL             COMMENT '测试集MAPE',
    `last_train_time` DATETIME      DEFAULT NULL             COMMENT '最近一次训练时间',
    `next_train_time` DATETIME      DEFAULT NULL             COMMENT '下次计划训练时间',
    `is_active`       TINYINT       DEFAULT 0                COMMENT '是否为当前激活模型（0-否 1-是）',
    `status`          TINYINT       DEFAULT 1                COMMENT '状态（0-停用 1-正常）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '创建人',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)   DEFAULT NULL             COMMENT '更新人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name_version` (`model_name`, `model_version`),
    KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI模型配置表（LSTM等模型的版本与参数管理）';


-- -----------------------------------------------------------
-- 十、智能识别模块（RFID/条码）
-- -----------------------------------------------------------

-- 34. RFID扫描记录表
DROP TABLE IF EXISTS `wms_rfid_scan_record`;
CREATE TABLE `wms_rfid_scan_record` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '扫描记录主键ID',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '仓库ID',
    `device_id`       VARCHAR(64)   DEFAULT NULL             COMMENT '扫描设备ID/编号',
    `device_type`     TINYINT       DEFAULT 1                COMMENT '设备类型（1-固定扫描器 2-手持终端 3-PDA）',
    `scan_type`       TINYINT       DEFAULT 1                COMMENT '扫描类型（1-入库扫描 2-盘点扫描 3-位置核查 4-出库扫描）',
    `rfid_tag`        VARCHAR(128)  DEFAULT NULL             COMMENT 'RFID标签值',
    `barcode`         VARCHAR(128)  DEFAULT NULL             COMMENT '条形码值',
    `qr_code`         VARCHAR(255)  DEFAULT NULL             COMMENT '二维码值',
    `motorcycle_id`   BIGINT        DEFAULT NULL             COMMENT '识别到的摩托车ID',
    `model_id`        BIGINT        DEFAULT NULL             COMMENT '识别到的摩托车型号ID',
    `bin_id`          BIGINT        DEFAULT NULL             COMMENT '扫描位置货位ID',
    `is_position_match` TINYINT     DEFAULT NULL             COMMENT '位置是否匹配（0-不匹配 1-匹配 NULL-未校验）',
    `is_alarm`        TINYINT       DEFAULT 0                COMMENT '是否触发告警（0-否 1-是）',
    `alarm_reason`    VARCHAR(255)  DEFAULT NULL             COMMENT '告警原因',
    `operator_id`     BIGINT        DEFAULT NULL             COMMENT '操作人ID',
    `scan_time`       DATETIME      NOT NULL                 COMMENT '扫描时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_motorcycle_id` (`motorcycle_id`),
    KEY `idx_rfid_tag` (`rfid_tag`),
    KEY `idx_scan_time` (`scan_time`),
    KEY `idx_is_alarm` (`is_alarm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='RFID/条码扫描记录表（智能识别模块数据采集）';


-- -----------------------------------------------------------
-- 十一、仓库地图网格表（叉车路径规划环境建模）
-- -----------------------------------------------------------

-- 35. 仓库网格地图表
DROP TABLE IF EXISTS `wms_grid_map`;
CREATE TABLE `wms_grid_map` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '网格主键ID',
    `warehouse_id`    BIGINT        NOT NULL                 COMMENT '所属仓库ID',
    `grid_x`          INT           NOT NULL                 COMMENT '网格X坐标（列号）',
    `grid_y`          INT           NOT NULL                 COMMENT '网格Y坐标（行号）',
    `cell_type`       TINYINT       DEFAULT 0                COMMENT '格子类型（0-可通行 1-障碍物/货架 2-禁行区 3-出库口 4-入库口）',
    `bind_rack_id`    BIGINT        DEFAULT NULL             COMMENT '关联货架ID（货架占用的格子）',
    `bind_bin_id`     BIGINT        DEFAULT NULL             COMMENT '关联货位ID',
    `is_passable`     TINYINT       DEFAULT 1                COMMENT '是否可通行（0-不可通行 1-可通行）',
    `weight`          DECIMAL(5,2)  DEFAULT 1.00             COMMENT '通行权重/代价（用于路径规划算法）',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_grid` (`warehouse_id`, `grid_x`, `grid_y`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_cell_type` (`cell_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库网格地图表（A*路径规划环境建模）';


-- -----------------------------------------------------------
-- 十二、系统通知与消息表
-- -----------------------------------------------------------

-- 36. 系统通知表
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '通知主键ID',
    `title`           VARCHAR(255)  NOT NULL                 COMMENT '通知标题',
    `content`         TEXT          NOT NULL                 COMMENT '通知内容',
    `notify_type`     TINYINT       DEFAULT 1                COMMENT '通知类型（1-系统通知 2-补货预警 3-盘点差异 4-任务异常 5-位置告警）',
    `source_type`     VARCHAR(64)   DEFAULT NULL             COMMENT '来源类型（如 ALERT/STOCKTAKE/TASK）',
    `source_id`       BIGINT        DEFAULT NULL             COMMENT '来源ID（关联预警/盘点/任务等记录）',
    `target_user_id`  BIGINT        DEFAULT NULL             COMMENT '目标用户ID（NULL表示全员通知）',
    `target_role_code` VARCHAR(64)  DEFAULT NULL             COMMENT '目标角色编码（按角色推送）',
    `is_read`         TINYINT       DEFAULT 0                COMMENT '是否已读（0-未读 1-已读）',
    `read_time`       DATETIME      DEFAULT NULL             COMMENT '阅读时间',
    `remark`          VARCHAR(500)  DEFAULT NULL             COMMENT '备注信息',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_target_user_id` (`target_user_id`),
    KEY `idx_notify_type` (`notify_type`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统通知表（预警推送、任务通知等）';
