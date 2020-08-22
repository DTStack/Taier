package com.dtstack.engine.kylin;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.kylin.enums.EKylinJobStatus;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author jiangbo
 * @date 2019/7/2
 */
public class KylinClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(KylinClient.class);

    private static final String KEY_JOB_ID = "uuid";
    private static final String KEY_EXCEPTION = "exception";
    private static final String KEY_STACKTRACE = "stacktrace";
    private static final String KEY_JOB_STATUS = "job_status";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_STEP_STATUS = "step_status";
    private static final String KEY_ERROR_INFO = "error_info";
    private static final String KEY_CMD_OUTPUT = "cmd_output";
    private static final String KEY_STEP_ID = "id";
    private static final String KEY_RETRY = "retry";

    private Gson gson = new Gson();

    private static ObjectMapper objectMapper = new ObjectMapper();

    private KylinHttpClient kylinHttpClient;

    private KylinConfig kylinConfig;

    @Override
    public void init(Properties prop) throws Exception {
        kylinConfig = KylinConfig.buildWithProperties(prop);
        kylinHttpClient = new KylinHttpClient();
        kylinHttpClient.init(kylinConfig);
    }

    @Override
    public JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.KYLIN.equals(jobType)){
            jobResult = triggerBuildCube(jobClient);
        }

        return jobResult;
    }

    private JobResult triggerBuildCube(JobClient jobClient){
        JsonObject jsonObject = gson.fromJson(jobClient.getPluginInfo(), JsonObject.class);
        if(jsonObject.get(KEY_RETRY) == null || !jsonObject.get(KEY_RETRY).getAsBoolean()){
            return createNewJobInstance();
        } else {
            return resumeJob();
        }
    }

    private JobResult createNewJobInstance(){
        discardErrorJob();

        KylinHttpClient.RequestResult requestResult = kylinHttpClient.buildCube(kylinConfig);
        if (requestResult.getStatusCode() == HttpStatus.SC_OK){
            JsonObject responseJson = gson.fromJson(requestResult.getBody(), JsonObject.class);
            String jobId = responseJson.get(KEY_JOB_ID).getAsString();
            return JobResult.createSuccessResult(jobId, jobId);
        } else {
            String errorInfo = parseErrorInfo(requestResult.getBody(), requestResult.getMsg());
            return JobResult.createErrorResult(errorInfo);
        }
    }

    private void discardErrorJob(){
        JsonElement lastJob = getLastJob();
        if(lastJob instanceof JsonNull){
            return;
        }

        String status = lastJob.getAsJsonObject().get(KEY_JOB_STATUS).getAsString();
        if(EKylinJobStatus.ERROR.name().equals(status)){
            String jobId = lastJob.getAsJsonObject().get(KEY_JOB_ID).getAsString();
            kylinHttpClient.discardJob(jobId);

            logger.info("Discard the last job [{}] of cube [{}]", jobId, kylinConfig.getCubeName());
        }
    }

    private JobResult resumeJob(){
        JsonElement lastJob = getLastJob();
        if(lastJob instanceof JsonNull){
            return createNewJobInstance();
        }

        String jobId = lastJob.getAsJsonObject().get(KEY_JOB_ID).getAsString();

        KylinHttpClient.RequestResult requestResult = kylinHttpClient.resumeJob(jobId);
        if (requestResult.getStatusCode() == HttpStatus.SC_OK){
            return JobResult.createSuccessResult(jobId, jobId);
        } else {
            String errorInfo = parseErrorInfo(requestResult.getBody(), requestResult.getMsg());
            return JobResult.createErrorResult(errorInfo);
        }
    }

    private String parseErrorInfo(String responseStr, String msg){
        JsonElement jsonResult = gson.fromJson(responseStr, JsonElement.class);
        if(jsonResult == null || jsonResult instanceof JsonNull){
            return msg;
        }

        StringBuilder errorInfo = new StringBuilder();
        if(jsonResult.getAsJsonObject().get(KEY_EXCEPTION) != null){
            errorInfo.append(jsonResult.getAsJsonObject().get(KEY_EXCEPTION).getAsString());
            errorInfo.append("\n");
        }

        if(jsonResult.getAsJsonObject().get(KEY_STACKTRACE) != null){
            errorInfo.append(jsonResult.getAsJsonObject().get(KEY_STACKTRACE).getAsString());
        }

        return errorInfo.toString();
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        KylinHttpClient.RequestResult requestResult = kylinHttpClient.cancelJob(jobIdentifier.getEngineJobId());
        if (requestResult.getStatusCode() == HttpStatus.SC_OK){
            return JobResult.createSuccessResult(jobIdentifier.getEngineJobId());
        } else {
            String errorInfo = parseErrorInfo(requestResult.getBody(), requestResult.getMsg());
            return JobResult.createErrorResult(errorInfo);
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        KylinHttpClient.RequestResult requestResult = kylinHttpClient.getJobStatus(jobIdentifier.getEngineJobId());
        if(requestResult.getStatusCode() == HttpStatus.SC_OK){
            JsonObject responseJson = gson.fromJson(requestResult.getBody(), JsonObject.class);
            String status = responseJson.get(KEY_JOB_STATUS).getAsString();

            EKylinJobStatus kylinJobStatus = EKylinJobStatus.getByName(status);
            return kylinJobStatus.getRdosStatus();
        }

        return null;
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        KylinHttpClient.RequestResult requestResult = kylinHttpClient.getJobStatus(jobIdentifier.getEngineJobId());

        if(requestResult.getStatusCode() != HttpStatus.SC_OK){
            Map<String, String> errorResult = new HashMap<>(1);
            errorResult.put(KEY_ERROR_INFO, requestResult.getMsg());
            try {
                return objectMapper.writeValueAsString(errorResult);
            } catch (Exception e){
                String msg = String.format("Get job log error,engineJobId:{%s},taskId:{%s},error info:\n",
                        jobIdentifier.getEngineJobId(), jobIdentifier.getTaskId());
                return msg + ExceptionUtil.getErrorMessage(e);
            }
        }

        JsonObject responseJson = gson.fromJson(requestResult.getBody(), JsonObject.class);
        JsonArray steps = responseJson.get(KEY_STEPS).getAsJsonArray();
        for (JsonElement step : steps) {
            String stepStatus = step.getAsJsonObject().get(KEY_STEP_STATUS).getAsString();
            String stepId = step.getAsJsonObject().get(KEY_STEP_ID).getAsString();

            if(EKylinJobStatus.ERROR.name().equals(stepStatus)){
                step.getAsJsonObject().add(KEY_ERROR_INFO, new JsonPrimitive(getErrorLog(jobIdentifier.getEngineJobId(), stepId)));
                break;
            }
        }

        return gson.toJson(responseJson);
    }

    private String getErrorLog(String jobId, String stepId){
        KylinHttpClient.RequestResult requestResult = kylinHttpClient.getStepOutput(jobId, stepId);
        if(requestResult.getStatusCode() != HttpStatus.SC_OK){
            return requestResult.getMsg();
        }

        JsonObject responseJson = gson.fromJson(requestResult.getBody(), JsonObject.class);
        if(responseJson.get(KEY_CMD_OUTPUT) != null){
            return responseJson.get(KEY_CMD_OUTPUT).getAsString();
        }

        return StringUtils.EMPTY;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return hasResource();
    }

    private JudgeResult hasResource(){
        JsonElement lastJob = getLastJob();
        if(lastJob instanceof JsonNull){
            return JudgeResult.ok();
        }

        String status = lastJob.getAsJsonObject().get(KEY_JOB_STATUS).getAsString();
        if(EKylinJobStatus.PENDING.name().equals(status) || EKylinJobStatus.RUNNING.name().equals(status)){
            String msg = String.format("The last job of cube [%s] is in status [%s], waiting for it to finish", kylinConfig.getCubeName(), status);
            logger.info(msg);
            return JudgeResult.notOk(false, msg);
        }

        if(EKylinJobStatus.STOPPED.name().equals(status)){
            String msg = String.format("The last job of cube [%s] is in status [%s],please resume or discard it first", kylinConfig.getCubeName(), status);
            logger.warn(msg);
            return JudgeResult.notOk(false, msg);
        }

        return JudgeResult.ok();
    }

    private JsonElement getLastJob(){
        KylinHttpClient.RequestResult requestResult = kylinHttpClient.getJobList(kylinConfig.getCubeName(), 1);
        if(requestResult.getStatusCode() != HttpStatus.SC_OK){
            logger.warn("Check the job status of kylin error:{}", requestResult.getMsg());
            return JsonNull.INSTANCE;
        }

        JsonArray jobsJson = gson.fromJson(requestResult.getBody(), JsonArray.class);
        if(jobsJson == null || jobsJson.isJsonNull() || jobsJson.size() == 0){
            return JsonNull.INSTANCE;
        }

        return jobsJson.get(0).getAsJsonObject();
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }
}
