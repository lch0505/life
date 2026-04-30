package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.User;
import com.life.progress.service.UserService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        User user = userService.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<User> updateUserInfo(
            @RequestBody User user,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        User existing = userService.findById(userId);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }

        user.setId(userId);
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);

        boolean success = userService.update(user);
        if (success) {
            User updated = userService.findById(userId);
            updated.setPassword(null);
            return Result.success("更新成功", updated);
        } else {
            return Result.error("更新失败");
        }
    }

    @PutMapping("/update-birthdate")
    public Result<User> updateBirthdate(
            @RequestParam String birthDate,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        User user = userService.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        user.setBirthDate(LocalDate.parse(birthDate));
        boolean success = userService.update(user);

        if (success) {
            User updated = userService.findById(userId);
            updated.setPassword(null);
            return Result.success("生日更新成功", updated);
        } else {
            return Result.error("更新失败");
        }
    }

    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<User>> getUserList(@RequestParam(required = false) Integer role) {
        List<User> users;
        if (role != null) {
            users = userService.findByRole(role);
        } else {
            users = userService.findAll();
        }

        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        if (userService.isUsernameExists(user.getUsername())) {
            return Result.error("用户名已存在");
        }

        User registeredUser = userService.register(user);
        registeredUser.setPassword(null);
        return Result.success("创建成功", registeredUser);
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.findById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }

        user.setId(id);
        if (user.getPassword() != null) {
            user.setPassword(null);
        }

        boolean success = userService.update(user);
        if (success) {
            User updated = userService.findById(id);
            updated.setPassword(null);
            return Result.success("更新成功", updated);
        } else {
            return Result.error("更新失败");
        }
    }

    @PutMapping("/admin/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User existing = userService.findById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }

        boolean success = userService.updateStatus(id, status);
        if (success) {
            return Result.success(status == 1 ? "已启用" : "已禁用", null);
        } else {
            return Result.error("操作失败");
        }
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteUser(@PathVariable Long id) {
        User existing = userService.findById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }

        boolean success = userService.deleteById(id);
        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getStats() {
        List<User> allUsers = userService.findAll();
        List<User> admins = userService.findByRole(0);
        List<User> normalUsers = userService.findByRole(1);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("adminCount", admins.size());
        stats.put("normalUserCount", normalUsers.size());

        return Result.success(stats);
    }

    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = token.substring(7);
        if (jwtUtil.validateToken(jwtToken)) {
            return jwtUtil.getUserIdFromToken(jwtToken);
        }
        return null;
    }
}
