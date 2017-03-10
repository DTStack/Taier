package com.dtstack.rdos.common.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosHttpRequestRetryHandler implements HttpRequestRetryHandler{

	@Override
	public boolean retryRequest(IOException exception, int executionCount,
			HttpContext context) {
		// TODO Auto-generated method stub
		 if (executionCount >= 3) {// 如果已经重试了3次，就放弃
             return false;
         }
         if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
             return true;
         }
         if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
             return false;
         }
         if (exception instanceof InterruptedIOException) {// 超时
             return false;
         }
         if (exception instanceof UnknownHostException) {// 目标服务器不可达
             return false;
         }
         if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
             return false;
         }
         if (exception instanceof SSLException) {// ssl握手异常
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
