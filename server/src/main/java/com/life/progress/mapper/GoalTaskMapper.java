package com.life.progress.mapper;

import com.life.progress.entity.GoalTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoalTaskMapper {

    GoalTask selectById(@Param("id") Long id);

    List<GoalTask> selectByGoalId(@Param("goalId") Long goalId);

    List<GoalTask> selectByUserId(@Param("userId") Long userId);

    List<GoalTask> selectAll();

    int insert(GoalTask goalTask);

    int update(GoalTask goalTask);

    int deleteById(@Param("id") Long id);

    int deleteByGoalId(@Param("goalId") Long goalId);

    int deleteByUserId(@Param("userId") Long userId);

    int updateProgress(@Param("id") Long id, @Param("progress") java.math.BigDecimal progress);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
