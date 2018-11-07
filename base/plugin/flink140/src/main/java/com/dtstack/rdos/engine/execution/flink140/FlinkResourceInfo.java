package com.dtstack.rdos.engine.execution.flink140;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.flink140.enums.FlinkYarnMode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.EnumSet;
import java.util.List;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkResourceInfo extends EngineResourceInfo {

    public static final String FLINK_SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String FLINK_MR_PARALLELISM = "mr.job.parallelism";

    public static FlinkYarnMode flinkYarnMode;

    public static YarnClient yarnClient;

    public static String queue;

    public static boolean elasticCapacity;

    public static int yarnAccepterTaskNumber;

    @Override
    public boolean judgeSlots(JobClient jobClient) {

        if (ComputeType.STREAM == jobClient.getComputeType() && FlinkYarnMode.PER_JOB == flinkYarnMode){
           return judgePerjobResource(jobClient);
        }
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

    private boolean judgePerjobResource(JobClient jobClient) {
        FlinkPerJobResourceInfo resourceInfo = new FlinkPerJobResourceInfo();
        try {
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> acceptedApps = yarnClient.getApplications(enumSet);
            if (acceptedApps.size() > yarnAccepterTaskNumber) {
                return false;
            }
            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            int containerLimit = 0;
            float capacity = 1;
            if (!elasticCapacity){
                capacity = getQueueRemainCapacity(1,yarnClient.getRootQueueInfos());
            }
            resourceInfo.setCapacity(capacity);
            for(NodeReport report : nodeReports){
                Resource capability = report.getCapability();
                Resource used = report.getUsed();
                int totalMem = capability.getMemory();
                int totalCores = capability.getVirtualCores();

                int usedMem = used.getMemory();
                int usedCores = used.getVirtualCores();

                int freeCores = totalCores - usedCores;
                int freeMem = totalMem - usedMem;

                if (freeMem > containerLimit) {
                    containerLimit = freeMem;
                }
                resourceInfo.addNodeResource(new EngineResourceInfo.NodeResourceDetail(report.getNodeId().toString(), totalCores,usedCores,freeCores, totalMem,usedMem,freeMem));
            }
            resourceInfo.setContainerLimit(containerLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceInfo.judgeSlots(jobClient);
    }

    private float getQueueRemainCapacity(float coefficient, List<QueueInfo> queueInfos){
        float capacity = 0;
        for (QueueInfo queueInfo : queueInfos){
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                float subCoefficient = queueInfo.getCapacity() * coefficient;
                capacity = getQueueRemainCapacity(subCoefficient, queueInfo.getChildQueues());
            }
            if (queue.equals(queueInfo.getQueueName())){
                capacity = coefficient * queueInfo.getCapacity() * (1 - queueInfo.getCurrentCapacity());
            }
            if (capacity>0){
                return capacity;
            }
        }
        return capacity;
    }

}
