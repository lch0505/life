package com.life.progress.service.impl;

import com.life.progress.entity.WorkSetting;
import com.life.progress.mapper.WorkSettingMapper;
import com.life.progress.service.WorkSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkSettingServiceImpl implements WorkSettingService {

    @Autowired
    private WorkSettingMapper workSettingMapper;

    @Override
    public WorkSetting findByUserId(Long userId) {
        return workSettingMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(WorkSetting workSetting) {
        if (workSetting.getRetirementAge() == null) {
            workSetting.setRetirementAge(65);
        }
        if (workSetting.getWorkDaysPerWeek() == null) {
            workSetting.setWorkDaysPerWeek(5);
        }
        return workSettingMapper.insert(workSetting) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(WorkSetting workSetting) {
        return workSettingMapper.update(workSetting) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(WorkSetting workSetting) {
        WorkSetting existing = workSettingMapper.selectByUserId(workSetting.getUserId());
        if (existing == null) {
            return create(workSetting);
        } else {
            workSetting.setId(existing.getId());
            return update(workSetting);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        return workSettingMapper.deleteByUserId(userId) > 0;
    }
}
