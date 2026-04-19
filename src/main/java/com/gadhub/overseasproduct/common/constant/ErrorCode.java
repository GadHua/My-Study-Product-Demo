package com.gadhub.overseasproduct.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    // 用户相关错误 10000-10999
    EMAIL_ALREADY_EXISTS(10001, "邮箱已被注册"),
    USERNAME_ALREADY_EXISTS(10002, "用户名已存在"),
    PASSWORD_MISMATCH(10003, "两次密码输入不一致"),
    INVALID_EMAIL(10004, "邮箱格式不正确"),
    PASSWORD_TOO_SHORT(10005, "密码长度不能少于8位"),
    PASSWORD_FORMAT_ERROR(10006, "密码必须包含字母和数字"),
    LOGIN_FAILED(10007, "用户名或者密码错误"),

    // 商品相关错误 11000-11999
    PRODUCT_NOT_FOUND(11001, "商品不存在"),
    PRODUCT_ID_REQUIRED(11002, "商品ID不能为空"),


    // 系统错误 50000-50999
    SYSTEM_ERROR(50000, "系统异常");

    private final Integer code;
    private final String message;
}
