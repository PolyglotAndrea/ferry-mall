package com.ferry.module.system.aop;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.service.PermissionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    private final PermissionService permissionService;

    public PermissionAspect(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Around("@annotation(requirePermission)")
    public Object around(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new FerryBusinessException(401, "未登录");
        }
        String permission = requirePermission.value();
        if (!permissionService.hasPermission(userId, permission)) {
            throw new FerryBusinessException(403, "无权限: " + permission);
        }
        return joinPoint.proceed();
    }

    private Long getCurrentUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        return null;
    }
}
