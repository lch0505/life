package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserTalent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Integer appearance;
    private Integer intelligence;
    private Integer wealth;
    private Integer health;
    private Integer emotional;
    private Integer luck;
    private Integer totalPointsUsed;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
