package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.dtscript.api.DtYarnConstants;

import java.util.Map;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/02/25
 **/
public class KrbUtils {

    private static final String PYTHON_TYPE = "python";

    public static boolean hasKrb(Map<String, String> env) {
        String principal = env.get(DtYarnConstants.ENV_PRINCIPAL);
        return principal != null;
    }

    public static boolean isPythonType(String appType) {
        if (null == appType) {
            return false;
        } else {
            return appType.toLowerCase().startsWith(PYTHON_TYPE);
        }
    }
}
