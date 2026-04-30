package com.life.progress.mapper;

import com.life.progress.entity.AnnualGoal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnualGoalMapper {

    AnnualGoal selectById(@Param("id") Long id);

    List<AnnualGoal> selectByUserId(@Param("userId") Long userId);

    List<AnnualGoal> selectByUserIdAndYear(@Param("userId") Long userId, @Param("year") Integer year);

    List<AnnualGoal> selectAll();

    int insert(AnnualGoal annualGoal);

    int update(AnnualGoal annualGoal);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    int updateProgress(@Param("id") Long id, @Param("progress") java.math.BigDecimal progress);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
