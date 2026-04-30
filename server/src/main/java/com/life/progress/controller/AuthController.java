package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.User;
import com.life.progress.service.UserService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("role", user.getRole());
        result.put("birthDate", user.getBirthDate());

        return Result.success("登录成功", result);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        if (userService.isUsernameExists(user.getUsername())) {
            return Result.error("用户名已存在");
        }

        if (user.getEmail() != null && userService.isEmailExists(user.getEmail())) {
            return Result.error("邮箱已被注册");
        }

        User registeredUser = userService.register(user);
        return Result.success("注册成功", registeredUser);
    }

    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        return Result.success(exists);
    }

    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return Result.success(exists);
    }
}
