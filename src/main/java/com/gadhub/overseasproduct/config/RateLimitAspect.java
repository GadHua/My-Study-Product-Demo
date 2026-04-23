package com.gadhub.overseasproduct.config;

import com.gadhub.overseasproduct.common.annotation.RateLimit;
import com.gadhub.overseasproduct.common.constant.ErrorCode;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private final ConcurrentHashMap<String, AtomicInteger> limitMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> timeMap = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateKey(joinPoint, rateLimit);

        long currentTime = System.currentTimeMillis();
        long windowStart = timeMap.getOrDefault(key, 0L);

        if (currentTime - windowStart > rateLimit.time() * 1000L) {
            timeMap.put(key, currentTime);
            limitMap.put(key, new AtomicInteger(0));
        }

        AtomicInteger count = limitMap.get(key);
        if (count.incrementAndGet() > rateLimit.count()) {
            log.warn("限流触发: key={}, count={}", key, count.get());
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED.getCode(), rateLimit.message());
        }

        try {
            return joinPoint.proceed();
        } finally {
            count.decrementAndGet();
        }
    }

    private String generateKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String key = rateLimit.key();

        if (key.isEmpty()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            key = method.getDeclaringClass().getName() + "." + method.getName();
        }

        return "rate_limit:" + key;
    }
}
