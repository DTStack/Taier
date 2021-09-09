package com.dtstack.engine.dummy;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.pojo.ComponentTestResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * 用于流程上压测的dummy插件
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/4/13
 */
public class DummyClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(DummyClient.class);

    @Override
    public void init(Properties prop) throws Exception {
    }

    @Override
    public String getJobLog(JobIdentifier jobId) {
        Map<String, Object> jobLog = new HashMap<>(2);
        jobLog.put("jobId", jobId.getTaskId());
        jobLog.put("msg_info", System.currentTimeMillis());
        return JSONObject.toJSONString(jobLog);
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.ok();
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return JobResult.createSuccessResult(jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId());
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return RdosTaskStatus.FINISHED;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return StringUtils.EMPTY;
    }

    @Override
    public String getMessageByHttp(String path) {
        return StringUtils.EMPTY;
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return JobResult.createSuccessResult(jobClient.getTaskId(), jobClient.getTaskId());
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            SftpConfig sftpConfig = PublicUtil.jsonStrToObject(pluginInfo, SftpConfig.class);
            // check sftpConfig 准确性
            SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
            //测试路径是否存在
            Vector res = sftpFileManage.listFile(sftpConfig.getPath());
            if (null != res) {
                componentTestResult.setResult(true);
            }
        } catch (Exception e) {
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            componentTestResult.setResult(false);
        }
        return componentTestResult;
    }
}
