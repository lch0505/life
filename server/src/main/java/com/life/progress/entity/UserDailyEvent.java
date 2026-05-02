package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDailyEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long eventId;
    private LocalDate eventDate;
    private Integer eventType;
    private String eventName;
    private String eventDescription;
    private String impactType;
    private Integer impactValue;
    private Integer isApplied;
    private LocalDateTime createTime;
}
