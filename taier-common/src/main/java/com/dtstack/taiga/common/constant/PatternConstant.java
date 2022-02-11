package com.dtstack.taiga.common.constant;

/**
 * 
 * @author sishu.yss
 *
 */
public class PatternConstant {

	public final static String PHONEPATTERN = "^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\d{8}$";
	
	public final static String EMAILPATTERN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	
	public final static String USERNAMEPATTERN = "[a-z0-9A-Z_-.]{3,}";

	public final static String PASSWORDPATTERN = "[a-z0-9A-Z_-.]{8,}";
	
	public final static String PROJECTPATTERN = "[a-z0-9A-Z_-]+";

	public final static String TASKPATTERN = "[a-z0-9A-Z_-]+";
	
	public final static String FUNCTIONPATTERN = "[a-z0-9A-Z_]+";

    /** 字符串查找是否存在密码字段的正则表达式 **/
    public final static String PASSWORD_FIELD_REGEX = "\"(pass(word)?|accesskey)\"\\s*:\\s*\"\\*{6}\"";
}
