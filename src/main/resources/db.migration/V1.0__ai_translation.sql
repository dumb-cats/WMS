-- 建表语句
CREATE TABLE users (
                       user_id VARCHAR(36) NOT NULL COMMENT '用户唯一标识符 (UUID)',
                       username VARCHAR(255) COMMENT '用户名',
                       password VARCHAR(255) COMMENT '加密后的密码',
                       name VARCHAR(255) COMMENT '姓名',
                       gender TINYINT COMMENT '性别',
                       id_card_number VARCHAR(18) COMMENT '身份证号码',
                       phone_number VARCHAR(11) COMMENT '手机号码',
                       email VARCHAR(255) COMMENT '邮箱地址',
                       address VARCHAR(500) COMMENT '家庭住址',
                       date_of_birth DATE COMMENT '出生日期',
                       is_verified TINYINT DEFAULT 0 COMMENT '是否已验证身份',
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       role_id INT COMMENT '权限',
                       PRIMARY KEY (user_id),
                       UNIQUE KEY uk_username (username),
                       UNIQUE KEY uk_id_card_number (id_card_number),
                       UNIQUE KEY uk_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';