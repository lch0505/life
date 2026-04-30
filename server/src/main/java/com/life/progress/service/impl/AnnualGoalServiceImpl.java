package com.life.progress.service.impl;

import com.life.progress.entity.AnnualGoal;
import com.life.progress.mapper.AnnualGoalMapper;
import com.life.progress.service.AnnualGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AnnualGoalServiceImpl implements AnnualGoalService {

    @Autowired
    private AnnualGoalMapper annualGoalMapper;

    @Override
    public AnnualGoal findById(Long id) {
        return annualGoalMapper.selectById(id);
    }

    @Override
    public List<AnnualGoal> findByUserId(Long userId) {
        return annualGoalMapper.selectByUserId(userId);
    }

    @Override
    public List<AnnualGoal> findByUserIdAndYear(Long userId, Integer year) {
        return annualGoalMapper.selectByUserIdAndYear(userId, year);
    }

    @Override
    public List<AnnualGoal> findAll() {
        return annualGoalMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(AnnualGoal annualGoal) {
        if (annualGoal.getProgress() == null) {
            annualGoal.setProgress(BigDecimal.ZERO);
        }
        if (annualGoal.getStatus() == null) {
            annualGoal.setStatus(1);
        }
        if (annualGoal.getPriority() == null) {
            annualGoal.setPriority(1);
        }
        return annualGoalMapper.insert(annualGoal) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AnnualGoal annualGoal) {
        return annualGoalMapper.update(annualGoal) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProgress(Long id, BigDecimal progress) {
        return annualGoalMapper.updateProgress(id, progress) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        return annualGoalMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return annualGoalMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        return annualGoalMapper.deleteByUserId(userId) > 0;
    }
}
