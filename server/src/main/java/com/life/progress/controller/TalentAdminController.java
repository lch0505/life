package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.TalentConfig;
import com.life.progress.entity.TalentSimulationLog;
import com.life.progress.entity.UserTalent;
import com.life.progress.service.TalentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/talent/admin")
@PreAuthorize("hasRole('ADMIN')")
public class TalentAdminController {

    @Autowired
    private TalentService talentService;

    @GetMapping("/configs")
    public Result<List<TalentConfig>> getAllConfigs() {
        List<TalentConfig> configs = talentService.getAllTalentConfigs();
        return Result.success(configs);
    }

    @PutMapping("/config/{configKey}")
    public Result<TalentConfig> updateConfig(
            @PathVariable String configKey,
            @RequestBody Map<String, String> request) {
        String configValue = request.get("configValue");
        String description = request.get("description");

        if (configValue == null) {
            return Result.error("配置值不能为空");
        }

        boolean success = talentService.updateTalentConfig(configKey, configValue, description);
        if (success) {
            TalentConfig updated = talentService.getTalentConfigByKey(configKey);
            return Result.success("配置更新成功", updated);
        } else {
            return Result.error("配置更新失败");
        }
    }

    @GetMapping("/users")
    public Result<List<UserTalent>> getAllUserTalents() {
        List<UserTalent> talents = talentService.getAllUserTalents();
        return Result.success(talents);
    }

    @GetMapping("/user/{userId}")
    public Result<UserTalent> getUserTalent(@PathVariable Long userId) {
        UserTalent talent = talentService.getUserTalentByUserId(userId);
        if (talent == null) {
            return Result.error(404, "用户天赋数据不存在");
        }
        return Result.success(talent);
    }

    @PutMapping("/user/{userId}/allocate")
    public Result<UserTalent> allocateTalentForUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> talentPoints) {
        boolean success = talentService.allocateTalentPoints(userId, talentPoints);
        if (success) {
            UserTalent updated = talentService.getUserTalentByUserId(userId);
            return Result.success("天赋点分配成功", updated);
        } else {
            return Result.error("天赋点分配失败，请检查点数是否符合规则");
        }
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getTalentStatistics() {
        Map<String, Object> stats = talentService.getTalentStatistics();
        return Result.success(stats);
    }

    @GetMapping("/logs/{userId}")
    public Result<List<TalentSimulationLog>> getUserSimulationLogs(
            @PathVariable Long userId,
            @RequestParam(required = false) String simulationType) {
        List<TalentSimulationLog> logs = talentService.getSimulationLogs(userId, simulationType);
        return Result.success(logs);
    }

    @PostMapping("/init/{userId}")
    public Result<UserTalent> initUserTalent(@PathVariable Long userId) {
        UserTalent talent = talentService.initUserTalent(userId);
        return Result.success("初始化成功", talent);
    }
}
