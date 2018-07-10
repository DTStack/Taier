package com.dtstack.rdos.common.util;

/**
 * Reason:
 * Date: 2018/6/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class DtStringUtil {

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     * @param str
     * @param delimter
     * @return
     */
    public static String[] splitIgnoreQuota(String str, String delimter){
        String splitPatternStr = delimter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";
        return str.split(splitPatternStr);
    }

    /***
     * 根据指定分隔符分割字符串---忽略在引号 和 括号 里面的分隔符
     * @param str
     * @param delimter
     * @return
     */
    public static String[] splitIgnoreQuotaBrackets(String str, String delimter){
        String splitPatternStr = delimter + "(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)";
        return str.split(splitPatternStr);
    }
}
