package com.dtstack.engine.master.druid;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author xinge
 */
@Aspect
@Component
public class DruidAspect {

    private final DruidDataSourceService druidDataSourceService;
    public DruidAspect(DruidDataSourceService druidDataSourceService){
        this.druidDataSourceService = druidDataSourceService;
    }

    @Around("@annotation(com.dtstack.engine.master.druid.DtDruidRemoveAbandoned)")
    public Object druidDatasourceEnhance(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            druidDataSourceService.forbidRemoveAbandoned();
            return joinPoint.proceed();
        } finally {
            druidDataSourceService.releaseRemoveAbandoned();
        }
    }
}
