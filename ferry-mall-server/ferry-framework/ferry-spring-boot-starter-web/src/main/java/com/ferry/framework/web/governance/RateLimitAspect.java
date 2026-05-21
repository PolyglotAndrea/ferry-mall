package com.ferry.framework.web.governance;

import com.ferry.framework.web.exception.FerryBusinessException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {
    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = rateLimit.key().isBlank() ? joinPoint.getSignature().toShortString() : rateLimit.key();
        long currentSecond = Instant.now().getEpochSecond();
        WindowCounter counter = counters.compute(key, (ignored, existing) -> existing == null || existing.second != currentSecond ? new WindowCounter(currentSecond) : existing);
        if (counter.count.incrementAndGet() > rateLimit.permitsPerSecond()) {
            throw new FerryBusinessException(429, "请求过于频繁，请稍后再试");
        }
        return joinPoint.proceed();
    }

    private static final class WindowCounter {
        private final long second;
        private final AtomicInteger count = new AtomicInteger();
        private WindowCounter(long second) { this.second = second; }
    }
}
