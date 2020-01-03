package com.dtstack.engine.common.util;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/1/3
 */
public class GenerateErrorMsgUtil {

    public static String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }

}
