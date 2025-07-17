package com.college.PlacementApl.aspect;




import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.college.PlacementApl.Controller..*(..))")
    public void controllerMethods() {}


    @Pointcut("execution(* com.college.PlacementApl.Service..*(..))")
    public void serviceMethods() {}


    @Before("controllerMethods(), serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments {}-------------------------------------->>>", 
                    joinPoint.getSignature().toShortString(),
                    joinPoint.getArgs());

    }

    @AfterReturning(pointcut = "controllerMethods(), serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} returned: {}--------------------------------------->>>>>>", 
                    joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(pointcut = "controllerMethods(), serviceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Exception in method {} with cause {}", 
                     joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}

