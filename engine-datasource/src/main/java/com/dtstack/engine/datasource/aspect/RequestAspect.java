package com.dtstack.engine.datasource.aspect;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import dt.insight.plat.lang.web.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/16
 * @desc 请求信息输出切面
 */
@Aspect
@Slf4j
@Component
@Order()
public class RequestAspect extends AbstractAspect {

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
            result = joinPoint.proceed();
        } finally {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            long handleTime = System.currentTimeMillis() - startTime;
            String url = req.getRequestURI();
            String param = getRequestParam(joinPoint);
            String header = JSON.toJSONString(getHeaders(req));
            log.info("url:[{}] param:[{}] header:[{}] handleTime:[{}]", url, param, header, handleTime);
            if (result != null){
                R r = (R) result;
                r.setSpace(startTime);
            }
        }
        return result;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParam(ProceedingJoinPoint point) {
        Object[] methodArgs = point.getArgs();
        Parameter[] parameters = ((MethodSignature) point.getSignature()).getMethod().getParameters();
        String requestStr;
        try {
            requestStr = logParam(parameters, methodArgs);
        } catch (Exception e) {
            requestStr = "获取参数失败";
        }
        return requestStr;
    }

    /**
     * 拼接请求参数
     */
    private String logParam(Parameter[] paramsArgsName, Object[] paramsArgsValue) {
        if (ArrayUtils.isEmpty(paramsArgsName) || ArrayUtils.isEmpty(paramsArgsValue)) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < paramsArgsValue.length; i++) {
            String name = paramsArgsName[i].getName();
            Object value = paramsArgsValue[i];
            buffer.append(name).append("=");
            if (value instanceof String) {
                buffer.append(value).append(",");
            } else {
                buffer.append(JSON.toJSONString(value)).append(",");
            }
        }
        return buffer.toString();
    }

    /**
     * 获取所有的请求头
     * @param request
     * @return
     */
    public static Map<String,String> getHeaders(HttpServletRequest request){
        Map<String,String> headerMap = Maps.newHashMap();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            headerMap.put(headerName,request.getHeader(headerName));
        }
        return headerMap;
    }
}
