package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 环境参数工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:02 2021/9/22
 * company: www.dtstack.com
 */
public class EnvUtil {

    /**
     * 测试连通性超时时间对应环境变量
     */
    private final static String TEST_CONN_TIMEOUT_KEY = "LOADER_TEST_CONN_TIMEOUT";

    // 测试连通性超时时间。单位：秒
    private final static int TEST_CONN_TIMEOUT = 30;

    /**
     * 获取测试连通性超时时间
     *
     * @return 超时时间
     */
    public static int getTestConnTimeout() {
        return getTestConnTimeout(TEST_CONN_TIMEOUT);
    }

    /**
     * 获取测试连通性超时时间
     *
     * @param def 默认超时时间
     * @return 超时时间
     */
    public static int getTestConnTimeout(int def) {
        String timeout = System.getenv(TEST_CONN_TIMEOUT_KEY);
        if (StringUtils.isNotBlank(timeout) && NumberUtils.isNumber(timeout)) {
            return NumberUtils.toInt(timeout);
        }
        return def;
    }
}
