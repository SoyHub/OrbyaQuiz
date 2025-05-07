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

    private final LoggingUtils loggingUtils;

    @Pointcut("within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)" +
            " || within(com.orbyta_admission_quiz.service..*)" +
            " || within(com.orbyta_admission_quiz.repository..*)" +
            " || within(com.orbyta_admission_quiz.client..*)" +
            " || within(com.orbyta_admission_quiz.controller..*)" +
            " || execution(* org.springframework.web.client.RestTemplate.*(..))")
    private void applicationAndRestTemplatePointcut() {
    }

    @Around("applicationAndRestTemplatePointcut()")
    public Object logAroundWithDepth(ProceedingJoinPoint joinPoint) throws Throwable {
        loggingUtils.incrementDepth();

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        String entryArrows = loggingUtils.generateArrows(">");
        String argsJson = "[Args serialization failed]"; // Default

        if (log.isInfoEnabled()) {
            try {
                argsJson = loggingUtils.toJson(joinPoint.getArgs());
                log.info("{} {}.{}() argsJson={}", entryArrows, className, methodName, argsJson);
            } catch (Exception logEx) {
                log.warn("{} {}.{}() - Failed to serialize args to JSON: {}", entryArrows, className, methodName, logEx.getMessage());
                log.info("{} {}.{}() args=[Serialization Failed]", entryArrows, className, methodName);
            }
        }

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            String exitArrows = loggingUtils.generateArrows("<");

            if (log.isInfoEnabled()) {
                try {
                    String resultJson = loggingUtils.toJson(result);
                    log.info("{} {}.{}() time={}ms resultJson={}", exitArrows, className, methodName, executionTime, resultJson);
                } catch (Exception logEx) {
                    log.warn("{} {}.{}() time={}ms - Failed to serialize result to JSON: {}", exitArrows, className, methodName, executionTime, logEx.getMessage());
                    log.info("{} {}.{}() time={}ms result=[Serialization Failed]", exitArrows, className, methodName, executionTime);
                }
            }
            return result;
        } catch (Throwable e) {
            String errorArrows = loggingUtils.generateArrows("<");
            long executionTime = System.currentTimeMillis() - startTime;
            if (log.isDebugEnabled()) {
                log.debug("{} {}.{}() Exception Type: {}, Message: {} time={}ms with argsJson={}",
                        errorArrows, className, methodName, e.getClass().getSimpleName(), e.getMessage(), executionTime, argsJson);
            }
            throw e;
        } finally {
            int finalDepth = loggingUtils.decrementDepth();
            if (finalDepth == 0) {
                loggingUtils.removeDepth();
            }
        }
    }
}