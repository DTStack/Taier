package com.dtstack.taier.develop.datasource.convert.dto;

/**
 * @author ：nanqi
 * date：Created in 下午5:15 2021/7/29
 * company: www.dtstack.com
 */
public class PluginInfoUtils {
    public static final int MAX_ROWS = 5000;
    public static final int QUERY_TIMEOUT = 60000;

    public static PluginInfoDTO setNullPropertiesToDefaultValue(PluginInfoDTO pluginInfo) {
        if (pluginInfo.getMaxRows() == null) {
            pluginInfo.setMaxRows(MAX_ROWS);
        }

        if (pluginInfo.getQueryTimeout() == null) {
            pluginInfo.setQueryTimeout(QUERY_TIMEOUT);
        }

        return pluginInfo;
    }
}
