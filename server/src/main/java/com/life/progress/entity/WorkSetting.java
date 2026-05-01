package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private LocalDate joinDate;
    private Integer retirementAge;
    private BigDecimal monthlySalary;
    private Integer workDaysPerWeek;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
