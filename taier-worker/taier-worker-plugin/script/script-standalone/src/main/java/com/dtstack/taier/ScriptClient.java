package com.dtstack.taier;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.CheckResult;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

public class ScriptClient extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptClient.class);

    @Override
    public void init(Properties prop) {
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {
        ScriptExecutor.buildExecutor();
        ScriptJob scriptJob = new ScriptJob(jobClient.getJobId(), jobClient.getShellParams());
        ScriptExecutor.getJobMap().put(jobClient.getJobId(), scriptJob);
        ScriptExecutor.getSqlExecutor().execute(scriptJob);

        JSONObject extraInfo = new JSONObject();
        extraInfo.put("shellLogPath", scriptJob.getShellLogPath());
        extraInfo.put("runMode", "standalone");

        JobResult jobResult = JobResult.createSuccessResult(jobClient.getJobId(), String.valueOf(ScriptExecutor.getJobMap()
                .getOrDefault(jobClient.getJobId(), new ScriptJob()).getProcessId()));
        jobResult.setExtraInfoJson(extraInfo);
        return jobResult;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            LOGGER.info("kill job , jobId is {}", jobIdentifier.getJobId());
            if (!ScriptExecutor.getJobMap().containsKey(jobIdentifier.getJobId())) {
                return JobResult.createSuccessResult(jobIdentifier.getJobId(), jobIdentifier.getEngineJobId());
            }
            ScriptJob scriptJob = ScriptExecutor.getJobMap().get(jobIdentifier.getJobId());
            if (Objects.isNull(scriptJob.getProcessId())) {
                return JobResult.createSuccessResult(jobIdentifier.getJobId(), jobIdentifier.getEngineJobId());
            }
            ProcessUtil.killProcess(scriptJob.getProcessId());
            return JobResult.createSuccessResult(jobIdentifier.getJobId(), jobIdentifier.getEngineJobId());
        } catch (Exception e) {
            LOGGER.info("kill job error, jobId is {}", jobIdentifier.getJobId());
            return JobResult.createErrorResult(String.format("kill jobId: %s process error : %s", jobIdentifier.getJobId(), Objects.nonNull(e.getCause()) ? e.getCause().getMessage() : e.getMessage()));
        }
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) {
        ScriptJob scriptJob = ScriptExecutor.getJobMap().get(jobIdentifier.getJobId());
        if (Objects.isNull(scriptJob)) {
            return null;
        }
        if (BooleanUtils.isTrue(scriptJob.getStatus())) {
            return TaskStatus.FINISHED;
        } else if (BooleanUtils.isFalse(scriptJob.getStatus())) {
            return TaskStatus.FAILED;
        }
        return null;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        ThreadPoolExecutor sqlExecutor = ScriptExecutor.getSqlExecutor();
        if (Objects.nonNull(sqlExecutor)) {
            if (sqlExecutor.getQueue().size() == sqlExecutor.getCorePoolSize()) {
                return JudgeResult.notOk(String.format("当前脚本任务运行线程数超过限制，默认值 %s", ScriptExecutor.DEFAULT_POOL_SIZE));
            }
        }
        return JudgeResult.ok();
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        return ComponentTestResult.ok();
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return null;
    }

    @Override
    public CheckResult grammarCheck(JobClient jobClient) {
        return CheckResult.success();
    }

}
