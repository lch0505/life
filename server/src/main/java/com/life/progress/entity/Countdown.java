package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class Countdown implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String category;
    private LocalDate targetDate;
    private LocalTime targetTime;
    private String color;
    private String icon;
    private Integer isTop;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
