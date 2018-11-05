package com.dtstack.rdos.engine.execution.flink130;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkResourceInfo extends EngineResourceInfo {

    public static final String FLINK_SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String FLINK_MR_PARALLELISM = "mr.job.parallelism";

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        int sqlEnvParallel = 1;
        int mrParallel = 1;
        if(jobClient.getConfProperties().containsKey(FLINK_SQL_ENV_PARALLELISM)){
            sqlEnvParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_SQL_ENV_PARALLELISM));
        }
        if(jobClient.getConfProperties().containsKey(FLINK_MR_PARALLELISM)){
            mrParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_MR_PARALLELISM));
        }
        return super.judgeFlinkResource(sqlEnvParallel,mrParallel);
    }
}
