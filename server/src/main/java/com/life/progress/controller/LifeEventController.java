package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.LifeEventConfig;
import com.life.progress.entity.UserDailyEvent;
import com.life.progress.service.LifeEventService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/life-event")
public class LifeEventController {

    @Autowired
    private LifeEventService lifeEventService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayEvent(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        UserDailyEvent todayEvent = lifeEventService.getTodayEvent(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("event", todayEvent);
        result.put("eventTypeText", getEventTypeText(todayEvent.getEventType()));
        result.put("impactTypeText", getImpactTypeText(todayEvent.getImpactType()));
        
        return Result.success(result);
    }

    @PostMapping("/apply")
    public Result<Map<String, Object>> applyEventImpact(
            @RequestBody Map<String, Long> request,
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Long eventRecordId = request.get("eventRecordId");
        if (eventRecordId == null) {
            return Result.error("事件记录ID不能为空");
        }

        boolean success = lifeEventService.applyEventImpact(userId, eventRecordId);
        
        if (success) {
            Map<String, Object> result = new HashMap<>();
            result.put("applied", true);
            result.put("message", "事件效果已应用到你的状态中");
            return Result.success("应用成功", result);
        } else {
            return Result.error("应用失败，可能事件已被应用或不存在");
        }
    }

    @GetMapping("/history")
    public Result<List<UserDailyEvent>> getEventHistory(
            @RequestParam(required = false) Integer limit,
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        List<UserDailyEvent> history = lifeEventService.getUserEventHistory(userId, limit);
        return Result.success(history);
    }

    @GetMapping("/configs")
    public Result<List<LifeEventConfig>> getActiveConfigs() {
        List<LifeEventConfig> configs = lifeEventService.getActiveEventConfigs();
        return Result.success(configs);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getEventStatistics(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> stats = lifeEventService.getEventStatistics();
        return Result.success(stats);
    }

    private String getEventTypeText(Integer eventType) {
        switch (eventType) {
            case 1:
                return "好运buff";
            case 2:
                return "小倒霉";
            case 3:
                return "意外惊喜";
            default:
                return "未知";
        }
    }

    private String getImpactTypeText(String impactType) {
        switch (impactType) {
            case "appearance":
                return "颜值";
            case "intelligence":
                return "智商";
            case "wealth":
                return "财运";
            case "health":
                return "健康";
            case "emotional":
                return "情商";
            case "luck":
                return "运气";
            default:
                return "未知";
        }
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
