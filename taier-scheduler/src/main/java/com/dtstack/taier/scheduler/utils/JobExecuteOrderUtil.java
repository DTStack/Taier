package com.dtstack.taier.scheduler.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: dazhi
 * @Date: 2022/1/18 3:16 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobExecuteOrderUtil {

    /**
     * 按照计划时间生成具体排列序号
     *
     * @param triggerTime 计划时间
     * @param count
     * @return
     */
    public static Long buildJobExecuteOrder(String triggerTime, Integer count) {
        if (StringUtils.isBlank(triggerTime)) {
            throw new RuntimeException("cycTime is not null");
        }

        // 时间格式 yyyyMMddHHmmss  截取 jobExecuteOrder = yyMMddHHmm +  9位的自增
        String substring = triggerTime.substring(2, triggerTime.length() - 2);
        String increasing = String.format("%09d", count);
        return Long.parseLong(substring + increasing);
    }
}
