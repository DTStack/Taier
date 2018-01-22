package com.dtstack.rdos.engine.web.vertx;

import com.dtstack.rdos.commom.exception.RdosException;
import com.google.common.base.Strings;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.common.annotation.Forbidden;
import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.common.util.MD5Util;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.web.callback.ApiCallback;
import com.dtstack.rdos.engine.web.callback.ApiCallbackMethod;
import com.google.common.collect.Maps;


/**
 * 
 * @author sishu.yss
 *
 */
public class BaseVerticle {
	
	private static Logger logger = LoggerFactory.getLogger(BaseVerticle.class);
	
	private static String classNameTemplate = "com.dtstack.rdos.engine.entrance.service.%sServiceImpl";
	
	private static Map<String,Object> objects = Maps.newConcurrentMap();
	
	private final static String CODE = "UTF-8";
	
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
		check(routingContext);
		Map<String,Object> params = routingContext.getBodyAsJson().getMap();
		String path = httpServerRequest.path();
		logger.warn("receive http request:{}:{}",path,routingContext.getBodyAsString());
		String[] paths = path.split("/");
		if(paths.length < 2){
			throw new RdosException("url path error,please check");
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
			throw new RdosException("this service is forbidden");
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
			throw new RdosException("this method is not exist");
		}
		if(mm.getAnnotation(Forbidden.class) != null){
			throw new RdosException("this method is forbidden");
		}
		return mm.invoke(obj, mapToParamObjects(params,mm.getParameters(),mm.getParameterTypes()));
	} 
	
	public void check(RoutingContext routingContext){
		String body = routingContext.getBodyAsString(CODE);
		String md5  = routingContext.request().getHeader("md5");
		String ctime = routingContext.request().getHeader("ctime");
		String md5other = MD5Util.getMD5String(String.format("%s:%s:%s", ctime,body,ctime));
		if(!md5other.equals(md5)){
			throw new RdosException("This call is unlawful");
		}
	}
	
	private Object[] mapToParamObjects(Map<String, Object> params,
			Parameter[] parameters, Class<?>[] parameterTypes) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException {
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
					obj = PublicUtil.ClassConvter(paramterType, obj);
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
            throw new RdosException("can't upper word of empty | null");
        }

        if(word.length() == 1){
            return word.toUpperCase();
        }

        String first = word.substring(0, 1).toUpperCase();
        String newWord = first + word.substring(1);
        return newWord;
	}
}
