package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.util.MathUtil;

/**
 * @author yuebai
 * @date 2021-02-01
 */
public class JobKeyUtils {

    public static  String getJobTriggerTimeFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 1) {
            return "";
        }

        String timeStr = strings[strings.length - 1];
        if (timeStr.length() < 8) {
            return "";
        }

        return timeStr.substring(0, 8);
    }


    /**
     * 此处获取的时候schedule_task_shade 的id 不是task_id
     * @param jobKey
     * @return
     */
    public static Long getTaskShadeIdFromJobKey(String jobKey) {
        String[] strings = jobKey.split("_");
        if (strings.length < 2) {
            return -1L;
        }

        String id = strings[strings.length - 2];
        try {
            return MathUtil.getLongVal(id);
        } catch (Exception e) {
            return -1L;
        }
    }
}
