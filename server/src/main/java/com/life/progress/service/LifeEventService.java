package com.life.progress.service;

import com.life.progress.entity.LifeEventConfig;
import com.life.progress.entity.UserDailyEvent;

import java.util.List;
import java.util.Map;

public interface LifeEventService {

    UserDailyEvent getTodayEvent(Long userId);

    UserDailyEvent generateTodayEvent(Long userId);

    boolean applyEventImpact(Long userId, Long eventRecordId);

    List<UserDailyEvent> getUserEventHistory(Long userId, Integer limit);

    List<LifeEventConfig> getAllEventConfigs();

    List<LifeEventConfig> getActiveEventConfigs();

    LifeEventConfig getEventConfigById(Long id);

    boolean addEventConfig(LifeEventConfig config);

    boolean updateEventConfig(LifeEventConfig config);

    boolean deleteEventConfig(Long id);

    Map<String, Object> getEventStatistics();
}
