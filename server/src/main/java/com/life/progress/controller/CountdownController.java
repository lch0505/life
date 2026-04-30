package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.Countdown;
import com.life.progress.service.CountdownService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/countdown")
public class CountdownController {

    @Autowired
    private CountdownService countdownService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<List<Countdown>> getList(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String category) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        List<Countdown> countdowns;
        if (category != null && !category.isEmpty()) {
            countdowns = countdownService.findByUserIdAndCategory(userId, category);
        } else {
            countdowns = countdownService.findByUserId(userId);
        }

        return Result.success(countdowns);
    }

    @GetMapping("/list-with-remaining")
    public Result<List<Map<String, Object>>> getListWithRemaining(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String category) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        List<Countdown> countdowns;
        if (category != null && !category.isEmpty()) {
            countdowns = countdownService.findByUserIdAndCategory(userId, category);
        } else {
            countdowns = countdownService.findByUserId(userId);
        }

        List<Map<String, Object>> result = countdowns.stream()
                .map(this::addRemainingTime)
                .toList();

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Countdown countdown = countdownService.findById(id);
        if (countdown == null) {
            return Result.error(404, "倒计时不存在");
        }

        if (!countdown.getUserId().equals(userId)) {
            return Result.error(403, "无权限访问");
        }

        return Result.success(addRemainingTime(countdown));
    }

    @PostMapping("/create")
    public Result<Countdown> create(
            @RequestBody Countdown countdown,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        countdown.setUserId(userId);
        boolean success = countdownService.create(countdown);

        if (success) {
            return Result.success("创建成功", countdown);
        } else {
            return Result.error("创建失败");
        }
    }

    @PutMapping("/update/{id}")
    public Result<Countdown> update(
            @PathVariable Long id,
            @RequestBody Countdown countdown,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Countdown existing = countdownService.findById(id);
        if (existing == null) {
            return Result.error(404, "倒计时不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改");
        }

        countdown.setId(id);
        countdown.setUserId(userId);
        boolean success = countdownService.update(countdown);

        if (success) {
            return Result.success("更新成功", countdown);
        } else {
            return Result.error("更新失败");
        }
    }

    @PutMapping("/top/{id}")
    public Result<String> updateTop(
            @PathVariable Long id,
            @RequestParam Integer isTop,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Countdown existing = countdownService.findById(id);
        if (existing == null) {
            return Result.error(404, "倒计时不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改");
        }

        boolean success = countdownService.updateTop(id, isTop);

        if (success) {
            return Result.success(isTop == 1 ? "已置顶" : "已取消置顶", null);
        } else {
            return Result.error("操作失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Countdown existing = countdownService.findById(id);
        if (existing == null) {
            return Result.error(404, "倒计时不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除");
        }

        boolean success = countdownService.deleteById(id);

        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = List.of("婚礼", "旅行", "考试", "纪念日", "发薪日", "其他");
        return Result.success(categories);
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

    private Map<String, Object> addRemainingTime(Countdown countdown) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", countdown.getId());
        result.put("userId", countdown.getUserId());
        result.put("title", countdown.getTitle());
        result.put("description", countdown.getDescription());
        result.put("category", countdown.getCategory());
        result.put("targetDate", countdown.getTargetDate());
        result.put("targetTime", countdown.getTargetTime());
        result.put("color", countdown.getColor());
        result.put("icon", countdown.getIcon());
        result.put("isTop", countdown.getIsTop());
        result.put("status", countdown.getStatus());
        result.put("createTime", countdown.getCreateTime());
        result.put("updateTime", countdown.getUpdateTime());

        LocalDate targetDate = countdown.getTargetDate();
        LocalTime targetTime = countdown.getTargetTime();
        if (targetTime == null) {
            targetTime = LocalTime.MIDNIGHT;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = LocalDateTime.of(targetDate, targetTime);

        long totalSeconds = ChronoUnit.SECONDS.between(now, target);
        boolean isPast = totalSeconds < 0;
        totalSeconds = Math.abs(totalSeconds);

        long days = totalSeconds / (24 * 60 * 60);
        long hours = (totalSeconds % (24 * 60 * 60)) / (60 * 60);
        long minutes = (totalSeconds % (60 * 60)) / 60;
        long seconds = totalSeconds % 60;

        result.put("isPast", isPast);
        result.put("days", days);
        result.put("hours", hours);
        result.put("minutes", minutes);
        result.put("seconds", seconds);
        result.put("totalSeconds", totalSeconds);

        return result;
    }
}
