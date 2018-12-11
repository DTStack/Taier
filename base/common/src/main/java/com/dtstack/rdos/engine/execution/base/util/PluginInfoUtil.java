package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Reason:
 * Date: 2018/12/8
 * Company: www.dtstack.com
 * @author xuchao
 */

public class PluginInfoUtil {

    public static final String HADOOP_CONF_KEY = "hadoopConf";

    public static String getSpecKeyConf(String pluginInfo, String key) throws IOException {
        Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        return MathUtil.getString(params.get(key));
    }
}
