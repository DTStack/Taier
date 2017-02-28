package com.dtstack.rdos.engine.entrance.http.callback;

import java.util.UUID;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年12月30日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ApiResult {

	private int code;
	private Object data;
	private String errorMsg;
	private long space;
	private String requestId = UUID.randomUUID().toString();

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
}
