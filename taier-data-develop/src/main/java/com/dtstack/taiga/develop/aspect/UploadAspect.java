package com.dtstack.taiga.develop.aspect;

import com.dtstack.taiga.common.util.PublicUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 上传文件切面
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */

@Aspect
@Component
public class UploadAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadAspect.class);

    private static String uploadLocation = System.getProperty("user.dir") + File.separator + "upload";

    @Pointcut("@annotation(com.dtstack.taiga.develop.annotation.FileUpload)")
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void before(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MultipartFile file = (MultipartFile) args[0];
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Map<String, Object> paramMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            paramMap.put(cookie.getName(),cookie.getValue());
        }
        if (file != null) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String temPath = uploadLocation + File.separator + filename;
            File tempFile = new File(temPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            file.transferTo(tempFile);
            Pair<String, String> pair = new MutablePair(file.getOriginalFilename(), temPath);
            paramMap.put("resource", pair);
        }
        Map<String, Object> configMap = (Map<String, Object>) args[1];
        configMap.putAll(paramMap);
        PublicUtil.removeEmptyValue(configMap);
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            configMap.putIfAbsent(paramName, request.getParameter(paramName));
        }
    }

    @After("logPointCut()")
    public void after(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> paramMap = (Map) args[1];
        Pair<String, String> pair = ((Pair<String, String>) paramMap.get("resource"));
        if (pair == null) {
            return;
        }
        String filePath = pair.getRight();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }



}
