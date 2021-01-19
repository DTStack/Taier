package com.dtstack.engine.alert.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;


public class HttpKit {


    /**
     * <p>
     *     post请求
     * </p>
     * @param url
     * @param param
     * @return
     */
    public static String post(String url, String param) throws IOException{

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.addHeader("content-type", "application/json;charset=utf-8");
        request.setEntity(new StringEntity(param, "utf-8"));
        HttpResponse response = httpClient.execute(request);
        return EntityUtils.toString(response.getEntity(), "utf-8");

    }
    
	public static String send(String url, Map<String, String> header, Object body, boolean isGet, boolean isCookieStore)
			throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpRequestBase request = isGet ? new HttpGet(url) : new HttpPost(url);
		request.addHeader("content-type", "application/json;charset=utf-8");
		if (header != null && !header.isEmpty()) {
			for(Entry<String, String> entry : header.entrySet()){
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
		if (body != null) {
			if (request instanceof HttpPost) {
				HttpPost req = (HttpPost) request;
				req.setEntity(new StringEntity(JSONObject.toJSONString(body), "utf-8"));
				request = req;
			}
		}
		HttpResponse response = null;
		if (isCookieStore) {
			HttpClientContext context = HttpClientContext.create();
			CookieStore cookieStore = new BasicCookieStore();
			context.setCookieStore(cookieStore);
			response = httpClient.execute(request, context);
		} else {
			response = httpClient.execute(request);
		}
		return EntityUtils.toString(response.getEntity(), "utf-8");
	}

}
