package com.dtstack.rdos.commom.exception;

/**
 * 错误码定义
 * FIXME 各个模块的错误码用分割线分开
 * company: www.dtstack.com
 * author: xuchao
 * create: 2017/12/1
 */
public enum ErrorCode {

    /***----------------start 公共模块错误码 0 ~ 100 start----------------------------------------****/

    NOT_LOGIN(0, "未登录"),
    SUCCESS(1, "执行成功"),
    PERMISSION_LIMIT(3, "没有权限"),

    UNKNOWN_ERROR(10, "未定义异常"),
    SERVER_EXCEPTION(11, "服务内部错误"),

    UNSUPPORTED_OPERATION(12, "不支持的操作"),
    INVALID_PARAMETERS(13, "参数不正确"),
    NAME_ALREADY_EXIST(14, "名称已经存在"),
    NAME_FORMAT_ERROR(15, "名称格式错误"),
    NAME_ILLEGAL(16, "名称非法"),
    NO_MASTER_NODE(17, "master节点未生成"),

    SERVICE_NOT_EXIST(47, "该服务不存在"),
    SERVICE_FORBIDDEN(48, "该服务禁止请求"),
    METHOD_NOT_EXIST(49, "方法不存在"),
    METHOD_FORBIDDEN(50, "该方法禁止被调用"),
    CALL_UNLAWFUL(51, "请求非法"),

    HTTP_CALL_ERROR(60, "http 调用失败"),


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
