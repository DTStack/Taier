package com.dtstack.taier.common.constant;


public interface PatternConstant {

	String FUNCTION_PATTERN = "[a-z0-9A-Z_]+";

    /** 字符串查找是否存在密码字段的正则表达式 **/
	String PASSWORD_FIELD_REGEX = "\"(pass(word)?|accesskey)\"\\s*:\\s*\"\\*{6}\"";
}
