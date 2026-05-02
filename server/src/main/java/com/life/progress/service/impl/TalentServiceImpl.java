package com.life.progress.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.life.progress.entity.TalentConfig;
import com.life.progress.entity.TalentSimulationLog;
import com.life.progress.entity.UserTalent;
import com.life.progress.mapper.TalentConfigMapper;
import com.life.progress.mapper.TalentSimulationLogMapper;
import com.life.progress.mapper.UserTalentMapper;
import com.life.progress.service.TalentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TalentServiceImpl implements TalentService {

    @Autowired
    private UserTalentMapper userTalentMapper;

    @Autowired
    private TalentConfigMapper talentConfigMapper;

    @Autowired
    private TalentSimulationLogMapper talentSimulationLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // UserTalent related methods
    @Override
    public UserTalent getUserTalentByUserId(Long userId) {
        UserTalent talent = userTalentMapper.selectByUserId(userId);
        if (talent == null) {
            talent = initUserTalent(userId);
        }
        return talent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTalent initUserTalent(Long userId) {
        UserTalent existing = userTalentMapper.selectByUserId(userId);
        if (existing != null) {
            return existing;
        }

        UserTalent talent = new UserTalent();
        talent.setUserId(userId);
        talent.setAppearance(0);
        talent.setIntelligence(0);
        talent.setWealth(0);
        talent.setHealth(0);
        talent.setEmotional(0);
        talent.setLuck(0);
        talent.setTotalPointsUsed(0);

        userTalentMapper.insert(talent);
        return talent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserTalent(UserTalent userTalent) {
        return userTalentMapper.update(userTalent) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean allocateTalentPoints(Long userId, Map<String, Integer> talentPoints) {
        UserTalent existing = getUserTalentByUserId(userId);
        if (existing == null) {
            return false;
        }

        int totalPoints = getTotalPoints();
        int maxPerTalent = getMaxPerTalent();
        int minPerTalent = getMinPerTalent();

        // Calculate points to allocate
        int appearance = talentPoints.getOrDefault("appearance", existing.getAppearance());
        int intelligence = talentPoints.getOrDefault("intelligence", existing.getIntelligence());
        int wealth = talentPoints.getOrDefault("wealth", existing.getWealth());
        int health = talentPoints.getOrDefault("health", existing.getHealth());
        int emotional = talentPoints.getOrDefault("emotional", existing.getEmotional());
        int luck = talentPoints.getOrDefault("luck", existing.getLuck());

        // Validate each talent is within range
        if (appearance < minPerTalent || appearance > maxPerTalent ||
            intelligence < minPerTalent || intelligence > maxPerTalent ||
            wealth < minPerTalent || wealth > maxPerTalent ||
            health < minPerTalent || health > maxPerTalent ||
            emotional < minPerTalent || emotional > maxPerTalent ||
            luck < minPerTalent || luck > maxPerTalent) {
            return false;
        }

        // Validate total points
        int totalUsed = appearance + intelligence + wealth + health + emotional + luck;
        if (totalUsed > totalPoints) {
            return false;
        }

        // Update talent
        existing.setAppearance(appearance);
        existing.setIntelligence(intelligence);
        existing.setWealth(wealth);
        existing.setHealth(health);
        existing.setEmotional(emotional);
        existing.setLuck(luck);
        existing.setTotalPointsUsed(totalUsed);

        return updateUserTalent(existing);
    }

    // TalentConfig related methods
    @Override
    public List<TalentConfig> getAllTalentConfigs() {
        return talentConfigMapper.selectAll();
    }

    @Override
    public TalentConfig getTalentConfigByKey(String configKey) {
        return talentConfigMapper.selectByKey(configKey);
    }

    @Override
    public int getTotalPoints() {
        TalentConfig config = talentConfigMapper.selectByKey("total_points");
        return config != null ? Integer.parseInt(config.getConfigValue()) : 20;
    }

    @Override
    public int getMaxPerTalent() {
        TalentConfig config = talentConfigMapper.selectByKey("max_per_talent");
        return config != null ? Integer.parseInt(config.getConfigValue()) : 10;
    }

    @Override
    public int getMinPerTalent() {
        TalentConfig config = talentConfigMapper.selectByKey("min_per_talent");
        return config != null ? Integer.parseInt(config.getConfigValue()) : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTalentConfig(String configKey, String configValue, String description) {
        TalentConfig existing = talentConfigMapper.selectByKey(configKey);
        if (existing == null) {
            TalentConfig newConfig = new TalentConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setDescription(description);
            return talentConfigMapper.insert(newConfig) > 0;
        } else {
            existing.setConfigValue(configValue);
            if (description != null) {
                existing.setDescription(description);
            }
            return talentConfigMapper.update(existing) > 0;
        }
    }

    // Simulation related methods
    private Random random = new Random();

    private int calculateBaseScore(UserTalent talent, String... keys) {
        int score = 0;
        for (String key : keys) {
            switch (key) {
                case "appearance": score += talent.getAppearance(); break;
                case "intelligence": score += talent.getIntelligence(); break;
                case "wealth": score += talent.getWealth(); break;
                case "health": score += talent.getHealth(); break;
                case "emotional": score += talent.getEmotional(); break;
                case "luck": score += talent.getLuck(); break;
            }
        }
        return score;
    }

    private String getLevelDescription(int score, int maxPossible) {
        double percentage = (double) score / maxPossible * 100;
        if (percentage >= 80) return "卓越";
        if (percentage >= 60) return "优秀";
        if (percentage >= 40) return "良好";
        if (percentage >= 20) return "一般";
        return "有待提升";
    }

    private void saveSimulationLog(Long userId, String simulationType, Map<String, Object> result, UserTalent talent) {
        try {
            TalentSimulationLog log = new TalentSimulationLog();
            log.setUserId(userId);
            log.setSimulationType(simulationType);
            log.setSimulationResult(objectMapper.writeValueAsString(result));
            log.setTalentSnapshot(objectMapper.writeValueAsString(talent));
            talentSimulationLogMapper.insert(log);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> simulateCareer(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        
        // Career主要受智商、情商、财运、运气影响
        int baseScore = calculateBaseScore(talent, "intelligence", "emotional", "wealth", "luck");
        int maxPossible = getMaxPerTalent() * 4;
        
        // Add random factor (0-20%)
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        
        String level = getLevelDescription(finalScore, maxPossible);
        
        List<String> events = generateCareerEvents(talent, finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("maxScore", maxPossible);
        result.put("level", level);
        result.put("events", events);
        result.put("talentBreakdown", Arrays.asList(
            Map.of("name", "智商", "value", talent.getIntelligence(), "impact", "高"),
            Map.of("name", "情商", "value", talent.getEmotional(), "impact", "高"),
            Map.of("name", "财运", "value", talent.getWealth(), "impact", "中"),
            Map.of("name", "运气", "value", talent.getLuck(), "impact", "中")
        ));
        
        saveSimulationLog(userId, "career", result, talent);
        return result;
    }

    private List<String> generateCareerEvents(UserTalent talent, int score, int maxPossible) {
        List<String> events = new ArrayList<>();
        double percentage = (double) score / maxPossible;
        
        if (percentage >= 0.8) {
            events.add("🌟 你的职业生涯一帆风顺，年纪轻轻就成为行业翘楚！");
            if (talent.getIntelligence() >= 8) {
                events.add("💡 凭借卓越的智商，你在技术领域取得突破性成就。");
            }
            if (talent.getEmotional() >= 8) {
                events.add("🤝 你的高情商让你在团队中如鱼得水，领导力备受认可。");
            }
            if (talent.getLuck() >= 7) {
                events.add("🍀 运气极佳，多次遇到贵人相助，职业发展顺风顺水。");
            }
        } else if (percentage >= 0.6) {
            events.add("📈 你的职业发展稳定上升，在公司中扮演重要角色。");
            if (talent.getIntelligence() >= 6) {
                events.add("💻 专业能力出众，是团队中的技术骨干。");
            }
            if (talent.getEmotional() >= 6) {
                events.add("👥 人际关系处理得当，职场氛围轻松愉快。");
            }
        } else if (percentage >= 0.4) {
            events.add("📊 职业发展平稳，按部就班地推进着自己的事业。");
            events.add("💪 虽然没有大的突破，但稳步积累经验和资历。");
        } else {
            events.add("⚠️ 职业道路上遇到一些挑战，需要更加努力。");
            if (talent.getIntelligence() < 3) {
                events.add("📚 专业技能需要提升，建议持续学习充电。");
            }
            if (talent.getEmotional() < 3) {
                events.add("💬 职场沟通能力有待加强，人际关系可能成为瓶颈。");
            }
            if (talent.getLuck() < 3) {
                events.add("😔 运气稍差，一些好机会擦肩而过，但不要气馁！");
            }
        }
        
        return events;
    }

    @Override
    public Map<String, Object> simulateRelationship(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        
        // Relationship主要受颜值、情商、运气影响
        int baseScore = calculateBaseScore(talent, "appearance", "emotional", "luck");
        int maxPossible = getMaxPerTalent() * 3;
        
        // Add random factor (0-20%)
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        
        String level = getLevelDescription(finalScore, maxPossible);
        
        List<String> events = generateRelationshipEvents(talent, finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("maxScore", maxPossible);
        result.put("level", level);
        result.put("events", events);
        result.put("talentBreakdown", Arrays.asList(
            Map.of("name", "颜值", "value", talent.getAppearance(), "impact", "高"),
            Map.of("name", "情商", "value", talent.getEmotional(), "impact", "高"),
            Map.of("name", "运气", "value", talent.getLuck(), "impact", "中")
        ));
        
        saveSimulationLog(userId, "relationship", result, talent);
        return result;
    }

    private List<String> generateRelationshipEvents(UserTalent talent, int score, int maxPossible) {
        List<String> events = new ArrayList<>();
        double percentage = (double) score / maxPossible;
        
        if (percentage >= 0.8) {
            events.add("💕 你的感情生活美满幸福，是众人羡慕的对象！");
            if (talent.getAppearance() >= 8) {
                events.add("😍 魅力四射，无论走到哪里都是焦点，追求者众多。");
            }
            if (talent.getEmotional() >= 8) {
                events.add("💗 你懂得如何经营感情，与伴侣相处融洽，默契十足。");
            }
            if (talent.getLuck() >= 7) {
                events.add("🍀 在对的时间遇到对的人，缘分天注定。");
            }
        } else if (percentage >= 0.6) {
            events.add("💑 感情生活稳定甜蜜，与伴侣关系融洽。");
            if (talent.getAppearance() >= 6) {
                events.add("✨ 气质出众，总能给人留下良好的第一印象。");
            }
            if (talent.getEmotional() >= 6) {
                events.add("🤗 善于倾听和沟通，感情中的问题总能妥善解决。");
            }
        } else if (percentage >= 0.4) {
            events.add("💞 感情生活平平淡淡，偶尔会有小惊喜。");
            events.add("😊 虽然没有轰轰烈烈，但细水长流也是一种幸福。");
        } else {
            events.add("😔 感情路上可能会有些坎坷，需要更多的耐心。");
            if (talent.getAppearance() < 3) {
                events.add("💄 可以尝试提升自己的外在形象，增强自信。");
            }
            if (talent.getEmotional() < 3) {
                events.add("🗣️ 学会更好地表达情感，多与伴侣沟通交流。");
            }
            if (talent.getLuck() < 3) {
                events.add("⏳ 缘分未到，不要着急，最好的总在不经意间出现。");
            }
        }
        
        return events;
    }

    @Override
    public Map<String, Object> simulateHealth(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        
        // Health主要受健康、运气影响
        int baseScore = calculateBaseScore(talent, "health", "luck");
        int maxPossible = getMaxPerTalent() * 2;
        
        // Add random factor (0-20%)
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        
        String level = getLevelDescription(finalScore, maxPossible);
        
        List<String> events = generateHealthEvents(talent, finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("maxScore", maxPossible);
        result.put("level", level);
        result.put("events", events);
        result.put("talentBreakdown", Arrays.asList(
            Map.of("name", "健康", "value", talent.getHealth(), "impact", "高"),
            Map.of("name", "运气", "value", talent.getLuck(), "impact", "中")
        ));
        
        saveSimulationLog(userId, "health", result, talent);
        return result;
    }

    private List<String> generateHealthEvents(UserTalent talent, int score, int maxPossible) {
        List<String> events = new ArrayList<>();
        double percentage = (double) score / maxPossible;
        
        if (percentage >= 0.8) {
            events.add("💪 身体状况极佳，精力充沛，活力满满！");
            if (talent.getHealth() >= 8) {
                events.add("🏃 身体素质一流，运动能力出众，很少生病。");
            }
            if (talent.getLuck() >= 7) {
                events.add("🍀 运气极佳，即使遇到小病小痛也能很快康复。");
            }
            events.add("⏰ 预期寿命较长，可以享受更多美好时光。");
        } else if (percentage >= 0.6) {
            events.add("😊 身体健康状况良好，日常活动不受影响。");
            if (talent.getHealth() >= 6) {
                events.add("🏋️ 有一定的运动基础，保持规律锻炼效果更佳。");
            }
        } else if (percentage >= 0.4) {
            events.add("😐 健康状况一般，需要注意保养。");
            events.add("💊 可能会有一些小毛病，定期体检很重要。");
        } else {
            events.add("⚠️ 健康状况需要关注，建议加强锻炼和保养。");
            if (talent.getHealth() < 3) {
                events.add("🏥 体质较弱，容易疲劳生病，需要特别注意休息和营养。");
            }
            if (talent.getLuck() < 3) {
                events.add("😷 运气较差，可能会遇到一些健康问题，要多加小心。");
            }
            events.add("💡 建议：保持规律作息，合理饮食，适度运动，定期体检。");
        }
        
        return events;
    }

    @Override
    public Map<String, Object> simulateWealth(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        
        // Wealth主要受财运、智商、运气影响
        int baseScore = calculateBaseScore(talent, "wealth", "intelligence", "luck");
        int maxPossible = getMaxPerTalent() * 3;
        
        // Add random factor (0-20%)
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        
        String level = getLevelDescription(finalScore, maxPossible);
        
        List<String> events = generateWealthEvents(talent, finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("maxScore", maxPossible);
        result.put("level", level);
        result.put("events", events);
        result.put("talentBreakdown", Arrays.asList(
            Map.of("name", "财运", "value", talent.getWealth(), "impact", "高"),
            Map.of("name", "智商", "value", talent.getIntelligence(), "impact", "中"),
            Map.of("name", "运气", "value", talent.getLuck(), "impact", "高")
        ));
        
        saveSimulationLog(userId, "wealth", result, talent);
        return result;
    }

    private List<String> generateWealthEvents(UserTalent talent, int score, int maxPossible) {
        List<String> events = new ArrayList<>();
        double percentage = (double) score / maxPossible;
        
        if (percentage >= 0.8) {
            events.add("💰 财运亨通，财富积累速度惊人！");
            if (talent.getWealth() >= 8) {
                events.add("📈 投资眼光独到，无论是股票、房产还是创业都能获得丰厚回报。");
            }
            if (talent.getIntelligence() >= 6) {
                events.add("🧠 善于理财，能够让钱生钱，财富滚雪球式增长。");
            }
            if (talent.getLuck() >= 7) {
                events.add("🍀 运气极佳，经常能遇到意外之财，比如中彩票、继承遗产等。");
            }
            events.add("🏰 有很大概率实现财务自由，过上富裕舒适的生活。");
        } else if (percentage >= 0.6) {
            events.add("💵 财运不错，收入稳定增长。");
            if (talent.getWealth() >= 6) {
                events.add("📊 有一定的投资意识，能够获得不错的理财收益。");
            }
            events.add("🏠 能够轻松应对生活开支，还有余钱进行储蓄和投资。");
        } else if (percentage >= 0.4) {
            events.add("💴 财运一般，收入基本够用。");
            events.add("📝 需要精打细算，合理规划开支才能有所积蓄。");
        } else {
            events.add("💸 财运欠佳，可能会遇到一些财务困难。");
            if (talent.getWealth() < 3) {
                events.add("⚠️ 投资需谨慎，容易亏损，建议先学习理财知识。");
            }
            if (talent.getLuck() < 3) {
                events.add("😔 运气较差，可能会遇到意外支出，要有储蓄应对风险。");
            }
            events.add("💡 建议：提升专业技能增加收入，学习理财知识，养成储蓄习惯。");
        }
        
        return events;
    }

    @Override
    public Map<String, Object> simulateOverall(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        
        Map<String, Object> career = simulateCareerWithoutLog(userId);
        Map<String, Object> relationship = simulateRelationshipWithoutLog(userId);
        Map<String, Object> health = simulateHealthWithoutLog(userId);
        Map<String, Object> wealth = simulateWealthWithoutLog(userId);
        
        int careerScore = (int) career.get("score");
        int relationshipScore = (int) relationship.get("score");
        int healthScore = (int) health.get("score");
        int wealthScore = (int) wealth.get("score");
        
        int totalScore = careerScore + relationshipScore + healthScore + wealthScore;
        int maxPossible = (getMaxPerTalent() * 4) + (getMaxPerTalent() * 3) + 
                          (getMaxPerTalent() * 2) + (getMaxPerTalent() * 3);
        
        String level = getLevelDescription(totalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", totalScore);
        result.put("maxPossible", maxPossible);
        result.put("level", level);
        result.put("breakdown", Arrays.asList(
            Map.of("name", "事业", "score", careerScore, "level", career.get("level")),
            Map.of("name", "感情", "score", relationshipScore, "level", relationship.get("level")),
            Map.of("name", "健康", "score", healthScore, "level", health.get("level")),
            Map.of("name", "财富", "score", wealthScore, "level", wealth.get("level"))
        ));
        result.put("summary", generateOverallSummary(totalScore, maxPossible, talent));
        
        saveSimulationLog(userId, "overall", result, talent);
        return result;
    }

    private Map<String, Object> simulateCareerWithoutLog(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        int baseScore = calculateBaseScore(talent, "intelligence", "emotional", "wealth", "luck");
        int maxPossible = getMaxPerTalent() * 4;
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        String level = getLevelDescription(finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("level", level);
        return result;
    }

    private Map<String, Object> simulateRelationshipWithoutLog(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        int baseScore = calculateBaseScore(talent, "appearance", "emotional", "luck");
        int maxPossible = getMaxPerTalent() * 3;
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        String level = getLevelDescription(finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("level", level);
        return result;
    }

    private Map<String, Object> simulateHealthWithoutLog(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        int baseScore = calculateBaseScore(talent, "health", "luck");
        int maxPossible = getMaxPerTalent() * 2;
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        String level = getLevelDescription(finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("level", level);
        return result;
    }

    private Map<String, Object> simulateWealthWithoutLog(Long userId) {
        UserTalent talent = getUserTalentByUserId(userId);
        int baseScore = calculateBaseScore(talent, "wealth", "intelligence", "luck");
        int maxPossible = getMaxPerTalent() * 3;
        int randomBonus = random.nextInt((int) (maxPossible * 0.2) + 1);
        int finalScore = Math.min(baseScore + randomBonus, maxPossible);
        String level = getLevelDescription(finalScore, maxPossible);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", finalScore);
        result.put("level", level);
        return result;
    }

    private String generateOverallSummary(int totalScore, int maxPossible, UserTalent talent) {
        double percentage = (double) totalScore / maxPossible;
        
        if (percentage >= 0.8) {
            return "🌟 你的人生可谓是人生赢家！各项天赋均衡且出色，无论是事业、感情、健康还是财富都能取得令人羡慕的成就。你的高智商、高情商和出色的财运让你在各个领域都能如鱼得水。继续保持，你的未来充满无限可能！";
        } else if (percentage >= 0.6) {
            return "✨ 你的人生整体表现优秀！虽然可能不是每一项都顶尖，但综合实力相当不错。你在某些方面有明显的优势，同时其他方面也保持在良好水平。建议继续发挥优势，同时提升相对薄弱的环节，你的人生会更加精彩！";
        } else if (percentage >= 0.4) {
            return "📊 你的人生整体表现平稳。没有特别突出的优势，但也没有明显的短板。这是大多数人的人生状态，平平淡淡才是真。不过，如果你想要更精彩的人生，可以考虑在某些方面投入更多精力，提升自己的天赋点。";
        } else {
            return "💪 你的人生可能面临一些挑战，但请不要气馁！每个人都有自己的时区和节奏。虽然目前的天赋点数不高，但这并不决定你的全部。记住，后天的努力同样重要。建议你：1) 提升专业技能增加收入；2) 锻炼身体保持健康；3) 学习沟通技巧改善人际关系；4) 保持积极乐观的心态。记住，每一点努力都不会白费！";
        }
    }

    @Override
    public List<TalentSimulationLog> getSimulationLogs(Long userId, String simulationType) {
        if (simulationType != null && !simulationType.isEmpty()) {
            return talentSimulationLogMapper.selectByUserIdAndType(userId, simulationType);
        }
        return talentSimulationLogMapper.selectByUserId(userId);
    }

    // Admin methods
    @Override
    public List<UserTalent> getAllUserTalents() {
        return userTalentMapper.selectAll();
    }

    @Override
    public Map<String, Object> getTalentStatistics() {
        List<UserTalent> allTalents = userTalentMapper.selectAll();
        Map<String, Object> stats = new HashMap<>();
        
        if (allTalents.isEmpty()) {
            stats.put("totalUsers", 0);
            return stats;
        }
        
        int totalAppearance = 0, totalIntelligence = 0, totalWealth = 0;
        int totalHealth = 0, totalEmotional = 0, totalLuck = 0;
        int totalPointsUsed = 0;
        
        int maxAppearance = 0, maxIntelligence = 0, maxWealth = 0;
        int maxHealth = 0, maxEmotional = 0, maxLuck = 0;
        
        for (UserTalent talent : allTalents) {
            totalAppearance += talent.getAppearance();
            totalIntelligence += talent.getIntelligence();
            totalWealth += talent.getWealth();
            totalHealth += talent.getHealth();
            totalEmotional += talent.getEmotional();
            totalLuck += talent.getLuck();
            totalPointsUsed += talent.getTotalPointsUsed();
            
            maxAppearance = Math.max(maxAppearance, talent.getAppearance());
            maxIntelligence = Math.max(maxIntelligence, talent.getIntelligence());
            maxWealth = Math.max(maxWealth, talent.getWealth());
            maxHealth = Math.max(maxHealth, talent.getHealth());
            maxEmotional = Math.max(maxEmotional, talent.getEmotional());
            maxLuck = Math.max(maxLuck, talent.getLuck());
        }
        
        int userCount = allTalents.size();
        
        stats.put("totalUsers", userCount);
        stats.put("totalPointsUsed", totalPointsUsed);
        stats.put("averagePointsUsed", String.format("%.2f", (double) totalPointsUsed / userCount));
        
        stats.put("averages", Arrays.asList(
            Map.of("name", "颜值", "average", String.format("%.2f", (double) totalAppearance / userCount), "max", maxAppearance),
            Map.of("name", "智商", "average", String.format("%.2f", (double) totalIntelligence / userCount), "max", maxIntelligence),
            Map.of("name", "财运", "average", String.format("%.2f", (double) totalWealth / userCount), "max", maxWealth),
            Map.of("name", "健康", "average", String.format("%.2f", (double) totalHealth / userCount), "max", maxHealth),
            Map.of("name", "情商", "average", String.format("%.2f", (double) totalEmotional / userCount), "max", maxEmotional),
            Map.of("name", "运气", "average", String.format("%.2f", (double) totalLuck / userCount), "max", maxLuck)
        ));
        
        return stats;
    }
}
