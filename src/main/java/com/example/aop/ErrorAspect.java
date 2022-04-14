package com.example.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@Aspect
public class ErrorAspect {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @AfterThrowing(pointcut = "execution(* com.example.controller.LinkController.*(..))",
            throwing = "exception")
    public void afterThrowingException(Throwable exception){
        log.error("Unexpected error! " + exception);
    }
}
