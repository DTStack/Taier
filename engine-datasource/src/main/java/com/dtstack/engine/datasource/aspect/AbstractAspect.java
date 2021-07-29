package com.dtstack.engine.datasource.aspect;

import org.aspectj.lang.annotation.Pointcut;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
public abstract class AbstractAspect {

    @Pointcut(value = "execution(public * com.dtstack..controller..*.*(..))")
    public void pointCut() {}
}
