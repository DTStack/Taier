package com.dtstack.engine.web.vertx;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.web.callback.ApiCallback;
import com.dtstack.engine.web.callback.ApiCallbackMethod;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;


/**
 * 
 * @author sishu.yss
 *
 */
public class BaseVerticle {
	
	private static Logger logger = LoggerFactory.getLogger(BaseVerticle.class);

	private static String classNameTemplate = "com.dtstack.engine.service.%sServiceImpl";

	private static Map<String,Object> objects = Maps.newConcurrentMap();
	
	private final static String CODE = "UTF-8";

	private static ObjectMapper objectMapper = new ObjectMapper();

	public void request(final RoutingContext routingContext){
		
		final BaseVerticle allRequestVerticle  = this;
		
		ApiCallbackMethod.doCallback(new ApiCallback(){

			@Override
			public Object execute() throws Exception {
				return allRequestVerticle.reflectionMethod(routingContext);
			}

		}, routingContext);
	}
	
	
	protected Object reflectionMethod(RoutingContext routingContext) throws Exception{
		HttpServerRequest httpServerRequest = routingContext.request();
		//调用合法性验证
//		check(routingContext);
		String rbody = routingContext.getBodyAsString(CODE);
		Map<String,Object> params = objectMapper.readValue(rbody,Map.class);
		String path = httpServerRequest.path();
		logger.info("receive http request:{}:{}",path,rbody);
		String[] paths = path.split("/");
		if(paths.length < 2){
			throw new RdosException("request address error", ErrorCode.SERVICE_NOT_EXIST);
		}

		String name = paths[paths.length-2];
		String method = paths[paths.length-1];
		Class<?> cla = null;
		Object obj = objects.get(name);
		if(obj == null){
			synchronized(BaseVerticle.class){
				if(obj == null){
				    String className = upperFirstLetter(name);
					cla = Class.forName(String.format(classNameTemplate, className));
					obj = cla.newInstance();
					objects.put(name,obj);
				}
			}
		}else{
			cla = obj.getClass();
		}
		if(cla.getAnnotation(Forbidden.class) != null){
			throw new RdosException(ErrorCode.SERVICE_FORBIDDEN);
		}
		
		Method[] methods = cla.getMethods();
		Method mm = null;
		for(Method med:methods){
			if(med.getName().equals(method)){
				mm = med;
				break;
			}
		}
		if(mm == null){
			throw new RdosException(ErrorCode.METHOD_NOT_EXIST);
		}
		if(mm.getAnnotation(Forbidden.class) != null){
			throw new RdosException(ErrorCode.METHOD_FORBIDDEN);
		}
		return mm.invoke(obj, mapToParamObjects(params,mm.getParameters(),mm.getParameterTypes()));
	} 
	
	public void check(RoutingContext routingContext){
		String body = routingContext.getBodyAsString(CODE);
		String md5  = routingContext.request().getHeader("md5");
		String ctime = routingContext.request().getHeader("ctime");
		String md5other = MD5Util.getMd5String(String.format("%s:%s:%s", ctime,body,ctime));
		if(!ConfigParse.isDebug() && !md5other.equals(md5)){
			throw new RdosException(ErrorCode.CALL_UNLAWFUL);
		}
	}
	
	private Object[] mapToParamObjects(Map<String, Object> params,
			Parameter[] parameters, Class<?>[] parameterTypes) throws IOException {
		if(parameters==null||parameters.length==0){return new Object[]{};}
		int length = parameters.length;
		Object[] objs  = new Object[length];
		for(int i=0; i<parameters.length; i++){
			Parameter pa = parameters[i];
			Class<?> paramterType = parameterTypes[i];
			Param param = pa.getAnnotation(Param.class);
			Object obj = null;
			if(param != null){
				obj = params.get(param.value());
				if(obj!=null&&!obj.getClass().equals(paramterType)){
					obj = PublicUtil.classConvter(paramterType, obj);
				}
			}else if(Map.class.equals(paramterType)){
				obj = params;
			}else{
				obj = PublicUtil.mapToObject(params, paramterType);
			}
			objs[i] = obj;
		}
		return objs;
	}

	public String upperFirstLetter(String word){
        if(Strings.isNullOrEmpty(word)){
            throw new RdosException("can't upper word of empty | null", ErrorCode.INVALID_PARAMETERS);
        }

        if(word.length() == 1){
            return word.toUpperCase();
        }

        String first = word.substring(0, 1).toUpperCase();
        String newWord = first + word.substring(1);
        return newWord;
	}
}
