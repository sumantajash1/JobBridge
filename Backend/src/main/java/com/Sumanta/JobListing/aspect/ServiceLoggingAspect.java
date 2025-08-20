package com.Sumanta.JobListing.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Before("execution(* com.Sumanta.JobListing.Service.impl..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Service method entered : {} with args {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(value = "execution(* com.Sumanta.JobListing.Service.impl..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Service method completed : {} returning {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(value = "execution(* com.Sumanta.JobListing.Service.impl..*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.warn("Exception in service method : {} throwing exception : {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}
