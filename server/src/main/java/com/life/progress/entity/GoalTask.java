package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoalTask implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long goalId;
    private Long userId;
    private String title;
    private String description;
    private BigDecimal progress;
    private Integer status;
    private LocalDate deadline;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
