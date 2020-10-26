package com.dtstack.engine.sql.formate;

/**
 * copy from hibernate
 * Date: 2018/7/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class StringHelper {

    public static final String WHITESPACE = " \n\r\f\t";

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

}
