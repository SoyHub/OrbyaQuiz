package com.orbyta_admission_quiz.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    @Pointcut("within(com.orbyta_admission_quiz.controller..*)" +
            " || within(com.orbyta_admission_quiz.service..*)" +
            " || within(com.orbyta_admission_quiz.repository..*)" +
            " || within(com.orbyta_admission_quiz.client..*)" +
            " || execution(* org.springframework.web.client.RestTemplate.*(..))")
    private void applicationAndRestTemplatePointcut() {
    }

    private final LoggingUtils loggingUtils;

    @Around("applicationAndRestTemplatePointcut()")
    public Object logAroundWithDepth(ProceedingJoinPoint joinPoint) throws Throwable {
        loggingUtils.incrementDepth();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        String entryArrows = loggingUtils.generateArrows(">");

        loggingUtils.logMethodEntry(joinPoint, className, methodName, entryArrows);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            loggingUtils.logMethodExit(result, className, methodName, executionTime, loggingUtils.generateArrows("<"));
            return result;
        }
        /* // Uncomment the following lines to log exceptions with depth
        catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - startTime;
            loggingUtils.logMethodException(e, className, methodName, executionTime, loggingUtils.generateArrows("<"), joinPoint.getArgs());
            throw e;
        } */ finally {
            loggingUtils.cleanupDepth();
        }
    }

}