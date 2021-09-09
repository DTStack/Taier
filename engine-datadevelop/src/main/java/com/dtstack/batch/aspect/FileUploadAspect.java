package com.dtstack.batch.aspect;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.web.resource.vo.query.BatchResourceAddVO;
import com.dtstack.dtcenter.common.util.PublicUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @company:www.dtstack.com
 * @Author:beihai
 * @Date:2020-12-29 16:11
 * @Description: 文件上传切面
 */
@Aspect
@Component
public class FileUploadAspect {

    @Pointcut("@annotation(com.dtstack.engine.common.annotation.FileUpload)")
    public void fileUploadPointCut() {

    }

    /**
     * 文件上传前处理，生产临时文件
     * @param joinPoint
     * @throws Throwable
     */
    @Before("fileUploadPointCut()")
    public void before(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MultipartFile file = (MultipartFile) args[1];
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        Cookie[] cookies = request.getCookies();
        Map<String,Object> ckJson = new HashMap<>();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                ckJson.put(cookie.getName(), cookie.getValue());
            }
        }

        // 做这步的原因是通用参数都从cookie和session中取，但是部分接口会传回tenantId和projectId
        if (!Objects.isNull(args[0])) {
            PublicUtil.copyPropertiesIgnoreNull(args[0], ckJson);
            PublicUtil.copyPropertiesIgnoreNull(ckJson, args[0]);
        }
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            String tmpPath = System.getProperty("user.dir") + File.separator + "upload" + File.separator + fileName;
            File tmpFile = new File(tmpPath);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            file.transferTo(tmpFile);
            Class clazz = args[0].getClass();
            Method tmpPathMethod = clazz.getDeclaredMethod("setTmpPath", String.class);
            Method originalFilenameMethod = clazz.getDeclaredMethod("setOriginalFilename", String.class);
            tmpPathMethod.invoke(args[0], tmpPath);
            originalFilenameMethod.invoke(args[0], originalFilename);
        }
    }

    /**
     * 文件上传之后处理，删除临时文件
     * @param joinPoint
     */
    @After("fileUploadPointCut()")
    public void after(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Class clazz = args[0].getClass();
        Field tmpPathField = clazz.getDeclaredField("tmpPath");
        tmpPathField.setAccessible(true);
        Object tmpPath = tmpPathField.get(args[0]);
        if (tmpPath != null) {
            File file = new File(tmpPath.toString());
            if (file.exists()) {
                file.delete();
            }
        }
    }


    public static void main(String[] args) {
        Map<String,Object> ckJson = new HashMap<>();
        ckJson.put("projectId", 1);
        BatchResourceAddVO vo = new BatchResourceAddVO();
        PublicUtil.copyPropertiesIgnoreNull(ckJson, vo);
        System.out.println(vo);
    }
}
