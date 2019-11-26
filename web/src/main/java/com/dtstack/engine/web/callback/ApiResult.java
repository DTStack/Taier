package com.dtstack.engine.web.callback;

import java.util.UUID;

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
public class ApiResult {

    private static final Logger LOG = LoggerFactory.getLogger(ApiResult.class);

	private int code;
	private Object data;
	private String errorMsg;
	private long space;
	private String requestId = UUID.randomUUID().toString();
    private static final ObjectMapper objectMapper = new ObjectMapper();

	public ApiResult(){
//		setRequestId(RequestContext.get().getRequestId());
	}
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}


	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public long getSpace() {
		return space;
	}


	public void setSpace(long space) {
		this.space = space;
	}


	public String getErrorMsg() {
		return errorMsg;
	}


	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}


	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}



	public void serverError() {
		this.setCodeMsg(500, "Internal Server Error");
	}

	public void setCodeMsg(int code, String msg) {
		this.setCode(code);
		this.setErrorMsg(msg);
	}
	
	public void success(Object data){
		this.setCodeMsg(200, "OK");
		this.setData(data);
	}

	public static String createErrorResultJsonStr(int code, String message) {
		ApiResult apiResult = createErrorResult(message, code);
		String result;
		try {
			result = objectMapper.writeValueAsString(apiResult);
		} catch (Exception e) {
            LOG.error("", e);
			result = "code:" + code + ",message:" + message;
		}
		return result;
	}

	public static ApiResult createErrorResult(String errMsg, int code) {
		ApiResult apiResult = new ApiResult();
		apiResult.setCode(code);
		apiResult.setErrorMsg(errMsg);
		return apiResult;
	}
	

	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}


	public void noContent(){
		this.setCodeMsg(204, "No Content");
	}
	
	public void notModified(){
		this.setCodeMsg(304, "Not Modified");
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
