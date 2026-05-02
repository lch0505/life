package com.life.progress.mapper;

import com.life.progress.entity.TalentSimulationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TalentSimulationLogMapper {

    TalentSimulationLog selectById(@Param("id") Long id);

    List<TalentSimulationLog> selectByUserId(@Param("userId") Long userId);

    List<TalentSimulationLog> selectByUserIdAndType(
            @Param("userId") Long userId,
            @Param("simulationType") String simulationType);

    List<TalentSimulationLog> selectAll();

    int insert(TalentSimulationLog talentSimulationLog);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);
}
