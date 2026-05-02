package com.life.progress.service.impl;

import com.life.progress.entity.LifeEventConfig;
import com.life.progress.entity.UserDailyEvent;
import com.life.progress.entity.UserTalent;
import com.life.progress.mapper.LifeEventConfigMapper;
import com.life.progress.mapper.UserDailyEventMapper;
import com.life.progress.mapper.UserTalentMapper;
import com.life.progress.service.LifeEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class LifeEventServiceImpl implements LifeEventService {

    @Autowired
    private LifeEventConfigMapper lifeEventConfigMapper;

    @Autowired
    private UserDailyEventMapper userDailyEventMapper;

    @Autowired
    private UserTalentMapper userTalentMapper;

    private Random random = new Random();

    @Override
    public UserDailyEvent getTodayEvent(Long userId) {
        LocalDate today = LocalDate.now();
        UserDailyEvent event = userDailyEventMapper.selectByUserAndDate(userId, today);
        
        if (event == null) {
            event = generateTodayEvent(userId);
        }
        
        return event;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDailyEvent generateTodayEvent(Long userId) {
        LocalDate today = LocalDate.now();
        
        UserDailyEvent existing = userDailyEventMapper.selectByUserAndDate(userId, today);
        if (existing != null) {
            return existing;
        }
        
        LifeEventConfig selectedEvent = selectRandomEvent();
        
        UserDailyEvent dailyEvent = new UserDailyEvent();
        dailyEvent.setUserId(userId);
        dailyEvent.setEventId(selectedEvent.getId());
        dailyEvent.setEventDate(today);
        dailyEvent.setEventType(selectedEvent.getEventType());
        dailyEvent.setEventName(selectedEvent.getEventName());
        dailyEvent.setEventDescription(selectedEvent.getEventDescription());
        dailyEvent.setImpactType(selectedEvent.getImpactType());
        dailyEvent.setImpactValue(selectedEvent.getImpactValue());
        dailyEvent.setIsApplied(0);
        
        userDailyEventMapper.insert(dailyEvent);
        
        return dailyEvent;
    }

    private LifeEventConfig selectRandomEvent() {
        List<LifeEventConfig> activeEvents = lifeEventConfigMapper.selectActive();
        
        if (activeEvents == null || activeEvents.isEmpty()) {
            return createDefaultEvent();
        }
        
        int totalWeight = 0;
        for (LifeEventConfig event : activeEvents) {
            totalWeight += event.getWeight();
        }
        
        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (LifeEventConfig event : activeEvents) {
            currentWeight += event.getWeight();
            if (randomValue < currentWeight) {
                return event;
            }
        }
        
        return activeEvents.get(activeEvents.size() - 1);
    }

    private LifeEventConfig createDefaultEvent() {
        LifeEventConfig defaultEvent = new LifeEventConfig();
        defaultEvent.setId(0L);
        defaultEvent.setEventType(1);
        defaultEvent.setEventName("平凡的一天");
        defaultEvent.setEventDescription("今天是平凡的一天，没有特别的事情发生。保持平常心，继续努力！");
        defaultEvent.setIcon("🌅");
        defaultEvent.setColor("#409EFF");
        defaultEvent.setImpactType("luck");
        defaultEvent.setImpactValue(1);
        return defaultEvent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyEventImpact(Long userId, Long eventRecordId) {
        UserDailyEvent event = userDailyEventMapper.selectById(eventRecordId);
        
        if (event == null || !event.getUserId().equals(userId)) {
            return false;
        }
        
        if (event.getIsApplied() == 1) {
            return true;
        }
        
        UserTalent userTalent = userTalentMapper.selectByUserId(userId);
        if (userTalent == null) {
            return false;
        }
        
        String impactType = event.getImpactType();
        int impactValue = event.getImpactValue();
        
        applyImpact(userTalent, impactType, impactValue);
        
        userTalentMapper.update(userTalent);
        
        event.setIsApplied(1);
        userDailyEventMapper.update(event);
        
        return true;
    }

    private void applyImpact(UserTalent talent, String impactType, int impactValue) {
        int maxPerTalent = 10;
        int minPerTalent = 0;
        
        switch (impactType) {
            case "appearance":
                int newAppearance = talent.getAppearance() + impactValue;
                talent.setAppearance(Math.max(minPerTalent, Math.min(maxPerTalent, newAppearance)));
                break;
            case "intelligence":
                int newIntelligence = talent.getIntelligence() + impactValue;
                talent.setIntelligence(Math.max(minPerTalent, Math.min(maxPerTalent, newIntelligence)));
                break;
            case "wealth":
                int newWealth = talent.getWealth() + impactValue;
                talent.setWealth(Math.max(minPerTalent, Math.min(maxPerTalent, newWealth)));
                break;
            case "health":
                int newHealth = talent.getHealth() + impactValue;
                talent.setHealth(Math.max(minPerTalent, Math.min(maxPerTalent, newHealth)));
                break;
            case "emotional":
                int newEmotional = talent.getEmotional() + impactValue;
                talent.setEmotional(Math.max(minPerTalent, Math.min(maxPerTalent, newEmotional)));
                break;
            case "luck":
                int newLuck = talent.getLuck() + impactValue;
                talent.setLuck(Math.max(minPerTalent, Math.min(maxPerTalent, newLuck)));
                break;
        }
        
        int totalUsed = talent.getAppearance() + talent.getIntelligence() + 
                       talent.getWealth() + talent.getHealth() + 
                       talent.getEmotional() + talent.getLuck();
        talent.setTotalPointsUsed(totalUsed);
    }

    @Override
    public List<UserDailyEvent> getUserEventHistory(Long userId, Integer limit) {
        if (limit != null && limit > 0) {
            return userDailyEventMapper.selectByUserIdWithLimit(userId, limit);
        }
        return userDailyEventMapper.selectByUserId(userId);
    }

    @Override
    public List<LifeEventConfig> getAllEventConfigs() {
        return lifeEventConfigMapper.selectAll();
    }

    @Override
    public List<LifeEventConfig> getActiveEventConfigs() {
        return lifeEventConfigMapper.selectActive();
    }

    @Override
    public LifeEventConfig getEventConfigById(Long id) {
        return lifeEventConfigMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addEventConfig(LifeEventConfig config) {
        return lifeEventConfigMapper.insert(config) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEventConfig(LifeEventConfig config) {
        return lifeEventConfigMapper.update(config) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEventConfig(Long id) {
        return lifeEventConfigMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> getEventStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<LifeEventConfig> allConfigs = lifeEventConfigMapper.selectAll();
        
        int totalConfigs = allConfigs.size();
        int activeConfigs = 0;
        int goodEvents = 0;
        int badEvents = 0;
        int surpriseEvents = 0;
        
        for (LifeEventConfig config : allConfigs) {
            if (config.getStatus() == 1) {
                activeConfigs++;
            }
            
            switch (config.getEventType()) {
                case 1:
                    goodEvents++;
                    break;
                case 2:
                    badEvents++;
                    break;
                case 3:
                    surpriseEvents++;
                    break;
            }
        }
        
        stats.put("totalConfigs", totalConfigs);
        stats.put("activeConfigs", activeConfigs);
        stats.put("goodEvents", goodEvents);
        stats.put("badEvents", badEvents);
        stats.put("surpriseEvents", surpriseEvents);
        
        return stats;
    }
}
