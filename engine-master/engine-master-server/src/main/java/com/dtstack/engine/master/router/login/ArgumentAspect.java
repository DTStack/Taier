package com.dtstack.engine.master.router.login;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/9/22
 */
@Aspect
@Component
public class ArgumentAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestAnnotation() {
    }

    @Pointcut("execution(* com.dtstack.engine.master.controller.*.*(..))")
    public void executeController() {
    }

    @Around(value = "requestAnnotation()&&executeController()")
    public Object ArgumentResolve(ProceedingJoinPoint joinPoint) {
        return null;
    }
}
