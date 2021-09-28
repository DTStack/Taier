/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.datasource.aspect;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Aspect
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingAspect {

    /**
     * 拦截所有controller包下的方法
     */
    @Pointcut("execution(* com.dtstack.engine.datasource..*.*Controller.*(..))")
    private void controllerMethod() {
    }

    @Around("controllerMethod()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
            result = joinPoint.proceed();
        } catch (Exception e) {
            //如果有异常继续抛
            throw e;
        } finally {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            long handleTime = System.currentTimeMillis() - startTime;
            String clientIp = getRealIP(req);
            String url = req.getRequestURI();
            String param = getRequestParam(joinPoint);
            String header = JSON.toJSONString(getHeaders(req));
            log.info("clientIp:{} url:{} header:{} param:{} handleTime:{}", clientIp, url, header, param, handleTime);
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
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < paramsArgsValue.length; i++) {
            //参数名
            String name = paramsArgsName[i].getName();
            //参数值
            Object value = paramsArgsValue[i];
            buffer.append(name + "=");
            if (value instanceof String) {
                buffer.append(value + ",");
            } else {
                buffer.append(JSON.toJSONString(value) + ",");
            }
        }
        return buffer.toString();
    }

    /**
     * 获取请求的真实IP
     * @param request
     * @return
     */
    public static String getRealIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取所有的请求头
     * @param request
     * @return
     */
    public static Map<String,String> getHeaders(HttpServletRequest request){
        Map<String,String> headerMap = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            headerMap.put(headerName,request.getHeader(headerName));
        }
        return headerMap;
    }
}
