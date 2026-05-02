package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TalentSimulationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String simulationType;
    private String simulationResult;
    private String talentSnapshot;
    private LocalDateTime createTime;
}
