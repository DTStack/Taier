package com.dtstack.engine.router.callback;


import com.dtstack.engine.common.exception.ErrorCode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年12月30日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ApiResult<T> {

    private static final Logger logger = LoggerFactory.getLogger(ApiResult.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * FIXME: 具体信息见对应的code定义 1:成功，-1：失败，0：需要登录
     */
    public int code;

    public String message;

    private T data;

    private long space;

    public ApiResult() {
    }

    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResult createErrorResult(String errMsg, int code) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(code);
        apiResult.setMessage(errMsg);
        return apiResult;
    }


    public static String createErrorResultJsonStr(int code, String message) {
        ApiResult apiResult = createErrorResult(message, code);
        String result;
        try {
            result = objectMapper.writeValueAsString(apiResult);
        } catch (Exception e) {
            logger.error("", e);
            result = "code:" + code + ",message:" + message;
        }
        return result;
    }

    public static String createSuccessResultJsonStr(String msg) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ErrorCode.SUCCESS.getCode());
        apiResult.setMessage(msg);
        String result;
        try {
            result = objectMapper.writeValueAsString(apiResult);
        } catch (Exception e) {
            logger.error("", e);
            result = "code:" + ErrorCode.SUCCESS.getCode();
        }

        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    @SuppressWarnings("rawtypes")
    public static String getApiResult(int code){
        try {
            ApiResult apiResult = new ApiResult();
            apiResult.setCode(code);
            return objectMapper.writeValueAsString(apiResult);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return String.format("{\"code\":%d}", code);
    }
}
