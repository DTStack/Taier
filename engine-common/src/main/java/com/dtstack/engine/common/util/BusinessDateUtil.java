package com.dtstack.engine.common.util;

import com.dtstack.dtcenter.common.util.DateUtil;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class BusinessDateUtil {

    public static String getOnlyDate(String date){
        String str = DateUtil.addTimeSplit(date);
        if (str.length() != 19){
            return str;
        }
        return str.substring(0,11);
    }
}
