package com.life.progress.mapper;

import com.life.progress.entity.TalentConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TalentConfigMapper {

    TalentConfig selectById(@Param("id") Long id);

    TalentConfig selectByKey(@Param("configKey") String configKey);

    List<TalentConfig> selectAll();

    int insert(TalentConfig talentConfig);

    int update(TalentConfig talentConfig);

    int deleteById(@Param("id") Long id);

    int deleteByKey(@Param("configKey") String configKey);
}
