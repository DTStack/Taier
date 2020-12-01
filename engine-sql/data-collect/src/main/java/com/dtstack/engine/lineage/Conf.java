package com.dtstack.engine.lineage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chener
 * @Classname Conf
 * @Description TODO
 * @Date 2020/11/23 11:21
 * @Created chener@dtstack.com
 */
public class Conf {
    public static final String CLASS_NAME = "class_name";

    public static final String URL_SUFFIX = "url";

    public static final String USER_SUFFIX = "user";

    public static final String PASSWORD_SUFFIX = "password";

    public static final String BATCH_URL_SUFFIX = "batch.url";

    public static final String BATCH_USER_SUFFIX = "batch.user";

    public static final String BATCH_PASSWORD_SUFFIX = "batch.password";

    public static final String ASSERTS_URL_SUFFIX = "asserts.url";

    public static final String ASSERTS_USER_SUFFIX = "asserts.user";

    public static final String ASSERTS_PASSWORD_SUFFIX = "asserts.password";

    public static final String SERVER = "server";

    public static final String NODES = "nodes";

    public static final String TOKEN = "token";

    public static Map<String,String> confMap = new HashMap<>();

    public static String getConf(String key){
        return confMap.get(key);
    }
}
