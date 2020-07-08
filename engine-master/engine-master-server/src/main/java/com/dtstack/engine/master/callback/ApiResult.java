package com.dtstack.engine.master.callback;


import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author toutian
 */
public class ApiResult {

    private static final Logger logger = LoggerFactory.getLogger(ApiResult.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getApiResultString(ExecFunction function, String uri, Object... params) {
        try {
            long start = System.currentTimeMillis();
            ApiResult apiResult = new ApiResult();
            apiResult.setData(function.execute());
            apiResult.setCode(ErrorCode.SUCCESS.getCode());
            long end = System.currentTimeMillis();
            apiResult.setSpace(end - start);
            return objectMapper.writeValueAsString(apiResult);
        } catch (Throwable e) {
            return getApiResultStringWithThrowable(e, uri, params);
        }
    }

    private static String getApiResultStringWithThrowable(Throwable e, String uri, Object... params) {
        try {
            ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
            String errorMsg;
            RdosDefineException rdosDefineException;
            if (e.getCause() instanceof RdosDefineException) {
                rdosDefineException = (RdosDefineException)e.getCause();
                if(rdosDefineException.getErrorCode()!=null){
                    errorCode = rdosDefineException.getErrorCode();
                }
                errorMsg = rdosDefineException.getErrorMsg();
                if (e.getCause().getCause() != null) {
                    logger.error(e.getCause().getCause().toString());
                }
            } else if (e instanceof RdosDefineException) {
                rdosDefineException = (RdosDefineException)e;
                if(rdosDefineException.getErrorCode()!=null){
                    errorCode = rdosDefineException.getErrorCode();
                }
                errorMsg = rdosDefineException.getErrorMsg();
                if (e.getCause() != null) {
                    logger.error(e.getCause().toString());
                }
            }else{
                errorCode = ErrorCode.SERVER_EXCEPTION;
                errorMsg = ErrorCode.SERVER_EXCEPTION.getDescription();
                logger.error("uri:{}, params:{} ApiCallbackMethod error:", uri, params, e);
            }
            return createErrorResultJsonStr(errorCode.getCode(), errorMsg);
        } catch (Throwable e1) {
            logger.error("uri:{}, params:{} ApiCallbackMethod error:", uri, params, e1);
            return null;
        }
    }

    private static String createErrorResultJsonStr(int code, String message) {
        com.dtstack.engine.master.router.callback.ApiResult apiResult = createErrorResult(message, code);
        String result;
        try {
            result = objectMapper.writeValueAsString(apiResult);
        } catch (Exception e) {
            logger.error("", e);
            result = "code:" + code + ",message:" + message;
        }
        return result;
    }

    private static com.dtstack.engine.master.router.callback.ApiResult createErrorResult(String errMsg, int code) {
        com.dtstack.engine.master.router.callback.ApiResult apiResult = new com.dtstack.engine.master.router.callback.ApiResult();
        apiResult.setCode(code);
        apiResult.setMessage(errMsg);
        return apiResult;
    }

    /**
     * FIXME: 具体信息见对应的code定义 1:成功，-1：失败，0：需要登录
     */
    private int code;

    private String message;

    private Object data;

    private long space;

    private ApiResult() {
    }

    public int getCode() {
        return code;
    }

    private void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    private void setData(Object data) {
        this.data = data;
    }

    public long getSpace() {
        return space;
    }

    private void setSpace(long space) {
        this.space = space;
    }

}
