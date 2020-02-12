package com.dtstack.engine.common.http;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

/**
 * 
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosHttpRequestRetryHandler implements HttpRequestRetryHandler{

	@Override
	public boolean retryRequest(IOException exception, int executionCount,
			HttpContext context) {

		 if (executionCount >= 3) {// 如果已经重试了3次，就放弃
             return false;
         }

         HttpClientContext clientContext = HttpClientContext.adapt(context);
         HttpRequest request = clientContext.getRequest();
         // 如果请求是幂等的，就再次尝试
         if (!(request instanceof HttpEntityEnclosingRequest)) {
             return true;
         }
         return false;
	}

}
