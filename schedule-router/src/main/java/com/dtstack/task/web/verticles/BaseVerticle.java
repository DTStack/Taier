package com.dtstack.task.web.verticles;

import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.util.AopTargetUtils;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.task.common.exception.ErrorCode;
import com.dtstack.task.common.exception.RdosDefineException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @author sishu.yss
 */
public class BaseVerticle {
    private static Logger LOGGER = LoggerFactory.getLogger(BaseVerticle.class);

    protected ApplicationContext context;

    private static final String BEAN_TEMPLATE = "%sService";
    private static final String USER_ID = "userId";
    private static final String PROJECT_ID = "projectId";
    private static final String TENANT_ID = "tenantId";
    private static final String AUTH_SERVICE = "authService";
    private static final String IS_ROOT = "isRoot";

    protected Object reflectionMethod(RoutingContext routingContext, Map<String, Object> params) throws Exception {
        HttpServerRequest httpServerRequest = routingContext.request();
        String path = httpServerRequest.path();
        String[] paths = path.split("/");
        if (paths.length < 2) {
            throw new Exception("url path error,please check");
        }
        String name = paths[paths.length - 2];
        String method = paths[paths.length - 1];
        Object obj = context.getBean(String.format(BEAN_TEMPLATE, name));
        Object targetObj = AopTargetUtils.getTarget(obj);

        if (targetObj == null) {
            throw new RdosDefineException(ErrorCode.SERVICE_NOT_EXIST);
        }
        if (targetObj.getClass().getAnnotation(Forbidden.class) != null) {
            throw new RdosDefineException(ErrorCode.SERVICE_FORBIDDEN);
        }
        Method targetMethod = getMethod(targetObj.getClass(), method);
        if (targetMethod == null) {
            throw new RdosDefineException(ErrorCode.METHOD_NOT_EXIST);
        }
        if (targetMethod.getAnnotation(Forbidden.class) != null) {
            throw new RdosDefineException(ErrorCode.METHOD_FORBIDDEN);
        }
        Method proxyMethod = getMethod(obj.getClass(), method);
        Object result =  proxyMethod.invoke(obj, mapToParamObjects(params, targetMethod.getParameters(), targetMethod.getParameterTypes()));

        return result;
    }

    private Method getMethod(Class<?> clazz, String method) {
        Method[] methods = clazz.getMethods();
        Method mm = null;
        for (Method med : methods) {
            if (med.getName().equals(method)) {
                mm = med;
                break;
            }
        }
        return mm;
    }

    private Object[] mapToParamObjects(Map<String, Object> params,
                                       Parameter[] parameters, Class<?>[] parameterTypes) throws Exception {
        LOGGER.info("--->mapToParamObjects   :params{},parameters {},parameterTypes{}", params, parameters, parameterTypes);
        if (parameters == null || parameters.length == 0) {
            return new Object[]{};
        }
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameterTypes[i];
            Param param = parameter.getAnnotation(Param.class);
            Object obj = null;
            if (param != null) {
                obj = params.get(param.value());
                if (obj != null) {
                    if (Collection.class == parameterType || List.class.isAssignableFrom(parameterType)) {
                        //只适合基本数据类型的
                        ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                        Type t = parameterizedType.getActualTypeArguments()[0];
                        Class clazz = Class.forName(t.getTypeName());
                        if (clazz != null) {
                            List list = (List) obj;
                            List converterArgs = new ArrayList<>(list.size());
                            for (Object arg : list) {
                                arg = PublicUtil.ClassConvter(clazz, arg);
                                converterArgs.add(arg);
                            }
                            obj = converterArgs;
                        }
                    } else {
                        if (!obj.getClass().equals(parameterType)) {
                            obj = PublicUtil.ClassConvter(parameterType, obj);
                        }
                    }
                }
            } else if (Map.class.equals(parameterType)) {
                obj = params;
            } else {
                obj = PublicUtil.objectToObject(params, parameterType);
            }
            args[i] = obj;
        }
        LOGGER.info("-->mapToParamObjects  end");
        return args;
    }

}
