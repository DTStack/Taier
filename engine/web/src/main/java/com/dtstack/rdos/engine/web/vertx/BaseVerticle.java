package com.dtstack.rdos.engine.web.vertx;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import com.dtstack.rdos.common.annotation.Forbidden;
import com.dtstack.rdos.common.annotation.Param;
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
	
	private static String classNameTemplate = "com.dtstack.rdos.engine.entrance.service.%sServiceImpl";
	
	private static Map<String,Object> objects = Maps.newConcurrentMap();
	
	public void request(final RoutingContext routingContext){
		
		final BaseVerticle allRequestVerticle  = this;
		
		ApiCallbackMethod.doCallback(new ApiCallback(){

			@Override
			public Object execute() throws Exception {
				// TODO Auto-generated method stub
				return allRequestVerticle.reflectionMethod(routingContext);
			}
			
		}, routingContext);
	}
	
	
	protected Object reflectionMethod(RoutingContext routingContext) throws Exception{
		HttpServerRequest httpServerRequest = routingContext.request();
		Map<String,Object> params = routingContext.getBodyAsJson().getMap();
		String path = httpServerRequest.path();
		String[] paths = path.split("/");
		if(paths.length < 2){
			throw new Exception("url path error,please check");
		}
		String name = paths[paths.length-2];
		String method = paths[paths.length-1];
		Class<?> cla = null;
		Object obj = objects.get(name);
		if(obj == null){
			synchronized(BaseVerticle.class){
				if(obj == null){
					cla = Class.forName(String.format(classNameTemplate,name));
					obj = cla.newInstance();
					objects.put(name,obj);
				}
			}
		}else{
			cla = obj.getClass();
		}
		if(cla.getAnnotation(Forbidden.class) != null){
			throw new Exception("this service is forbidden");
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
			throw new Exception("this method is not exist");
		}
		if(mm.getAnnotation(Forbidden.class) != null){
			throw new Exception("this method is forbidden");
		}
		return mm.invoke(obj, mapToParamObjects(params,mm.getParameters(),mm.getParameterTypes()));
	} 
	
	private Object[] mapToParamObjects(Map<String, Object> params,
			Parameter[] parameters, Class<?>[] parameterTypes) throws JsonParseException, JsonMappingException, JsonGenerationException, IOException {
		// TODO Auto-generated method st
		if(parameters==null||parameters.length==0)return new Object[]{};
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
}
