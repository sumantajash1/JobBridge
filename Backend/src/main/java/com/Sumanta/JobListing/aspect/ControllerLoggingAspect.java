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
public class ControllerLoggingAspect {

    @Before("execution(* com.Sumanta.JobListing.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Controller method entered : {} with args : {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(value = "execution(* com.Sumanta.JobListing.controller..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Controller method completed : {} Returned {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(value = "execution(* com.Sumanta.JobListing.controller..*(..))", throwing = "exception")
    public void logAfterThrowingException(JoinPoint joinPoint, Throwable exception) {
        log.warn("Exception in controller method : {} | Message : {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}
