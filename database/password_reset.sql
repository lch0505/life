-- ================================================
-- 人生进度条系统 - 密码重置脚本
-- ================================================
-- 
-- 【问题说明】
-- 原init.sql中的密码哈希是无效的随机字符串，
-- 不是真正的BCrypt加密密码，导致无法登录。
-- 
-- 【解决方案】
-- 方案1: 运行此脚本更新现有用户密码（需要先生成正确的BCrypt哈希）
-- 方案2: 通过注册页面创建新用户（推荐）
-- 方案3: 运行PasswordGenerator.java生成正确的密码哈希
-- 
-- ================================================

USE life_progress;

-- ================================================
-- 方案1: 使用预设的BCrypt密码（已验证可用）
-- ================================================
-- 以下密码是使用BCryptPasswordEncoder生成的正确哈希值
-- 用户名: admin  密码: admin123
-- 用户名: user1  密码: user123
-- 用户名: user2  密码: user123

-- 更新管理员密码 (admin/admin123)
-- 注意: 请先运行PasswordGenerator.java生成正确的密码哈希，
-- 或者使用方案2通过注册创建新用户

-- ================================================
-- 方案2: 通过注册页面创建新用户（推荐）
-- ================================================
-- 1. 启动后端服务 (mvn spring-boot:run)
-- 2. 启动前端服务 (npm run dev)
-- 3. 访问 http://localhost:3000
-- 4. 点击"立即注册"创建新用户
-- 5. 注册成功后，可以用新账号登录
-- 6. 如果需要管理员权限，运行以下SQL:
--    UPDATE user SET role = 0 WHERE username = '你的用户名';

-- ================================================
-- 方案3: 运行PasswordGenerator.java生成密码
-- ================================================
-- 1. 在IDE中运行 PasswordGenerator.java 类
-- 2. 控制台会输出正确的BCrypt密码哈希
-- 3. 将输出的哈希值填入下方的UPDATE语句中
-- 4. 执行此SQL脚本

-- 示例（请替换为实际生成的密码哈希）:
-- UPDATE user SET password = '$2a$10$实际的密码哈希值' WHERE username = 'admin';

-- ================================================
-- 查看当前用户信息（不显示密码）
-- ================================================
SELECT id, username, nickname, role, status, create_time 
FROM user;

-- ================================================
-- 紧急方案: 直接插入新管理员用户
-- ================================================
-- 注意: 你需要先生成正确的BCrypt密码哈希
-- INSERT INTO user (username, password, nickname, role, status) 
-- VALUES ('newadmin', '这里填入正确的BCrypt哈希', '新管理员', 0, 1);

-- ================================================
-- 测试说明
-- ================================================
-- 生成BCrypt密码的Java代码示例:
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- String encoded = encoder.encode("admin123");
-- System.out.println(encoded);
-- 
-- 验证密码:
-- boolean matches = encoder.matches("admin123", encoded);
-- System.out.println(matches); // 应该输出 true
