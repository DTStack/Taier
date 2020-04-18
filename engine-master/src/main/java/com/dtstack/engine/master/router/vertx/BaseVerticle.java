package com.dtstack.engine.master.router.vertx;

import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.master.utils.AopTargetUtils;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
		String rbody = routingContext.getBodyAsString("UTF-8");
		LOGGER.info("receive http request:{}:{}",path,rbody);
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
		for(int i=0; i<parameters.length; i++){
			Parameter pa = parameters[i];
			Class<?> paramterType = parameterTypes[i];
			Param param = pa.getAnnotation(Param.class);
			Object obj = null;
			if(param != null){
				obj = params.get(param.value());
				if(obj!=null&&!obj.getClass().equals(paramterType)){
					obj = com.dtstack.engine.common.util.PublicUtil.classConvter(paramterType, obj);
				}
			}else if(Map.class.equals(paramterType)){
				obj = params;
			}else{
				obj = PublicUtil.mapToObject(params, paramterType);
			}
			args[i] = obj;
		}
		LOGGER.info("-->mapToParamObjects  end");
		return args;
	}

}
