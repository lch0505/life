package com.life.progress.service.impl;

import com.life.progress.entity.WorkDayRecord;
import com.life.progress.mapper.WorkDayRecordMapper;
import com.life.progress.service.WorkDayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkDayRecordServiceImpl implements WorkDayRecordService {

    @Autowired
    private WorkDayRecordMapper workDayRecordMapper;

    @Override
    public WorkDayRecord findById(Long id) {
        return workDayRecordMapper.selectById(id);
    }

    @Override
    public WorkDayRecord findByUserIdAndDate(Long userId, LocalDate workDate) {
        return workDayRecordMapper.selectByUserIdAndDate(userId, workDate);
    }

    @Override
    public List<WorkDayRecord> findByUserIdAndYear(Long userId, Integer year) {
        return workDayRecordMapper.selectByUserIdAndYear(userId, year);
    }

    @Override
    public List<WorkDayRecord> findByUserIdAndMonth(Long userId, Integer year, Integer month) {
        return workDayRecordMapper.selectByUserIdAndMonth(userId, year, month);
    }

    @Override
    public List<WorkDayRecord> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return workDayRecordMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(WorkDayRecord workDayRecord) {
        if (workDayRecord.getStatus() == null) {
            workDayRecord.setStatus(1);
        }
        return workDayRecordMapper.insert(workDayRecord) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(WorkDayRecord workDayRecord) {
        return workDayRecordMapper.update(workDayRecord) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(WorkDayRecord workDayRecord) {
        WorkDayRecord existing = workDayRecordMapper.selectByUserIdAndDate(
            workDayRecord.getUserId(), workDayRecord.getWorkDate());
        if (existing == null) {
            return create(workDayRecord);
        } else {
            workDayRecord.setId(existing.getId());
            return update(workDayRecord);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return workDayRecordMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        return workDayRecordMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public int countByStatus(Long userId, Integer year, Integer status) {
        return workDayRecordMapper.countByUserIdAndStatus(userId, year, status);
    }
}
