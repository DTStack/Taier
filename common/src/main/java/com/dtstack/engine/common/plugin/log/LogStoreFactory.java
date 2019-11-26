package com.dtstack.engine.common.plugin.log;

import com.dtstack.engine.common.plugin.log.mysql.MysqlLogStore;

/**
 * Created by sishu.yss on 2018/4/17.
 */
public class LogStoreFactory {

    public static LogStore getLogStore(Integer type){
        if(type == null){
            return MysqlLogStore.getInstance();
        }
        return null;
    }

}
