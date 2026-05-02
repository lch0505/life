package com.life.progress.service;

import com.life.progress.entity.TalentConfig;
import com.life.progress.entity.TalentSimulationLog;
import com.life.progress.entity.UserTalent;

import java.util.List;
import java.util.Map;

public interface TalentService {

    // UserTalent related methods
    UserTalent getUserTalentByUserId(Long userId);

    UserTalent initUserTalent(Long userId);

    boolean updateUserTalent(UserTalent userTalent);

    boolean allocateTalentPoints(Long userId, Map<String, Integer> talentPoints);

    // TalentConfig related methods
    List<TalentConfig> getAllTalentConfigs();

    TalentConfig getTalentConfigByKey(String configKey);

    int getTotalPoints();

    int getMaxPerTalent();

    int getMinPerTalent();

    boolean updateTalentConfig(String configKey, String configValue, String description);

    // Simulation related methods
    Map<String, Object> simulateCareer(Long userId);

    Map<String, Object> simulateRelationship(Long userId);

    Map<String, Object> simulateHealth(Long userId);

    Map<String, Object> simulateWealth(Long userId);

    Map<String, Object> simulateOverall(Long userId);

    List<TalentSimulationLog> getSimulationLogs(Long userId, String simulationType);

    // Admin methods
    List<UserTalent> getAllUserTalents();

    Map<String, Object> getTalentStatistics();
}
