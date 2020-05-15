package com.dtstack.engine.master.router.callback;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.util.ResponseUtil;
import com.dtstack.engine.master.utils.HdfsOperator;
import io.vertx.ext.web.RoutingContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Company www.dtstack.com
 * @author sishu.yss
 *
 */
public class ApiCallbackMethod {

	private final static Logger logger = LoggerFactory
			.getLogger(ApiCallbackMethod.class);

	private static ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void doCallback(ApiCallback ac, RoutingContext context) {
		ApiResult apiResult;
		String uri = context.request().uri();
		try {
			long start = System.currentTimeMillis();
			apiResult = new ApiResult();
			apiResult.setData(ac.execute());
			apiResult.setCode(ErrorCode.SUCCESS.getCode());
			long end = System.currentTimeMillis();
			apiResult.setSpace(end - start);
			ResponseUtil.res200(context, objectMapper.writeValueAsString(apiResult));
		} catch (Throwable e) {
			try {
				ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
				String errorMsg = null;
				RdosDefineException rdosDefineException = null;
				if (e.getCause() instanceof RdosDefineException) {
					rdosDefineException = (RdosDefineException)e.getCause();
					if(rdosDefineException.getErrorCode()!=null){
						errorCode = rdosDefineException.getErrorCode();
					}
					errorMsg = rdosDefineException.getErrorMsg();
					if (e.getCause().getCause() != null) {
					    logger.error("{}", e.getCause().getCause());
                    }
				} else if (e instanceof RdosDefineException) {
					rdosDefineException = (RdosDefineException)e;
					if(rdosDefineException.getErrorCode()!=null){
						errorCode = rdosDefineException.getErrorCode();
					}
					errorMsg = rdosDefineException.getErrorMsg();
					if (e.getCause() != null) {
					    logger.error("{}", e.getCause());
                    }
				}else{
					errorCode = ErrorCode.SERVER_EXCEPTION;
					errorMsg = ErrorCode.SERVER_EXCEPTION.getDescription();
					logger.error("uri:{}, params:{} ApiCallbackMethod error:", uri, context.get("params"), e);
				}

				if (errorCode.equals(ErrorCode.PERMISSION_LIMIT)) {
					ResponseUtil.res200(context, ApiResult.createErrorResultJsonStr(errorCode.getCode(), errorMsg));
					return;
				}

				ResponseUtil.res200(context, ApiResult.createErrorResultJsonStr(errorCode.getCode(), errorMsg));
			} catch (Throwable e1) {
				logger.error("uri:{}, params:{} ApiCallbackMethod error:", uri, context.get("params"), e1);
			}
		}finally{
//			HdfsOperator.release();//释放当前threadLocal
		}
	}
}
