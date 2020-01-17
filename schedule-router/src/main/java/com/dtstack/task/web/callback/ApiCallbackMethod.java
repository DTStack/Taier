package com.dtstack.task.web.callback;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;
import com.dtstack.dtcenter.common.hadoop.HdfsOperator;
import com.dtstack.task.common.exception.ErrorCode;
import com.dtstack.task.web.util.ResponseUtil;
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
				DtCenterDefException rdosDefineException = null;
				if (e.getCause() instanceof DtCenterDefException) {
					rdosDefineException = ((DtCenterDefException)e.getCause());
					if(rdosDefineException.getErrorCode()!=null){
						errorCode = rdosDefineException.getErrorCode();
					}
					errorMsg = rdosDefineException.getErrorMsg();
					if (e.getCause().getCause() != null) {
					    logger.error("{}", e.getCause().getCause());
                    }
				} else if (e instanceof DtCenterDefException) {
					rdosDefineException = ((DtCenterDefException)e);
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
			HdfsOperator.release();//释放当前threadLocal
		}
	}
}
