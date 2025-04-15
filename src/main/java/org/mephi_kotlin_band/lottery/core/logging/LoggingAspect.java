package org.mephi_kotlin_band.lottery.core.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    @Before("restControllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.info("➡️  Called: {}.{}() with args = {}",
                 methodSignature.getDeclaringType().getSimpleName(),
                 methodSignature.getName(),
                 Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "restControllerMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.info("⬅️  Returned from: {}.{}() with result = {}",
                 methodSignature.getDeclaringType().getSimpleName(),
                 methodSignature.getName(),
                 result);
    }

    @AfterThrowing(value = "restControllerMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.error("💥 Exception in: {}.{}() - {}",
                  methodSignature.getDeclaringType().getSimpleName(),
                  methodSignature.getName(),
                  ex.getMessage(), ex);
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.info("⚙️  Service call: {}.{}() with args = {}",
                 methodSignature.getDeclaringType().getSimpleName(),
                 methodSignature.getName(),
                 Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.info("✅ Service finished: {}.{}() with result = {}",
                 methodSignature.getDeclaringType().getSimpleName(),
                 methodSignature.getName(),
                 result);
    }

    @AfterThrowing(value = "serviceMethods()", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.error("❌ Exception in service: {}.{}() - {}",
                  methodSignature.getDeclaringType().getSimpleName(),
                  methodSignature.getName(),
                  ex.getMessage(), ex);
    }
}