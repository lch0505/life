package com.life.progress.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private LocalDate birthDate;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
