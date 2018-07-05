package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.flink150.enums.FlinkMode;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkResourceInfo extends EngineResourceInfo{

    public static final String FLINK_SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String FLINK_MR_PARALLELISM = "mr.job.parallelism";

    private static FlinkMode flinkMode = FlinkMode.LEGACY_MODE;
    private static int FLINK_NEW_MODE_MAX_SLOTS = 0;

    @Override
    public boolean judgeSlots(JobClient jobClient) {

        int availableSlots = 0;
        int totalSlots = 0;

        for(NodeResourceInfo value : nodeResourceMap.values()){
            int freeSlots = MathUtil.getIntegerVal(value.getProp("freeSlots"));
            int slotsNumber = MathUtil.getIntegerVal(value.getProp("slotsNumber"));
            availableSlots += freeSlots;
            totalSlots += slotsNumber;
        }
        if (FlinkMode.NEW_MODE == flinkMode) {
            availableSlots = FLINK_NEW_MODE_MAX_SLOTS - totalSlots + availableSlots;
            totalSlots = FLINK_NEW_MODE_MAX_SLOTS;
        }

        //没有资源直接返回false
        if(availableSlots == 0){
            return false;
        }

        boolean result = true;
        int maxParall = 0;

        if(jobClient.getConfProperties().containsKey(FLINK_SQL_ENV_PARALLELISM)){
            maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_SQL_ENV_PARALLELISM));
            result = result && availableSlots >= maxParall;
        }

        if(jobClient.getConfProperties().containsKey(FLINK_MR_PARALLELISM)){
            maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_MR_PARALLELISM));
            result = result && availableSlots >= maxParall;
        }

        if(totalSlots < maxParall){
            throw new RdosException("任务配置资源超过集群最大资源");
        }

        return result;
    }

    public static void setFlinkNewModeMaxSlots(int flinkNewModeMaxSlots) {
        FLINK_NEW_MODE_MAX_SLOTS = flinkNewModeMaxSlots;
    }

    public static void setFlinkMode(FlinkMode flinkMode) {
        FlinkResourceInfo.flinkMode = flinkMode;
    }
}
