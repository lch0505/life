package com.life.progress.mapper;

import com.life.progress.entity.WorkSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkSettingMapper {

    WorkSetting selectByUserId(@Param("userId") Long userId);

    int insert(WorkSetting workSetting);

    int update(WorkSetting workSetting);

    int deleteByUserId(@Param("userId") Long userId);
}
