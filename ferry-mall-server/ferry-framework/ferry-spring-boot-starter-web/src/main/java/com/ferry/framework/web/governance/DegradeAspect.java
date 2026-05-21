package com.ferry.framework.web.governance;

import com.ferry.framework.web.core.CommonResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DegradeAspect {
    @Around("@annotation(degrade)")
    public Object around(ProceedingJoinPoint joinPoint, Degrade degrade) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            if (CommonResult.class.isAssignableFrom(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getReturnType())) {
                return CommonResult.failed(503, degrade.message());
            }
            throw ex;
        }
    }
}
