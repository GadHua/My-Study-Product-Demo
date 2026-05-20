package com.gadhub.overseasproduct.util;


public class DesensitizationUtil {


    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }


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
