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
    USER_NOT_FOUND(10008, "用户不存在"),

    // 商品相关错误 11000-11999
    PRODUCT_NOT_FOUND(11001, "商品不存在"),
    PRODUCT_ID_REQUIRED(11002, "商品ID不能为空"),

    // 订单相关错误 12000-12999
    ORDER_NOT_FOUND(12001, "订单不存在"),
    ORDER_ID_REQUIRED(12002, "订单ID不能为空"),
    ORDER_STATUS_ERROR(12003, "订单状态不正确"),
    PRODUCT_STOCK_INSUFFICIENT(12005, "商品库存不足"),
    PRODUCT_OFF_SHELF(12006, "商品已下架"),
    ORDER_NOT_BELONG_TO_USER(12007, "订单不属于当前用户"),

    // 购物车相关错误 13000-13999
    CART_PRODUCT_NOT_FOUND(13001, "商品不存在"),
    CART_QUANTITY_INVALID(13002, "数量必须大于0"),
    PRODUCT_ALREADY_IN_CART(13003, "商品已存在购物车中"),
    CART_NOT_FOUND(13004, "购物车不存在"),

    // 支付相关错误 14000-14999
    PAYMENT_ORDER_NOT_FOUND(14001, "订单不存在"),
    PAYMENT_ORDER_ALREADY_PAID(14002, "订单已支付"),
    PAYMENT_ORDER_NOT_BELONG_TO_USER(14003, "订单不属于当前用户"),
    UNSUPPORTED_PAYMENT_METHODS(14004, "不支持的支付方式"),

    // 认证授权相关错误 15000-15999
    UNAUTHORIZED(15001, "用户未登录或Token无效"),
    // 系统错误 50000-50999
    SYSTEM_ERROR(50000, "系统异常");



    private final Integer code;
    private final String message;
}
