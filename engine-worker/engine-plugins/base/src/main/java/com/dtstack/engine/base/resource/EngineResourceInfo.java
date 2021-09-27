package com.dtstack.engine.base.resource;

import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.pojo.JudgeResult;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public interface EngineResourceInfo {

    JudgeResult judgeSlots(JobClient jobClient);

}
