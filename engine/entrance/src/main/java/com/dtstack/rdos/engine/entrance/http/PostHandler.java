package com.dtstack.rdos.engine.entrance.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年2月17日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public abstract class PostHandler implements HttpHandler{

	private static String encoding = "utf-8";

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handle(HttpExchange he) throws IOException {
		// TODO Auto-generated method stub
	}

	protected String getQueryString(HttpExchange exchange) throws IOException{
		String qry;
		InputStream in = exchange.getRequestBody();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte buf[] = new byte[4096];
			for (int n = in.read(buf); n > 0; n = in.read(buf)) {
				out.write(buf, 0, n);
			}
			qry = new String(out.toByteArray(), encoding);
		}finally {
			in.close();
		}
		return qry;
	}


	@SuppressWarnings("unchecked")
	protected Map<String,Object> parseQuery(String query) throws IOException {
		         Map<String,Object> parameters = Maps.newConcurrentMap();
		         if (StringUtils.isNotBlank(query)) {
					 parameters.putAll(objectMapper.readValue(query.getBytes(),Map.class));
		         }
		         return parameters;
		}
}
