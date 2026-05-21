package com.ferry.module.system.aop;

import com.ferry.module.system.dal.dataobject.SysOperateLogDO;
import com.ferry.module.system.service.OperateLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class OperateLogAspect {

    private final OperateLogService operateLogService;

    public OperateLogAspect(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        int success = 1;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            success = 0;
            throw t;
        } finally {
            long cost = System.currentTimeMillis() - start;
            try {
                saveLog(joinPoint, cost, success);
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long cost, int success) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        Object userId = SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;

        SysOperateLogDO log = new SysOperateLogDO();
        log.setUserId(userId instanceof Long ? (Long) userId : null);
        log.setModule(joinPoint.getTarget().getClass().getSimpleName());
        log.setName(joinPoint.getSignature().getName());
        log.setRequestMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURI());
        log.setUserIp(getClientIp(request));
        log.setDuration((int) cost);
        log.setResult(success);
        log.setCreatedAt(LocalDateTime.now());

        operateLogService.createLog(log);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
