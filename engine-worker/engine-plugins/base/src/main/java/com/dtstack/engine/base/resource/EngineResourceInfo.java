package com.dtstack.engine.base.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.JudgeResult;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public interface EngineResourceInfo {

    JudgeResult judgeSlots(JobClient jobClient);

}
