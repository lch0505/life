package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.LifeEventConfig;
import com.life.progress.service.LifeEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/life-event/admin")
public class LifeEventAdminController {

    @Autowired
    private LifeEventService lifeEventService;

    @GetMapping("/configs")
    public Result<List<LifeEventConfig>> getAllConfigs() {
        List<LifeEventConfig> configs = lifeEventService.getAllEventConfigs();
        return Result.success(configs);
    }

    @GetMapping("/config/{id}")
    public Result<LifeEventConfig> getConfigById(@PathVariable Long id) {
        LifeEventConfig config = lifeEventService.getEventConfigById(id);
        if (config == null) {
            return Result.error("事件配置不存在");
        }
        return Result.success(config);
    }

    @PostMapping("/config")
    public Result<LifeEventConfig> addConfig(@RequestBody LifeEventConfig config) {
        boolean success = lifeEventService.addEventConfig(config);
        if (success) {
            return Result.success("添加成功", config);
        }
        return Result.error("添加失败");
    }

    @PutMapping("/config/{id}")
    public Result<LifeEventConfig> updateConfig(@PathVariable Long id, @RequestBody LifeEventConfig config) {
        config.setId(id);
        boolean success = lifeEventService.updateEventConfig(config);
        if (success) {
            return Result.success("更新成功", config);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/config/{id}")
    public Result<Object> deleteConfig(@PathVariable Long id) {
        boolean success = lifeEventService.deleteEventConfig(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = lifeEventService.getEventStatistics();
        return Result.success(stats);
    }
}
