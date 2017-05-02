package com.dtstack.rdos.engine.web.callback;


import io.vertx.ext.web.RoutingContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.engine.web.enums.Code;
import com.dtstack.rdos.engine.web.util.ResponseUtil;

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
	
	public static void doCallback(ApiCallback ac,RoutingContext context) {
		 ApiResult apiResult = new ApiResult();
		try {
			long start = System.currentTimeMillis();
			apiResult.setData(ac.execute());
			apiResult.setCode(Code.NORMAL.getType());
			long end = System.currentTimeMillis();
			apiResult.setSpace(end - start);
			ResponseUtil.res200(context, objectMapper.writeValueAsString(apiResult));
		} catch (Throwable e) {
			apiResult.setCode(Code.FAIL.getType());
			apiResult.setErrorMsg(e.getCause().toString());
			logger.error("ApiCallbackMethod error:{}",e.getCause().toString());
			try {
				ResponseUtil.res500(context, objectMapper.writeValueAsString(apiResult));
			} catch (Throwable e1) {
				// TODO Auto-generated catch block
				logger.error("ApiCallbackMethod error:",e1);
		}
	}
	}
}
