package com.life.progress.mapper;

import com.life.progress.entity.UserTalent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserTalentMapper {

    UserTalent selectById(@Param("id") Long id);

    UserTalent selectByUserId(@Param("userId") Long userId);

    List<UserTalent> selectAll();

    int insert(UserTalent userTalent);

    int update(UserTalent userTalent);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);
}
