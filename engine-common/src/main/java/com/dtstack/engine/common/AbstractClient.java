package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.restart.RestartStrategyType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class AbstractClient implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    @Override
	public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try{
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null){
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(),",") + ")");
            }
        }catch (Exception e){
            logger.error("", e);
            jobResult = JobResult.createErrorResult(e);
        }finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    /**
     * job 处理具体实现的抽象
     *
     * @param jobClient 对象参数
     * @return 处理结果
     */
    protected abstract JobResult processSubmitJobWithType(JobClient jobClient);

    @Override
    public String getJobLog(JobIdentifier jobId) {
        return "";
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return false;
    }

    protected void beforeSubmitFunc(JobClient jobClient){
    }

    protected void afterSubmitFunc(JobClient jobClient){
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public RestartStrategyType getRestartStrategyType(JobIdentifier jobIdentifier) {
        return RestartStrategyType.NONE;
    }
}
