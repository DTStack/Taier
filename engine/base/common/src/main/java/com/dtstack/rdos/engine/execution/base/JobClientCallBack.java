package com.dtstack.rdos.engine.execution.base;

import java.util.Map;

/**
 * Created by sishu.yss on 2017/5/24.
 */
public  interface  JobClientCallBack {

    String JOB_STATUS = "JOB_STATUS";

    void execute(Map<String, ? extends Object> param);

}
