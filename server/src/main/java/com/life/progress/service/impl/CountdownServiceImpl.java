package com.life.progress.service.impl;

import com.life.progress.entity.Countdown;
import com.life.progress.mapper.CountdownMapper;
import com.life.progress.service.CountdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CountdownServiceImpl implements CountdownService {

    @Autowired
    private CountdownMapper countdownMapper;

    @Override
    public Countdown findById(Long id) {
        return countdownMapper.selectById(id);
    }

    @Override
    public List<Countdown> findByUserId(Long userId) {
        return countdownMapper.selectByUserId(userId);
    }

    @Override
    public List<Countdown> findByUserIdAndCategory(Long userId, String category) {
        return countdownMapper.selectByUserIdAndCategory(userId, category);
    }

    @Override
    public List<Countdown> findAll() {
        return countdownMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(Countdown countdown) {
        if (countdown.getStatus() == null) {
            countdown.setStatus(1);
        }
        if (countdown.getIsTop() == null) {
            countdown.setIsTop(0);
        }
        if (countdown.getColor() == null) {
            countdown.setColor("#1890ff");
        }
        return countdownMapper.insert(countdown) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Countdown countdown) {
        return countdownMapper.update(countdown) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        return countdownMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTop(Long id, Integer isTop) {
        return countdownMapper.updateTop(id, isTop) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return countdownMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        return countdownMapper.deleteByUserId(userId) > 0;
    }
}
