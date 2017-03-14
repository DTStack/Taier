package com.dtstack.rdos.engine.entrance.http.callback;

import java.io.OutputStream;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年12月30日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ApiCallbackMethod {
	private final static Logger logger = LoggerFactory
			.getLogger(ApiCallbackMethod.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	public static void doCallback(ApiCallback ac,HttpExchange he) {
		 OutputStream os = null;
		 ApiResult apiResult = new ApiResult();
		try {
			os = he.getResponseBody();
			long start = System.currentTimeMillis();
			apiResult.setData(ac.execute());
			apiResult.setCode(200);
			long end = System.currentTimeMillis();
			apiResult.setSpace(end - start);
		} catch (Throwable e) {
			apiResult.serverError();
			logger.error("", e);
		}finally{
			if(os!=null)
				try {
					byte[] result =objectMapper.writeValueAsBytes(apiResult);
					he.sendResponseHeaders(200, result.length);
					os.write(result);
					os.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(ExceptionUtil.getErrorMessage(e));
				}
		}
	}
}
