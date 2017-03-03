package com.dtstack.rdos.engine.entrance.http.handler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.dtstack.rdos.engine.entrance.http.callback.ApiCallbackMethod;
import com.dtstack.rdos.engine.entrance.http.callback.ApiCallback;
import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class NodeHandler extends PostHandler{
	
	private static String classNameTemplate = "com.dtstack.rdos.engine.entrance.service.impl.%sServiceImpl";
	
	private static Map<String,Object> objects = Maps.newConcurrentMap();
	
	public NodeHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(final HttpExchange he) throws IOException {
		// TODO Auto-generated method stub
		ApiCallbackMethod.doCallback(new ApiCallback(){
			@Override
			public Object execute() throws Exception {
				return reflectionMethod(he);
			}
		}, he);
	}
	
	private Object reflectionMethod(HttpExchange he) throws Exception{
		String path = he.getRequestURI().getPath();
		String[] paths = path.split("/");
		if(paths.length < 2){
			throw new Exception("url path error");
		}
		String name = paths[paths.length-2];
		String method = paths[paths.length-1];
		String paramsStr = getQueryString(he);
		Class<?> paramType = null;
		Map<String,Object> paramsMap = null;
		if(StringUtils.isNotBlank(paramsStr)){
			paramType = Map.class;
			paramsMap  = parseQuery(paramsStr);
		}
		Class<?> cla = Class.forName(String.format(classNameTemplate,name));
		Object obj = objects.get(name);
		if(obj == null){
			synchronized(NodeHandler.class){
				if(obj == null){
					obj = cla.newInstance();
				}
			}
		}
		Method rmethod = cla.getMethod(method, paramType);
		return rmethod.invoke(obj, paramsMap);
	}
}
