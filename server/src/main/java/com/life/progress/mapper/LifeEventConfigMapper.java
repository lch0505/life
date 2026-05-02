package com.life.progress.mapper;

import com.life.progress.entity.LifeEventConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LifeEventConfigMapper {

    LifeEventConfig selectById(@Param("id") Long id);

    List<LifeEventConfig> selectAll();

    List<LifeEventConfig> selectByEventType(@Param("eventType") Integer eventType);

    List<LifeEventConfig> selectActive();

    List<LifeEventConfig> selectActiveByType(@Param("eventType") Integer eventType);

    int insert(LifeEventConfig config);

    int update(LifeEventConfig config);

    int deleteById(@Param("id") Long id);
}
