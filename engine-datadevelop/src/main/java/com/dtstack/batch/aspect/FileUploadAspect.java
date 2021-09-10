package com.dtstack.batch.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.master.router.DtRequestWrapperFilter;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        if (args.length < 2) {
            throw new RdosDefineException("upload method args less than 2.");
        }
        if (!MultipartFile.class.isAssignableFrom(args[1].getClass())){
            throw new RdosDefineException("upload method args[1] not AssignableFrom MultipartFile.");
        }

        MultipartFile file = (MultipartFile) args[1];
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

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

            JSONObject bodyJson = (JSONObject) request.getAttribute(DtRequestWrapperFilter.DT_REQUEST_BODY);

            Object cjObj = JSON.toJavaObject(bodyJson, clazz);
            PublicUtil.copyPropertiesIgnoreNull(args[0], cjObj);
            PublicUtil.copyPropertiesIgnoreNull(cjObj, args[0]);
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
}
