package com.dtstack.rdos.engine.execution.learning;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

/**
 * 用于存储从xlearning上获取的资源信息
 * Date: 2018/6/27
 * Company: www.dtstack.com
 * @author jingzhen
 */
public class LearningResourceInfo extends EngineResourceInfo {
    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return false;
    }
}
