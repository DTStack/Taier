package com.dtstack.schedule.common.metric;

import java.io.UnsupportedEncodingException;

/**
 * Reason:
 * Date: 2018/10/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IFunction {

    boolean checkParam();

    String build(String content) throws UnsupportedEncodingException;
}
