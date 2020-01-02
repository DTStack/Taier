package com.dtstack.engine.common;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public  interface  JobClientCallBack {

    default void execute(Map<String, ? extends Object> param){
    }

    void updateStatus(Integer status);
}
