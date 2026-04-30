-- 人生进度条系统数据库初始化脚本
-- 数据库名称: life_progress
-- 创建日期: 2026-04-30

-- 创建数据库
CREATE DATABASE IF NOT EXISTS life_progress DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE life_progress;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `birth_date` DATE COMMENT '出生日期',
    `role` TINYINT DEFAULT 1 COMMENT '角色：0-超级管理员，1-普通用户',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 倒计时表
CREATE TABLE IF NOT EXISTS `countdown` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '倒计时ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '倒计时标题',
    `description` VARCHAR(500) COMMENT '描述',
    `category` VARCHAR(50) NOT NULL COMMENT '分类：婚礼、旅行、考试、纪念日、发薪日、其他',
    `target_date` DATE NOT NULL COMMENT '目标日期',
    `target_time` TIME COMMENT '目标时间',
    `color` VARCHAR(20) DEFAULT '#1890ff' COMMENT '主题颜色',
    `icon` VARCHAR(50) COMMENT '图标',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已过期，1-进行中，2-已完成',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_target_date` (`target_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='倒计时表';

-- 年度目标表
CREATE TABLE IF NOT EXISTS `annual_goal` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '目标ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `year` INT NOT NULL COMMENT '年度',
    `title` VARCHAR(200) NOT NULL COMMENT '目标标题',
    `description` TEXT COMMENT '目标描述',
    `progress` DECIMAL(5,2) DEFAULT 0.00 COMMENT '进度百分比',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已完成，3-已取消',
    `start_date` DATE COMMENT '开始日期',
    `end_date` DATE COMMENT '结束日期',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级：1-低，2-中，3-高',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_year` (`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年度目标表';

-- 目标拆解任务表
CREATE TABLE IF NOT EXISTS `goal_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    `goal_id` BIGINT NOT NULL COMMENT '目标ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `progress` DECIMAL(5,2) DEFAULT 0.00 COMMENT '任务进度百分比',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已完成，3-已取消',
    `deadline` DATE COMMENT '截止日期',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_goal_id` (`goal_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目标拆解任务表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(500) COMMENT '配置描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认超级管理员账号 (用户名: admin, 密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$EqfY2qGQvW8Pydj3K4zU9O9r7d7j5d4k3j2h1g0f9e8d7c6b5a4s3d2f1g', '超级管理员', 0, 1);

-- 插入系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('site.name', '人生进度条（地球online）', '网站名称'),
('site.description', '记录你的人生，规划你的未来', '网站描述'),
('life.expectancy', '80', '预期寿命（年）');

-- 插入测试用户（普通用户）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `phone`, `birth_date`, `role`, `status`) VALUES
('user1', '$2a$10$EqfY2qGQvW8Pydj3K4zU9O9r7d7j5d4k3j2h1g0f9e8d7c6b5a4s3d2f1g', '张三', 'zhangsan@example.com', '13800138001', '1990-01-15', 1, 1),
('user2', '$2a$10$EqfY2qGQvW8Pydj3K4zU9O9r7d7j5d4k3j2h1g0f9e8d7c6b5a4s3d2f1g', '李四', 'lisi@example.com', '13800138002', '1995-06-20', 1, 1);
