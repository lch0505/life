package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.User;
import com.life.progress.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dev")
public class DevController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/reset-password")
    public Result<String> resetPassword(
            @RequestParam String username,
            @RequestParam String newPassword) {

        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("密码长度至少6位");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userMapper.update(user);

        String generatedHash = passwordEncoder.encode(newPassword);

        return Result.success("密码重置成功！\n\n" +
                "用户名: " + username + "\n" +
                "新密码: " + newPassword + "\n" +
                "BCrypt哈希: " + generatedHash + "\n\n" +
                "请使用新密码登录系统。");
    }

    @PostMapping("/init-admin")
    public Result<String> initAdmin(
            @RequestParam(defaultValue = "admin") String username,
            @RequestParam(defaultValue = "admin123") String password) {

        User existingUser = userMapper.selectByUsername(username);
        String encodedPassword = passwordEncoder.encode(password);

        if (existingUser == null) {
            User newAdmin = new User();
            newAdmin.setUsername(username);
            newAdmin.setPassword(encodedPassword);
            newAdmin.setNickname("超级管理员");
            newAdmin.setRole(0);
            newAdmin.setStatus(1);
            userMapper.insert(newAdmin);
        } else {
            existingUser.setPassword(encodedPassword);
            existingUser.setNickname("超级管理员");
            existingUser.setRole(0);
            existingUser.setStatus(1);
            userMapper.update(existingUser);
        }

        return Result.success("管理员账户已初始化！\n\n" +
                "用户名: " + username + "\n" +
                "密码: " + password + "\n" +
                "BCrypt哈希: " + encodedPassword + "\n\n" +
                "请使用以上凭据登录系统。");
    }

    @GetMapping("/encode-password")
    public Result<String> encodePassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        return Result.success("原始密码: " + password + "\nBCrypt哈希: " + encoded);
    }

    @GetMapping("/test-password")
    public Result<String> testPassword(
            @RequestParam String rawPassword,
            @RequestParam String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        return Result.success("验证结果: " + matches +
                "\n原始密码: " + rawPassword +
                "\n哈希值: " + encodedPassword);
    }
}
