package com.gadhub.overseasproduct.util;

/**
 * 数据脱敏工具类
 */
public class DesensitizationUtil {

    /**
     * 手机号脱敏
     * 13812345678 -> 138****5678
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * H2845244813@outlook.com -> H***@outlook.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return "***" + email.substring(atIndex);
        }

        String prefix = email.substring(0, 1);
        String suffix = email.substring(atIndex);
        return prefix + "***" + suffix;
    }

    /**
     * 用户名脱敏
     * 张三 -> 张*
     * 李四丰 -> 李**
     */
    public static String maskUsername(String username) {
        if (username == null || username.isEmpty()) {
            return username;
        }

        if (username.length() == 1) {
            return "*";
        }

        return username.charAt(0) + "*".repeat(username.length() - 1);
    }
}
