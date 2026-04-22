package com.gadhub.overseasproduct.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createTime;
}
