-- 天赋加点系统数据库初始化脚本
-- 创建日期: 2026-05-02

USE life_progress;

-- 用户天赋表
CREATE TABLE IF NOT EXISTS `user_talent` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '天赋ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `appearance` INT DEFAULT 0 COMMENT '颜值',
    `intelligence` INT DEFAULT 0 COMMENT '智商',
    `wealth` INT DEFAULT 0 COMMENT '财运',
    `health` INT DEFAULT 0 COMMENT '健康',
    `emotional` INT DEFAULT 0 COMMENT '情商',
    `luck` INT DEFAULT 0 COMMENT '运气',
    `total_points_used` INT DEFAULT 0 COMMENT '已使用点数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_user_id` (`user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户天赋表';

-- 天赋系统配置表
CREATE TABLE IF NOT EXISTS `talent_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(500) COMMENT '配置描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='天赋系统配置表';

-- 天赋模拟日志表
CREATE TABLE IF NOT EXISTS `talent_simulation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `simulation_type` VARCHAR(50) NOT NULL COMMENT '模拟类型：career-事业, relationship-感情, health-健康, wealth-财富, overall-综合',
    `simulation_result` TEXT NOT NULL COMMENT '模拟结果（JSON格式）',
    `talent_snapshot` TEXT NOT NULL COMMENT '天赋快照（JSON格式）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_simulation_type` (`simulation_type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='天赋模拟日志表';

-- 插入默认天赋配置
INSERT INTO `talent_config` (`config_key`, `config_value`, `description`) VALUES
('total_points', '20', '初始总天赋点数'),
('max_per_talent', '10', '单天赋最大点数'),
('min_per_talent', '0', '单天赋最小点数'),
('appearance_description', '颜值影响你的社交吸引力和第一印象', '颜值描述'),
('intelligence_description', '智商影响你的学习能力和问题解决能力', '智商描述'),
('wealth_description', '财运影响你的收入和投资回报率', '财运描述'),
('health_description', '健康影响你的寿命和生活质量', '健康描述'),
('emotional_description', '情商影响你的人际关系和情绪管理', '情商描述'),
('luck_description', '运气影响你遇到意外好事的概率', '运气描述');

-- 为现有用户初始化天赋数据
INSERT INTO `user_talent` (`user_id`, `appearance`, `intelligence`, `wealth`, `health`, `emotional`, `luck`, `total_points_used`)
SELECT `id`, 0, 0, 0, 0, 0, 0, 0 FROM `user` 
WHERE `id` NOT IN (SELECT `user_id` FROM `user_talent`);
