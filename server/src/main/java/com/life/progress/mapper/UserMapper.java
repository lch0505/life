package com.life.progress.mapper;

import com.life.progress.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    List<User> selectAll();

    List<User> selectByRole(@Param("role") Integer role);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
