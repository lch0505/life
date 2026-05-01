package com.life.progress.mapper;

import com.life.progress.entity.WorkDayRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WorkDayRecordMapper {

    WorkDayRecord selectById(@Param("id") Long id);

    WorkDayRecord selectByUserIdAndDate(@Param("userId") Long userId, @Param("workDate") LocalDate workDate);

    List<WorkDayRecord> selectByUserIdAndYear(@Param("userId") Long userId, @Param("year") Integer year);

    List<WorkDayRecord> selectByUserIdAndMonth(@Param("userId") Long userId, @Param("year") Integer year, @Param("month") Integer month);

    List<WorkDayRecord> selectByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    int insert(WorkDayRecord workDayRecord);

    int update(WorkDayRecord workDayRecord);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    int countByUserIdAndStatus(@Param("userId") Long userId, @Param("year") Integer year, @Param("status") Integer status);
}
