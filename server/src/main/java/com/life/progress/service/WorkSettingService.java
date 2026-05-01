package com.life.progress.service;

import com.life.progress.entity.WorkSetting;

public interface WorkSettingService {

    WorkSetting findByUserId(Long userId);

    boolean create(WorkSetting workSetting);

    boolean update(WorkSetting workSetting);

    boolean saveOrUpdate(WorkSetting workSetting);

    boolean deleteByUserId(Long userId);
}
