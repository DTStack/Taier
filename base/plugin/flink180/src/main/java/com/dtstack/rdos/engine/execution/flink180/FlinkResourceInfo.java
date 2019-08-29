package com.dtstack.rdos.engine.execution.flink180;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.flink180.enums.FlinkYarnMode;
import com.dtstack.rdos.engine.execution.flink180.util.FlinkUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkResourceInfo extends EngineResourceInfo {

    private static final Logger logger = LoggerFactory.getLogger(FlinkResourceInfo.class);

    private final static ObjectMapper objMapper = new ObjectMapper();

    private static final String FLINK_SQL_ENV_PARALLELISM = "sql.env.parallelism";

    private static final String FLINK_MR_PARALLELISM = "mr.job.parallelism";

    private static final String DEFAULT_QUEUE = "default";

    /**
     * 允许在yarn上处于accept的任务数量
     */
    private static int yarnAccepterTaskNumber = 1;

    private YarnClient yarnClient;

    /**
     * 是否开启弹性容量-->暂时都是false
     *
     */
    private boolean elasticCapacity;

    private boolean isPerJob;

    private String queue = DEFAULT_QUEUE;

    public FlinkResourceInfo(JobClient jobClient, YarnClient yarnClient) {
        this.yarnClient = yarnClient;

        FlinkConfig flinkConfig = getJobFlinkConf(jobClient.getPluginInfo());
        FlinkYarnMode taskRunMode = FlinkUtil.getTaskRunMode(jobClient.getConfProperties(),jobClient.getComputeType());
        isPerJob = ComputeType.STREAM == jobClient.getComputeType() || FlinkYarnMode.isPerJob(taskRunMode);
        if(flinkConfig != null){
            queue = flinkConfig.getQueue();
        }
    }

    public boolean isPerJob(){
        return isPerJob;
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        if (isPerJob){
            return judgePerjobResource(jobClient, queue);
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

    private boolean judgePerjobResource(JobClient jobClient, String queue) {
        FlinkPerJobResourceInfo resourceInfo = new FlinkPerJobResourceInfo();
        try {
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> acceptedApps = yarnClient.getApplications(enumSet).stream().
                    filter(report->report.getQueue().endsWith(queue)).collect(Collectors.toList());
            if (acceptedApps.size() > yarnAccepterTaskNumber) {
                return false;
            }

            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            int containerLimit = 0;
            float capacity = 1;
            if (!elasticCapacity){
                capacity = getQueueRemainCapacity(1, queue, yarnClient.getRootQueueInfos());
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
            logger.error("Flink judgePerjobResource error: ", e);
        }
        return resourceInfo.judgeSlots(jobClient);
    }

    private float getQueueRemainCapacity(float coefficient, String queue, List<QueueInfo> queueInfos){
        float capacity = 0;
        for (QueueInfo queueInfo : queueInfos){
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                float subCoefficient = queueInfo.getCapacity() * coefficient;
                capacity = getQueueRemainCapacity(subCoefficient, queue, queueInfo.getChildQueues());
            }
            if (queue.equals(queueInfo.getQueueName())){
                queueCapacity = coefficient * queueInfo.getCapacity();
                capacity = queueCapacity * (1 - queueInfo.getCurrentCapacity());
            }
            if (capacity>0){
                return capacity;
            }
        }
        return capacity;
    }

    private FlinkConfig getJobFlinkConf(String pluginInfo) {
        if (StringUtils.isBlank(pluginInfo)){
            return null;
        }
        try {
            return PublicUtil.jsonStrToObject(pluginInfo, FlinkConfig.class);
        } catch (IOException e) {
            logger.error("Json to object error: ", e);
            return null;
        }
    }

    public FlinkResourceInfo getAvailSlots(String message, int flinkSessionSlotCount){
        if(StringUtils.isNotBlank(message)){
            try{
                Map<String, Object> taskManagerInfo = objMapper.readValue(message, Map.class);
                if(taskManagerInfo.containsKey("taskmanagers")){
                    List<Map<String, Object>> taskManagerList = (List<Map<String, Object>>) taskManagerInfo.get("taskmanagers");
                    if (taskManagerList.size()==0){
                        //FIXME 外部传入
                        this.addNodeResource(new EngineResourceInfo.NodeResourceDetail("1", flinkSessionSlotCount, flinkSessionSlotCount));
                    }else {
                        for(Map<String, Object> tmp : taskManagerList){
                            int freeSlots = MapUtils.getIntValue(tmp,"freeSlots");
                            int slotsNumber = MapUtils.getIntValue(tmp, "slotsNumber");
                            this.addNodeResource(new EngineResourceInfo.NodeResourceDetail((String)tmp.get("id"),freeSlots,slotsNumber));
                        }
                    }
                }
            }catch (Exception e){
                logger.error("", e);
            }
        }
        return this;
    }

}
