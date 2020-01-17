package com.dtstack.engine.master.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取输入的cronStr的各个field的值
 * 格式 * * * * * ?
 * Date: 2017/5/29
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class CronStrUtil {

    private static final Logger logger = LoggerFactory.getLogger(CronStrUtil.class);

    public static String getDayStr(String str){
        String[] arr = str.split("\\s+");
        if(arr.length < 6){
            logger.error("it is an illegal cron string");
            return null;
        }

        return arr[3];
    }

    public static String getDayOfWeekStr(String str){
        String[] arr = str.split("\\s+");
        if(arr.length < 6){
            logger.error("it is an illegal cron string");
            return null;
        }

        return arr[5];
    }
}
