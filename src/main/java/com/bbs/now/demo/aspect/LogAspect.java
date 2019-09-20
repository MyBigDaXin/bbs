package com.bbs.now.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.bbs.now.demo.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            sb.append("arg:" + arg.toString() + "|");
        }
        LOGGER.info("before method:" + sb.toString());
    }

    @After("execution(* com.bbs.now.demo.controller.*Controller.*(..))")
    public void afterMethod(JoinPoint joinPoint){
        LOGGER.info("after method" + new Date());
    }

}
