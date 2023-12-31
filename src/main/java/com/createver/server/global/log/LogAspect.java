package com.createver.server.global.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class LogAspect {

    private final LogTracer logTracer;

    @Around("execution(* com.createver.server..*Controller.*(..)) && !execution(* com.createver.server.domain.health.HealthCheckController.*(..)) || execution(* com.createver.server..*Service.*(..)) || execution(* com.createver.server..*Repository.*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        boolean hasException = false;
        try {
            status = logTracer.begin(joinPoint.getSignature().toString());
            return joinPoint.proceed();
        } catch (Exception ex) {
            logTracer.exception(status, ex);
            hasException = true;
            throw ex;
        } finally {
            if(!hasException) logTracer.end(status);
        }
    }
}
