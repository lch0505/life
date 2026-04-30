package com.life.progress.service.impl;

import com.life.progress.entity.GoalTask;
import com.life.progress.mapper.GoalTaskMapper;
import com.life.progress.service.GoalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoalTaskServiceImpl implements GoalTaskService {

    @Autowired
    private GoalTaskMapper goalTaskMapper;

    @Override
    public GoalTask findById(Long id) {
        return goalTaskMapper.selectById(id);
    }

    @Override
    public List<GoalTask> findByGoalId(Long goalId) {
        return goalTaskMapper.selectByGoalId(goalId);
    }

    @Override
    public List<GoalTask> findByUserId(Long userId) {
        return goalTaskMapper.selectByUserId(userId);
    }

    @Override
    public List<GoalTask> findAll() {
        return goalTaskMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(GoalTask goalTask) {
        if (goalTask.getProgress() == null) {
            goalTask.setProgress(BigDecimal.ZERO);
        }
        if (goalTask.getStatus() == null) {
            goalTask.setStatus(1);
        }
        if (goalTask.getSortOrder() == null) {
            goalTask.setSortOrder(0);
        }
        return goalTaskMapper.insert(goalTask) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(GoalTask goalTask) {
        return goalTaskMapper.update(goalTask) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProgress(Long id, BigDecimal progress) {
        return goalTaskMapper.updateProgress(id, progress) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        return goalTaskMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return goalTaskMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByGoalId(Long goalId) {
        return goalTaskMapper.deleteByGoalId(goalId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        return goalTaskMapper.deleteByUserId(userId) > 0;
    }
}
