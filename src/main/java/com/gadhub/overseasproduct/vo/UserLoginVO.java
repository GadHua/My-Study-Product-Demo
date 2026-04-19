package com.gadhub.overseasproduct.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private String userId;
    private String token;
    private String username;
    private String email;

}
