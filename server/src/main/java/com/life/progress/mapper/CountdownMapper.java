package com.life.progress.mapper;

import com.life.progress.entity.Countdown;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CountdownMapper {

    Countdown selectById(@Param("id") Long id);

    List<Countdown> selectByUserId(@Param("userId") Long userId);

    List<Countdown> selectByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);

    List<Countdown> selectAll();

    int insert(Countdown countdown);

    int update(Countdown countdown);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updateTop(@Param("id") Long id, @Param("isTop") Integer isTop);
}
