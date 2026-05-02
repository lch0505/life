-- 随机人生事件模块数据库初始化脚本
-- 创建日期: 2026-05-02

USE life_progress;

-- 随机事件配置表
CREATE TABLE IF NOT EXISTS `life_event_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事件配置ID',
    `event_type` TINYINT NOT NULL COMMENT '事件类型：1-好运buff，2-小倒霉，3-意外惊喜',
    `event_name` VARCHAR(100) NOT NULL COMMENT '事件名称',
    `event_description` VARCHAR(500) NOT NULL COMMENT '事件描述',
    `icon` VARCHAR(50) DEFAULT '🎲' COMMENT '事件图标',
    `color` VARCHAR(20) DEFAULT '#409EFF' COMMENT '事件颜色',
    `impact_type` VARCHAR(20) NOT NULL COMMENT '影响类型：appearance-颜值, intelligence-智商, wealth-财运, health-健康, emotional-情商, luck-运气',
    `impact_value` INT NOT NULL COMMENT '影响数值（正数或负数）',
    `weight` INT DEFAULT 10 COMMENT '权重值，影响随机概率',
    `min_days` INT DEFAULT 0 COMMENT '最小触发间隔（天）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_event_type` (`event_type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随机事件配置表';

-- 用户每日事件记录表
CREATE TABLE IF NOT EXISTS `user_daily_event` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `event_id` BIGINT NOT NULL COMMENT '事件配置ID',
    `event_date` DATE NOT NULL COMMENT '事件日期',
    `event_type` TINYINT NOT NULL COMMENT '事件类型：1-好运buff，2-小倒霉，3-意外惊喜',
    `event_name` VARCHAR(100) NOT NULL COMMENT '事件名称（快照）',
    `event_description` VARCHAR(500) NOT NULL COMMENT '事件描述（快照）',
    `impact_type` VARCHAR(20) NOT NULL COMMENT '影响类型',
    `impact_value` INT NOT NULL COMMENT '影响数值',
    `is_applied` TINYINT DEFAULT 0 COMMENT '是否已应用到用户状态：0-否，1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uniq_user_date` (`user_id`, `event_date`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_event_date` (`event_date`),
    INDEX `idx_event_type` (`event_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日事件记录表';

-- 插入随机事件配置数据
-- 好运buff (event_type=1)
INSERT INTO `life_event_config` (`event_type`, `event_name`, `event_description`, `icon`, `color`, `impact_type`, `impact_value`, `weight`) VALUES
(1, '偶遇贵人', '今天你遇到了一位贵人，他对你的事业发展提供了宝贵的建议和帮助。', '👔', '#67c23a', 'intelligence', 2, 15),
(1, '意外收获', '你在整理旧物品时发现了一些有价值的东西，或者收到了一笔意外的款项。', '💰', '#67c23a', 'wealth', 2, 15),
(1, '身体倍儿棒', '今天你感觉精力充沛，身体健康状况极佳，做什么都很有活力。', '💪', '#67c23a', 'health', 2, 15),
(1, '魅力四射', '今天你的魅力值爆棚，无论走到哪里都能给人留下美好的印象。', '✨', '#67c23a', 'appearance', 2, 15),
(1, '心情愉悦', '今天你的心情特别好，情绪稳定，与周围的人相处得非常融洽。', '😊', '#67c23a', 'emotional', 2, 15),
(1, '幸运星高照', '今天你的运气特别好，做什么事情都感觉一帆风顺，事半功倍。', '🍀', '#67c23a', 'luck', 2, 10),
(1, '灵感迸发', '今天你的思维特别活跃，灵感不断，解决问题的能力大幅提升。', '💡', '#67c23a', 'intelligence', 3, 8),
(1, '桃花运旺', '今天你特别有吸引力，可能会遇到心仪的对象或者收到他人的表白。', '💕', '#67c23a', 'appearance', 3, 8),
(1, '投资理财顺', '今天你的投资眼光特别准，或者收到了理财收益，财富有所增长。', '📈', '#67c23a', 'wealth', 3, 8),
(1, '身心健康', '今天你身心俱佳，不仅身体感觉良好，心情也特别舒畅。', '🌞', '#67c23a', 'health', 3, 8);

-- 小倒霉 (event_type=2)
INSERT INTO `life_event_config` (`event_type`, `event_name`, `event_description`, `icon`, `color`, `impact_type`, `impact_value`, `weight`) VALUES
(2, '工作受挫', '今天工作上遇到了一些挫折，可能是项目进展不顺利或者与同事发生了小摩擦。', '😓', '#f56c6c', 'intelligence', -1, 15),
(2, '破财消灾', '今天可能会有一些意外的支出，比如东西损坏需要维修或更换。', '💸', '#f56c6c', 'wealth', -1, 15),
(2, '身体不适', '今天你的身体状态不太好，可能会感到疲劳、头痛或者其他小毛病。', '🤒', '#f56c6c', 'health', -1, 15),
(2, '状态不佳', '今天你的精神状态不太好，可能会影响你的外在表现和社交互动。', '😴', '#f56c6c', 'appearance', -1, 15),
(2, '情绪低落', '今天你的心情不太好，可能会因为一些小事而感到沮丧或烦躁。', '😔', '#f56c6c', 'emotional', -1, 15),
(2, '运气欠佳', '今天你的运气不太好，可能会遇到一些小意外或者事情进展不顺利。', '🍂', '#f56c6c', 'luck', -1, 10),
(2, '思维卡顿', '今天你的思维不太活跃，可能会遇到思路不清或者决策困难的情况。', '🤔', '#f56c6c', 'intelligence', -2, 8),
(2, '社交尴尬', '今天可能会在社交场合遇到一些尴尬的情况，影响你的社交表现。', '😅', '#f56c6c', 'appearance', -2, 8),
(2, '投资失利', '今天你的投资可能会有一些亏损，或者理财收益不如预期。', '📉', '#f56c6c', 'wealth', -2, 8),
(2, '健康亮灯', '今天你的健康状况需要关注，可能会有一些明显的不适症状。', '🏥', '#f56c6c', 'health', -2, 8);

-- 意外惊喜 (event_type=3)
INSERT INTO `life_event_config` (`event_type`, `event_name`, `event_description`, `icon`, `color`, `impact_type`, `impact_value`, `weight`) VALUES
(3, '天降横财', '你获得了一笔完全意外的财富！可能是中奖、继承、或者是别人的馈赠。', '🎰', '#e6a23c', 'wealth', 5, 3),
(3, '才华爆发', '今天你展现出了惊人的才华，在某个领域取得了突破性的成就。', '🏆', '#e6a23c', 'intelligence', 5, 3),
(3, '完美邂逅', '你遇到了一个让你心动的人，或者与某个重要的人重逢，这可能改变你的人生轨迹。', '💫', '#e6a23c', 'emotional', 5, 3),
(3, '活力爆表', '今天你的身体状态好到爆炸，感觉自己能完成任何挑战！', '⚡', '#e6a23c', 'health', 5, 3),
(3, '颜值巅峰', '今天你的颜值达到了人生巅峰，无论走到哪里都是众人瞩目的焦点。', '👑', '#e6a23c', 'appearance', 5, 3),
(3, '锦鲤附体', '今天你的运气好到爆棚！任何事情都能逢凶化吉，甚至可能遇到一生难求的机遇。', '🐟', '#e6a23c', 'luck', 5, 2),
(3, '双重惊喜', '好事成双！今天你同时遇到了两件让你开心的事情，心情大好。', '🎊', '#e6a23c', 'luck', 4, 4),
(3, '贵人相助', '你遇到了一位真正的贵人，他不仅给你建议，还直接提供了实质性的帮助。', '🤝', '#e6a23c', 'intelligence', 4, 4),
(3, '财富自由第一步', '你获得了一笔可观的收入或者投资回报，离财务自由又近了一步。', '🏦', '#e6a23c', 'wealth', 4, 4),
(3, '爱情降临', '丘比特之箭射中了你！今天你可能会遇到真爱，或者与现任的感情突飞猛进。', '🌹', '#e6a23c', 'emotional', 4, 4);
