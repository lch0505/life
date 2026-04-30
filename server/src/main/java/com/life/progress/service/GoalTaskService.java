package com.life.progress.service;

import com.life.progress.entity.GoalTask;

import java.util.List;

public interface GoalTaskService {

    GoalTask findById(Long id);

    List<GoalTask> findByGoalId(Long goalId);

    List<GoalTask> findByUserId(Long userId);

    List<GoalTask> findAll();

    boolean create(GoalTask goalTask);

    boolean update(GoalTask goalTask);

    boolean updateProgress(Long id, java.math.BigDecimal progress);

    boolean updateStatus(Long id, Integer status);

    boolean deleteById(Long id);

    boolean deleteByGoalId(Long goalId);

    boolean deleteByUserId(Long userId);
}
