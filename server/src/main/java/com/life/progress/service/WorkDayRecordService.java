package com.life.progress.service;

import com.life.progress.entity.WorkDayRecord;

import java.time.LocalDate;
import java.util.List;

public interface WorkDayRecordService {

    WorkDayRecord findById(Long id);

    WorkDayRecord findByUserIdAndDate(Long userId, LocalDate workDate);

    List<WorkDayRecord> findByUserIdAndYear(Long userId, Integer year);

    List<WorkDayRecord> findByUserIdAndMonth(Long userId, Integer year, Integer month);

    List<WorkDayRecord> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    boolean create(WorkDayRecord workDayRecord);

    boolean update(WorkDayRecord workDayRecord);

    boolean saveOrUpdate(WorkDayRecord workDayRecord);

    boolean deleteById(Long id);

    boolean deleteByUserId(Long userId);

    int countByStatus(Long userId, Integer year, Integer status);
}
