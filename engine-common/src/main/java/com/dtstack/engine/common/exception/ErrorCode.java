package com.dtstack.engine.common.exception;

/**
 * 错误码定义
 * FIXME 各个模块的错误码用分割线分开
 * company: www.dtstack.com
 * author: xuchao
 * create: 2017/12/1
 */
public enum ErrorCode {

    /***----------------start 公共模块错误码 0 ~ 100 start----------------------------------------****/

    NOT_LOGIN(0, "not login"),
    SUCCESS(1, "success"),
    PERMISSION_LIMIT(3, "permission limit"),

    UNKNOWN_ERROR(10, "unknown error"),
    SERVER_EXCEPTION(11, "server exception"),

    UNSUPPORTED_OPERATION(12, "unsupported operation"),
    INVALID_PARAMETERS(13, "invalid parameters"),
    NAME_ALREADY_EXIST(14, "name alread exist"),
    NAME_FORMAT_ERROR(15, "name format error"),
    NAME_ILLEGAL(16, "name illegal"),
    NO_MASTER_NODE(17, "no master node"),
    INVALID_TASK_STATUS(18, "invalid task status"),
    INVALID_TASK_RUN_MODE(19, "invalid task run mod"),
    JOB_CACHE_NOT_EXIST(20, "job cache not exist this job"),

    SERVICE_NOT_EXIST(47, "service not exist"),
    SERVICE_FORBIDDEN(48, "service forbidden"),
    METHOD_NOT_EXIST(49, "method not exist"),
    METHOD_FORBIDDEN(50, "method forbidden"),
    CALL_UNLAWFUL(51, "call unlawful"),

    HTTP_CALL_ERROR(60, "http call error"),


    /***----------------end 公共模块错误码 end ----------------------------------------****/



    NOT_USED(10000000, "");

	private int code;

	private String description;

	ErrorCode(int code, String description){
        this.code = code;
        this.description = description;
    }

	// 错误码编号
	public int getCode(){
		return code;
	}

	public String getDescription(){
		return description;
	}

	@Override
	public String toString() {

		return String.format("{\"code\":%d, \"description\":\"%s\"}", this.code,
				this.description);
	}
}
