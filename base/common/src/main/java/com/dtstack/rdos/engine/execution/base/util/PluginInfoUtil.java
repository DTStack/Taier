package com.dtstack.rdos.engine.execution.base.util;

import com.dtstack.rdos.common.config.ConfigParse;
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

    public static String getPluginType(String pluginInfo) throws IOException {
        Map<String, Object> params = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        return MathUtil.getString(params.get(ConfigParse.TYPE_NAME_KEY));
    }
}
