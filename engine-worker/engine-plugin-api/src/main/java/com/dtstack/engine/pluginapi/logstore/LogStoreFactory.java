package com.dtstack.engine.pluginapi.logstore;


import com.dtstack.engine.pluginapi.logstore.mysql.MysqlLogStore;

import java.util.Map;

/**
 * Created by sishu.yss on 2018/4/17.
 */
public class LogStoreFactory {

    private static AbstractLogStore logStore;

    public static AbstractLogStore getLogStore() {
        return getLogStore(null);
    }
    public static synchronized AbstractLogStore getLogStore(Map<String, String> dbConfig) {
        if (logStore == null) {
            if (dbConfig == null) {
                return null;
            }
            logStore = MysqlLogStore.getInstance(dbConfig);
        }
        return logStore;
    }

}
