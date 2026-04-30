package com.life.progress.service;

import com.life.progress.entity.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    List<User> findByRole(Integer role);

    User register(User user);

    User login(String username, String password);

    boolean update(User user);

    boolean updateStatus(Long id, Integer status);

    boolean deleteById(Long id);

    boolean isUsernameExists(String username);

    boolean isEmailExists(String email);
}
