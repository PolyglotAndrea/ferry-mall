package com.ferry.framework.log.aspect;

import com.ferry.framework.log.config.LogProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
@Aspect
public class SlowLogAspect {

    private final LogProperties logProperties;

    public SlowLogAspect(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Around("@within(org.springframework.stereotype.Service) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            if (cost >= logProperties.getSlowLog().getThresholdMs()) {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String className = signature.getDeclaringType().getSimpleName();
                String methodName = signature.getName();
                StringBuilder sb = new StringBuilder();
                sb.append("[SLOW-LOG] ").append(className).append(".").append(methodName)
                    .append(" cost=").append(cost).append("ms");
                if (logProperties.getSlowLog().isPrintArgs()) {
                    sb.append(" args=").append(truncate(argsToString(joinPoint.getArgs())));
                }
                log.warn(sb.toString());
            }
        }
    }

    private String argsToString(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(argToString(args[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    private String argToString(Object arg) {
        if (arg == null) return "null";
        try {
            String s = arg.toString();
            return s.length() > 200 ? s.substring(0, 200) + "..." : s;
        } catch (Exception e) {
            return arg.getClass().getSimpleName();
        }
    }

    private String truncate(String s) {
        int max = logProperties.getSlowLog().getResultMaxLength();
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
