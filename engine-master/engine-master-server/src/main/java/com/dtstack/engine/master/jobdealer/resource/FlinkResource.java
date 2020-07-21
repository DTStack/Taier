package com.dtstack.engine.master.jobdealer.resource;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class FlinkResource extends CommonResource {

    private static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";

    private static final String SESSION = "session";
    private static final String PER_JOB = "per_job";

    @Override
    public ComputeResourceType getComputeResourceType(JobClient jobClient) {
        Properties properties = jobClient.getConfProperties();
        ComputeType computeType = jobClient.getComputeType();
        String modeStr = properties.getProperty(FLINK_TASK_RUN_MODE_KEY);

        if (StringUtils.isEmpty(modeStr)) {
            if (ComputeType.STREAM == computeType) {
                return ComputeResourceType.Yarn;
            } else {
                return ComputeResourceType.FlinkYarnSession;
            }
        }
        if (SESSION.equalsIgnoreCase(modeStr)) {
            return ComputeResourceType.FlinkYarnSession;
        } else if (PER_JOB.equalsIgnoreCase(modeStr)) {
            return ComputeResourceType.Yarn;
        }
        throw new RdosDefineException("not support mode: " + modeStr);
    }
}
