package com.gadhub.overseasproduct.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 注册用户数据传输对象
@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名

    @NotBlank(message = "密码不能为空")
    private String password; // 密码

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword; // 确认密码

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email; // 邮箱

}
