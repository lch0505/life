package com.life.progress.mapper;

import com.life.progress.entity.UserDailyEvent;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserDailyEventMapper {

    UserDailyEvent selectById(@Param("id") Long id);

    UserDailyEvent selectByUserAndDate(@Param("userId") Long userId, @Param("eventDate") LocalDate eventDate);

    List<UserDailyEvent> selectByUserId(@Param("userId") Long userId);

    List<UserDailyEvent> selectByUserIdWithLimit(@Param("userId") Long userId, @Param("limit") Integer limit);

    int insert(UserDailyEvent event);

    int update(UserDailyEvent event);

    int deleteById(@Param("id") Long id);
}
