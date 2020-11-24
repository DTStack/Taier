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

    public static final String URL = "url";

    public static final String USER = "user";

    public static final String PASSWORD = "password";

    public static final String SERVER = "server";

    public static final String NODES = "nodes";

    public static final String TOKEN = "token";

    public static Map<String,String> confMap = new HashMap<>();

    public static String getConf(String key){
        return confMap.get(key);
    }
}
