package com.life.progress.service;

import com.life.progress.entity.Countdown;

import java.util.List;

public interface CountdownService {

    Countdown findById(Long id);

    List<Countdown> findByUserId(Long userId);

    List<Countdown> findByUserIdAndCategory(Long userId, String category);

    List<Countdown> findAll();

    boolean create(Countdown countdown);

    boolean update(Countdown countdown);

    boolean updateStatus(Long id, Integer status);

    boolean updateTop(Long id, Integer isTop);

    boolean deleteById(Long id);

    boolean deleteByUserId(Long userId);
}
