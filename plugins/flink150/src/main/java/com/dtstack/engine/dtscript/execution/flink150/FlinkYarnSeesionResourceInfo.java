package com.dtstack.engine.dtscript.execution.flink150;

import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.AbstractFlinkResourceInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.dtstack.engine.dtscript.execution.flink150.constrant.ConfigConstrant.*;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkYarnSeesionResourceInfo extends AbstractFlinkResourceInfo {

    private static final Logger logger = LoggerFactory.getLogger(FlinkYarnSeesionResourceInfo.class);

    private final static ObjectMapper objMapper = new ObjectMapper();

    public FlinkYarnSeesionResourceInfo() {
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return judgeYarnSeesionResource(jobClient);
    }

    private boolean judgeYarnSeesionResource(JobClient jobClient) {
        int sqlEnvParallel = 1;
        int mrParallel = 1;

        if (jobClient.getConfProperties().containsKey(SQL_ENV_PARALLELISM)) {
            sqlEnvParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(SQL_ENV_PARALLELISM));
        }

        if (jobClient.getConfProperties().containsKey(MR_JOB_PARALLELISM)) {
            mrParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(MR_JOB_PARALLELISM));
        }

        return super.judgeFlinkSessionResource(sqlEnvParallel, mrParallel);
    }

    public FlinkYarnSeesionResourceInfo getFlinkSessionSlots(String message) {
        if (StringUtils.isNotBlank(message)) {
            try {
                Map<String, Object> taskManagerInfo = objMapper.readValue(message, Map.class);
                if (taskManagerInfo.containsKey("taskmanagers")) {
                    List<Map<String, Object>> taskManagerList = (List<Map<String, Object>>) taskManagerInfo.get("taskmanagers");
                    for (Map<String, Object> tmp : taskManagerList) {
                        int freeSlots = MapUtils.getIntValue(tmp, "freeSlots");
                        int slotsNumber = MapUtils.getIntValue(tmp, "slotsNumber");
                        this.addNodeResource(new NodeResourceDetail((String) tmp.get("id"), freeSlots, slotsNumber));
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return this;
    }

}
