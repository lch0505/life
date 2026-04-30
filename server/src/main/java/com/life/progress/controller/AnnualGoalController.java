package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.AnnualGoal;
import com.life.progress.entity.GoalTask;
import com.life.progress.service.AnnualGoalService;
import com.life.progress.service.GoalTaskService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goal")
public class AnnualGoalController {

    @Autowired
    private AnnualGoalService annualGoalService;

    @Autowired
    private GoalTaskService goalTaskService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<List<AnnualGoal>> getList(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer year) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<AnnualGoal> goals = annualGoalService.findByUserIdAndYear(userId, year);
        return Result.success(goals);
    }

    @GetMapping("/list-with-progress")
    public Result<List<Map<String, Object>>> getListWithProgress(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer year) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<AnnualGoal> goals = annualGoalService.findByUserIdAndYear(userId, year);
        List<Map<String, Object>> result = goals.stream()
                .map(goal -> buildGoalWithProgress(goal, userId))
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

        AnnualGoal goal = annualGoalService.findById(id);
        if (goal == null) {
            return Result.error(404, "目标不存在");
        }

        if (!goal.getUserId().equals(userId)) {
            return Result.error(403, "无权限访问");
        }

        return Result.success(buildGoalWithProgress(goal, userId));
    }

    @PostMapping("/create")
    public Result<AnnualGoal> create(
            @RequestBody AnnualGoal goal,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        if (goal.getYear() == null) {
            goal.setYear(LocalDate.now().getYear());
        }

        goal.setUserId(userId);
        boolean success = annualGoalService.create(goal);

        if (success) {
            return Result.success("创建成功", goal);
        } else {
            return Result.error("创建失败");
        }
    }

    @PutMapping("/update/{id}")
    public Result<AnnualGoal> update(
            @PathVariable Long id,
            @RequestBody AnnualGoal goal,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        AnnualGoal existing = annualGoalService.findById(id);
        if (existing == null) {
            return Result.error(404, "目标不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改");
        }

        goal.setId(id);
        goal.setUserId(userId);
        boolean success = annualGoalService.update(goal);

        if (success) {
            return Result.success("更新成功", goal);
        } else {
            return Result.error("更新失败");
        }
    }

    @PutMapping("/progress/{id}")
    public Result<String> updateProgress(
            @PathVariable Long id,
            @RequestParam BigDecimal progress,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        AnnualGoal existing = annualGoalService.findById(id);
        if (existing == null) {
            return Result.error(404, "目标不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改");
        }

        boolean success = annualGoalService.updateProgress(id, progress);

        if (success) {
            return Result.success("进度已更新", null);
        } else {
            return Result.error("更新失败");
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

        AnnualGoal existing = annualGoalService.findById(id);
        if (existing == null) {
            return Result.error(404, "目标不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除");
        }

        goalTaskService.deleteByGoalId(id);
        boolean success = annualGoalService.deleteById(id);

        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/{goalId}/tasks")
    public Result<List<GoalTask>> getTasks(
            @PathVariable Long goalId,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        AnnualGoal goal = annualGoalService.findById(goalId);
        if (goal == null) {
            return Result.error(404, "目标不存在");
        }

        if (!goal.getUserId().equals(userId)) {
            return Result.error(403, "无权限访问");
        }

        List<GoalTask> tasks = goalTaskService.findByGoalId(goalId);
        return Result.success(tasks);
    }

    @PostMapping("/{goalId}/task")
    public Result<GoalTask> createTask(
            @PathVariable Long goalId,
            @RequestBody GoalTask task,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        AnnualGoal goal = annualGoalService.findById(goalId);
        if (goal == null) {
            return Result.error(404, "目标不存在");
        }

        if (!goal.getUserId().equals(userId)) {
            return Result.error(403, "无权限操作");
        }

        task.setGoalId(goalId);
        task.setUserId(userId);
        boolean success = goalTaskService.create(task);

        if (success) {
            recalculateGoalProgress(goalId);
            return Result.success("创建成功", task);
        } else {
            return Result.error("创建失败");
        }
    }

    @PutMapping("/task/{taskId}")
    public Result<GoalTask> updateTask(
            @PathVariable Long taskId,
            @RequestBody GoalTask task,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        GoalTask existing = goalTaskService.findById(taskId);
        if (existing == null) {
            return Result.error(404, "任务不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改");
        }

        task.setId(taskId);
        task.setUserId(userId);
        task.setGoalId(existing.getGoalId());
        boolean success = goalTaskService.update(task);

        if (success) {
            recalculateGoalProgress(existing.getGoalId());
            return Result.success("更新成功", task);
        } else {
            return Result.error("更新失败");
        }
    }

    @PutMapping("/task/progress/{taskId}")
    public Result<String> updateTaskProgress(
            @PathVariable Long taskId,
            @RequestParam BigDecimal progress,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        GoalTask existing = goalTaskService.findById(taskId);
        if (existing == null) {
            return Result.error(404, "任务不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限修改");
        }

        boolean success = goalTaskService.updateProgress(taskId, progress);

        if (success) {
            recalculateGoalProgress(existing.getGoalId());
            return Result.success("进度已更新", null);
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/task/{taskId}")
    public Result<String> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        GoalTask existing = goalTaskService.findById(taskId);
        if (existing == null) {
            return Result.error(404, "任务不存在");
        }

        if (!existing.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除");
        }

        Long goalId = existing.getGoalId();
        boolean success = goalTaskService.deleteById(taskId);

        if (success) {
            recalculateGoalProgress(goalId);
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
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

    private Map<String, Object> buildGoalWithProgress(AnnualGoal goal, Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", goal.getId());
        result.put("userId", goal.getUserId());
        result.put("year", goal.getYear());
        result.put("title", goal.getTitle());
        result.put("description", goal.getDescription());
        result.put("progress", goal.getProgress());
        result.put("status", goal.getStatus());
        result.put("startDate", goal.getStartDate());
        result.put("endDate", goal.getEndDate());
        result.put("priority", goal.getPriority());
        result.put("createTime", goal.getCreateTime());
        result.put("updateTime", goal.getUpdateTime());

        List<GoalTask> tasks = goalTaskService.findByGoalId(goal.getId());
        result.put("tasks", tasks);

        if (!tasks.isEmpty()) {
            BigDecimal totalProgress = BigDecimal.ZERO;
            for (GoalTask task : tasks) {
                totalProgress = totalProgress.add(task.getProgress());
            }
            BigDecimal avgProgress = totalProgress.divide(BigDecimal.valueOf(tasks.size()), 2, BigDecimal.ROUND_HALF_UP);
            result.put("calculatedProgress", avgProgress);
        } else {
            result.put("calculatedProgress", goal.getProgress());
        }

        return result;
    }

    private void recalculateGoalProgress(Long goalId) {
        List<GoalTask> tasks = goalTaskService.findByGoalId(goalId);
        if (tasks.isEmpty()) {
            return;
        }

        BigDecimal totalProgress = BigDecimal.ZERO;
        for (GoalTask task : tasks) {
            totalProgress = totalProgress.add(task.getProgress());
        }
        BigDecimal avgProgress = totalProgress.divide(BigDecimal.valueOf(tasks.size()), 2, BigDecimal.ROUND_HALF_UP);

        annualGoalService.updateProgress(goalId, avgProgress);
    }
}
