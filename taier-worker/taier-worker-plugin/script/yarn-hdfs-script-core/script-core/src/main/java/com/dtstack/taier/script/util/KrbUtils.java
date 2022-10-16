package com.dtstack.taier.script.util;

import com.dtstack.taier.script.api.ScriptConstants;

import java.util.Map;

public class KrbUtils {

    private static final String PYTHON_TYPE = "python";

    public static boolean hasKrb(Map<String, String> env) {
        String principal = env.get(ScriptConstants.ENV_PRINCIPAL);
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
