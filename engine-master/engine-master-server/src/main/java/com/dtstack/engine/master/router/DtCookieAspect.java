package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.util.PublicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author <a href="mailto:qianyi@dtstack.com">shifang At 袋鼠云</a>.
 * @description 请求切面，处理 base 参数信息
 * @date 2020/8/12-09:24
 */
@Slf4j
@Aspect
@Component
public class DtCookieAspect {

    @Pointcut(value = "execution(public * com.dtstack..controller..*.*(..))")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        if (ArrayUtils.isNotEmpty(parameters)) {
            // 只有post请求，并且controller方法中第一个参数加了RequestBody注解才进行参数处理
            // 备注：文件上传相关的请求参数处理会放在FileUploadAspect中单独处理
            RequestBody body = parameters[0].getAnnotation(RequestBody.class);
            if (null != body) {
                Object argOjb = args[0];
                if (null == argOjb) {
                    argOjb = parameters[0].getType().newInstance();
                }

                JSONObject bodyJson = (JSONObject) request.getAttribute(DtRequestWrapperFilter.DT_REQUEST_BODY);
                Object cjObj = JSON.toJavaObject(bodyJson, argOjb.getClass());

                // 做这步的原因是通用参数都从cookie和session中取，但是部分接口会传回tenantId和projectId
                PublicUtil.copyPropertiesIgnoreNull(argOjb, cjObj);
                PublicUtil.copyPropertiesIgnoreNull(cjObj, argOjb);
                args[0] = argOjb;
                return point.proceed(args);
            }
        }
        return point.proceed();
    }

}
