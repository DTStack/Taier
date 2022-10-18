package com.dtstack.taier.common.constant;


public interface PatternConstant {

	String FUNCTION_PATTERN = "[a-z0-9_]+";

	/**
	 * 正则: 租户名称正则表达式,字母、数字、下划线组成，且长度不超过64个字符
	 * Regular: Tenant name regular expression, consisting of letters, numbers, and underscores, and the length does not exceed 64 characters
	 */
	String TENANT_NAME_REGEX = "^[a-zA-Z0-9_]{1,64}$";

}
