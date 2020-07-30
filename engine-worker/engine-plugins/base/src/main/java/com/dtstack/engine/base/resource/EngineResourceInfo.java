package com.dtstack.engine.base.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JudgeResult;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public interface EngineResourceInfo {

    String LIMIT_RESOURCE_ERROR = "LIMIT RESOURCE ERROR:";


    JudgeResult judgeSlots(JobClient jobClient) throws Exception;

}
