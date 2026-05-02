package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TalentConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String configKey;
    private String configValue;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
