package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.flink150.enums.FlinkYarnMode;
import com.dtstack.rdos.engine.execution.flink150.util.FlinkUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    private boolean judgePerjobResource(JobClient jobClient, String queueName) {
        FlinkPerJobResourceInfo resourceInfo = new FlinkPerJobResourceInfo();
        try {
            resourceInfo.getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        } catch (Exception e) {
            logger.error("Flink judgePerjobResource error: ", e);
        }
        return resourceInfo.judgeSlots(jobClient);
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

    public FlinkResourceInfo getAvailSlots(String message){
        if(StringUtils.isNotBlank(message)){
            try{
                Map<String, Object> taskManagerInfo = objMapper.readValue(message, Map.class);
                if(taskManagerInfo.containsKey("taskmanagers")){
                    List<Map<String, Object>> taskManagerList = (List<Map<String, Object>>) taskManagerInfo.get("taskmanagers");
                    for(Map<String, Object> tmp : taskManagerList){
                        int freeSlots = MapUtils.getIntValue(tmp,"freeSlots");
                        int slotsNumber = MapUtils.getIntValue(tmp, "slotsNumber");
                        this.addNodeResource(new EngineResourceInfo.NodeResourceDetail((String)tmp.get("id"),freeSlots,slotsNumber));
                    }
                }
            }catch (Exception e){
                logger.error("", e);
            }
        }
        return this;
    }

}
