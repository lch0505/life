package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnnualGoal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer year;
    private String title;
    private String description;
    private BigDecimal progress;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer priority;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
