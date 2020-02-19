package com.dtstack.engine.common.logstore;


import com.dtstack.engine.common.logstore.mysql.MysqlLogStore;

/**
 * Created by sishu.yss on 2018/4/17.
 */
public class LogStoreFactory {

    public static AbstractLogStore getLogStore(Integer type){
        if(type == null){
            return MysqlLogStore.getInstance();
        }
        return null;
    }

}
