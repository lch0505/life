package com.life.progress.service;

import com.life.progress.entity.AnnualGoal;

import java.util.List;

public interface AnnualGoalService {

    AnnualGoal findById(Long id);

    List<AnnualGoal> findByUserId(Long userId);

    List<AnnualGoal> findByUserIdAndYear(Long userId, Integer year);

    List<AnnualGoal> findAll();

    boolean create(AnnualGoal annualGoal);

    boolean update(AnnualGoal annualGoal);

    boolean updateProgress(Long id, java.math.BigDecimal progress);

    boolean updateStatus(Long id, Integer status);

    boolean deleteById(Long id);

    boolean deleteByUserId(Long userId);
}
