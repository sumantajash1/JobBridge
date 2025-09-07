package com.Sumanta.JobListing.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {
//
//    @Before("execution(* com.Sumanta.JobListing.controller..*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        log.info("Controller method entered : {} with args : {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
//    }
//
//    @AfterReturning(value = "execution(* com.Sumanta.JobListing.controller..*(..))", returning = "result")
//    public void logAfterReturning(JoinPoint joinPoint, Object result) {
//        log.info("Controller method completed : {} Returned {}", joinPoint.getSignature().toShortString(), result);
//    }
//
    @AfterThrowing(value = "execution(* com.Sumanta.JobListing.controller..*(..))", throwing = "exception")
    public void logAfterThrowingException(JoinPoint joinPoint, Throwable exception) {
        log.warn("Exception in controller method : {} | Message : {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }

    @Around("execution(* com.Sumanta.JobListing.controller..*(..))")
    public Object LogDBOperationTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionDuration = System.currentTimeMillis()-startTime;
        log.info("{} method completed it's Controller operation in {}", joinPoint.getSignature().toShortString(), executionDuration);
        return result;
    }
}
