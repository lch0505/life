package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LifeEventConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer eventType;
    private String eventName;
    private String eventDescription;
    private String icon;
    private String color;
    private String impactType;
    private Integer impactValue;
    private Integer weight;
    private Integer minDays;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
