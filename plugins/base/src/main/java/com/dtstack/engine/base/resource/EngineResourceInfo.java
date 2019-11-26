package com.dtstack.engine.base.resource;

import com.dtstack.engine.common.JobClient;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public interface EngineResourceInfo {

    String LIMIT_RESOURCE_ERROR = "LIMIT RESOURCE ERROR:";

    boolean judgeSlots(JobClient jobClient);

}
