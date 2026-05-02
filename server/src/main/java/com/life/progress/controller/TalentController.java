package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.TalentConfig;
import com.life.progress.entity.TalentSimulationLog;
import com.life.progress.entity.UserTalent;
import com.life.progress.service.TalentService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/talent")
public class TalentController {

    @Autowired
    private TalentService talentService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/my")
    public Result<UserTalent> getMyTalent(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        UserTalent talent = talentService.getUserTalentByUserId(userId);
        return Result.success(talent);
    }

    @GetMapping("/config")
    public Result<Map<String, Object>> getTalentConfig() {
        List<TalentConfig> configs = talentService.getAllTalentConfigs();
        int totalPoints = talentService.getTotalPoints();
        int maxPerTalent = talentService.getMaxPerTalent();
        int minPerTalent = talentService.getMinPerTalent();

        Map<String, Object> result = new HashMap<>();
        result.put("totalPoints", totalPoints);
        result.put("maxPerTalent", maxPerTalent);
        result.put("minPerTalent", minPerTalent);
        result.put("configs", configs);

        return Result.success(result);
    }

    @PostMapping("/allocate")
    public Result<UserTalent> allocateTalentPoints(
            @RequestBody Map<String, Integer> talentPoints,
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        boolean success = talentService.allocateTalentPoints(userId, talentPoints);
        if (success) {
            UserTalent updated = talentService.getUserTalentByUserId(userId);
            return Result.success("天赋点分配成功", updated);
        } else {
            return Result.error("天赋点分配失败，请检查点数是否符合规则");
        }
    }

    @PostMapping("/reset")
    public Result<UserTalent> resetTalentPoints(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Integer> resetPoints = new HashMap<>();
        resetPoints.put("appearance", 0);
        resetPoints.put("intelligence", 0);
        resetPoints.put("wealth", 0);
        resetPoints.put("health", 0);
        resetPoints.put("emotional", 0);
        resetPoints.put("luck", 0);

        boolean success = talentService.allocateTalentPoints(userId, resetPoints);
        if (success) {
            UserTalent updated = talentService.getUserTalentByUserId(userId);
            return Result.success("天赋点已重置", updated);
        } else {
            return Result.error("重置失败");
        }
    }

    @GetMapping("/simulate/career")
    public Result<Map<String, Object>> simulateCareer(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> result = talentService.simulateCareer(userId);
        return Result.success(result);
    }

    @GetMapping("/simulate/relationship")
    public Result<Map<String, Object>> simulateRelationship(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> result = talentService.simulateRelationship(userId);
        return Result.success(result);
    }

    @GetMapping("/simulate/health")
    public Result<Map<String, Object>> simulateHealth(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> result = talentService.simulateHealth(userId);
        return Result.success(result);
    }

    @GetMapping("/simulate/wealth")
    public Result<Map<String, Object>> simulateWealth(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> result = talentService.simulateWealth(userId);
        return Result.success(result);
    }

    @GetMapping("/simulate/overall")
    public Result<Map<String, Object>> simulateOverall(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> result = talentService.simulateOverall(userId);
        return Result.success(result);
    }

    @GetMapping("/logs")
    public Result<List<TalentSimulationLog>> getSimulationLogs(
            @RequestParam(required = false) String simulationType,
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        List<TalentSimulationLog> logs = talentService.getSimulationLogs(userId, simulationType);
        return Result.success(logs);
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
