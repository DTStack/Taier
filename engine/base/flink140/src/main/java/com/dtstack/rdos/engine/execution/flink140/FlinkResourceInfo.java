package com.dtstack.rdos.engine.execution.flink140;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class FlinkResourceInfo extends EngineResourceInfo{

    public static final String FLINK_SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String FLINK_MR_PARALLELISM = "mr.job.parallelism";

    @Override
    public boolean judgeSlots(JobClient jobClient) {

        int availableSlots = 0;
        for(NodeResourceInfo value : nodeResourceMap.values()){
            int freeSlots = MathUtil.getIntegerVal(value.getProp("freeSlots"));
            availableSlots += freeSlots;
        }

        boolean result = true;
        if(jobClient.getConfProperties().containsKey(FLINK_SQL_ENV_PARALLELISM)){
            int maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_SQL_ENV_PARALLELISM));
            result = result && availableSlots >= maxParall;
        }

        if(jobClient.getConfProperties().containsKey(FLINK_MR_PARALLELISM)){
            int maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_MR_PARALLELISM));
            result = result && availableSlots >= maxParall;
        }

        return result;
    }
}
