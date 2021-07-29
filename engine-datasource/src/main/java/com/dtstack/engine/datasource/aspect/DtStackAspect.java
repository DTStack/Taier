package com.dtstack.engine.datasource.aspect;

import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.engine.datasource.auth.MetaObjectHolder;
import com.dtstack.engine.datasource.param.BaseParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Slf4j
@Aspect
@Component
public class DtStackAspect extends AbstractAspect {

    private static final String UPLOAD_FORM_DATA = "multipart/form-data;";

    @Around(value = "pointCut()")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        BaseParam param = (BaseParam) request.getSession().getAttribute("param");
        //如果 是免登录接口 需要跳过 param赋值
        if (Objects.isNull(param)) {
            return point.proceed();
        }
        // 设置本地副本变量
        MetaObjectHolder.uid(param.getDtuicUserId());
        MetaObjectHolder.tenantId(param.getDtuicTenantId());

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        if (ArrayUtils.isNotEmpty(parameters)) {
            // 只有post请求，并且controller方法中第一个参数加了RequestBody注解才进行参数处理
            // 备注：文件上传相关的请求参数处理会放在FileUploadAspect中单独处理
            RequestBody body = parameters[0].getAnnotation(RequestBody.class);
            if (Objects.nonNull(body)) {
                if (null == args[0]) {
                    args[0] = parameters[0].getType().newInstance();
                }
                // 做这步的原因是通用参数都从cookie和session中取，但是部分接口会传回tenantId和projectId
                PublicUtil.copyPropertiesIgnoreNull(args[0], param);
                PublicUtil.copyPropertiesIgnoreNull(param, args[0]);
                return point.proceed(args);
            }
        }
        return point.proceed();
    }


}
