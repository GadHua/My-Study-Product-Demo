package com.gadhub.overseasproduct.dto;

import lombok.Data;

// 注册用户数据传输对象
@Data
public class UserRegisterDTO {

    private String username; // 用户名

    private String password; // 密码

    private String confirmPassword; // 确认密码

    private String email; // 邮箱

}
