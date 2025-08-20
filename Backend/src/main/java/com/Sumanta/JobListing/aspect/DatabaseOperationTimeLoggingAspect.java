package com.Sumanta.JobListing.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DatabaseOperationTimeLoggingAspect {

    @Around("execution(* com.Sumanta.JobListing.DAO..*(..))")
    public Object LogDBOperationTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionDuration = System.currentTimeMillis()-startTime;
        log.info("{} method completed it's Database operation in {}", joinPoint.getSignature().toShortString(), executionDuration);
        return result;
    }
}
