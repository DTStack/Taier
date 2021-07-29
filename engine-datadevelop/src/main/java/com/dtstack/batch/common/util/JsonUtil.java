package com.dtstack.batch.common.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:08 2020/2/25
 * @Description：Json补充 工具类
 */
public class JsonUtil {
    /**
     * 获取对应可以的值，如果不存在则直接附空字符串
     *
     * @param json
     * @param key
     * @return
     */
    public static String getStringDefaultEmpty(JSONObject json, String key) {
        return json.containsKey(key) ? json.getString(key) : "";
    }
}
