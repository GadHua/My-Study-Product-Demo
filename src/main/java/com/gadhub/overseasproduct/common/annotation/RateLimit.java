package com.gadhub.overseasproduct.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    String key() default "";

    int time() default 60;

    int count() default 10;

    String message() default "请求过于频繁，请稍后再试";
}
