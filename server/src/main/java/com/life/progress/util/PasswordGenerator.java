package com.life.progress.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        String adminEncoded = encoder.encode(adminPassword);
        String userEncoded = encoder.encode(userPassword);
        
        System.out.println("==================== 密码生成结果 ====================");
        System.out.println("原始密码 [admin123] 的BCrypt加密值:");
        System.out.println(adminEncoded);
        System.out.println();
        System.out.println("原始密码 [user123] 的BCrypt加密值:");
        System.out.println(userEncoded);
        System.out.println();
        System.out.println("==================== 验证测试 ====================");
        System.out.println("admin123 验证结果: " + encoder.matches(adminPassword, adminEncoded));
        System.out.println("user123 验证结果: " + encoder.matches(userPassword, userEncoded));
        System.out.println("==================================================");
        System.out.println();
        System.out.println("请将以下SQL更新到数据库中:");
        System.out.println();
        System.out.println("-- 更新管理员密码 (admin/admin123)");
        System.out.println("UPDATE user SET password = '" + adminEncoded + "' WHERE username = 'admin';");
        System.out.println();
        System.out.println("-- 更新测试用户密码 (user1/user123)");
        System.out.println("UPDATE user SET password = '" + userEncoded + "' WHERE username = 'user1';");
        System.out.println("UPDATE user SET password = '" + userEncoded + "' WHERE username = 'user2';");
    }
}
