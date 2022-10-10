package com.dtstack.taier.datasource.plugin.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:45 2020/2/27
 * @Description：JSONObject 工具类 {@link JSONObject}
 */
public class JSONUtil {
    /**
     * 判断字符串是否可以转化为json对象
     *
     * @param content
     * @return
     */
    public static boolean isJsonObject(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        try {
            JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否可以转化为JSON数组
     *
     * @param content
     * @return
     */
    public static boolean isJsonArray(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        try {
            JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 转化为 JSON 对象
     *
     * @param content
     * @return
     */
    public static JSONObject parseJsonObject(String content) {
        if (StringUtils.isBlank(content)) {
            return new JSONObject();
        }

        try {
            return JSONObject.parseObject(content);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 转化为 JSON 数组
     *
     * @param content
     * @return
     */
    public static JSONArray parseJsonArray(String content) {
        if (StringUtils.isBlank(content)) {
            return new JSONArray();
        }

        try {
            return JSONArray.parseArray(content);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /**
     * 转化为 MAP
     * @param content
     * @return
     */
    public static Map parseMap (String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyMap();
        }

        try {
            return JSONObject.parseObject(content, HashMap.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
