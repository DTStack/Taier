package com.dtstack.engine.common.api;

import com.dtstack.engine.api.pojo.*;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.api.message.*;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.remote.annotation.RemoteClient;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/9/4 3:26 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RemoteClient("worker")
public interface WorkerApi {

    JudgeResult judgeSlots(MessageJudgeSlots messageJudgeSlots) throws Exception;

    JobResult submitJob(MessageSubmitJob messageSubmitJob) throws Exception;

    RdosTaskStatus getJobStatus(MessageGetJobStatus messageGetJobStatus) throws Exception;

    String getEngineMessageByHttp(MessageGetEngineMessageByHttp messageGetEngineMessageByHttp) throws Exception;

    String getEngineMessageByHttp(MessageGetEngineLog messageGetEngineLog) throws Exception;

    String getCheckpoints(MessageGetCheckpoints messageGetCheckpoints) throws Exception;

    List<String> getRollingLogBaseInfo(MessageRollingLogBaseInfo messageRollingLogBaseInfo) throws Exception;

    String getJobMaster(MessageGetJobMaster messageGetJobMaster) throws Exception;

    JobResult stopJob(MessageStopJob messageStopJob) throws Exception;

    List<String> containerInfos(MessageContainerInfos messageContainerInfos) throws Exception;

    ComponentTestResult testConnect(MessageTestConnectInfo messageTestConnectInfo) throws Exception;

    List<List<Object>> executeQuery(MessageExecuteQuery messageExecuteQuery) throws Exception;

    String uploadStringToHdfs(MessageUploadInfo messageUploadInfo) throws Exception;

    ClusterResource clusterResource(MessageResourceInfo messageResourceInfo) throws Exception;

    List<Column> getAllColumns(MessageAllColumns messageAllColumns) throws Exception;

    CheckResult grammarCheck(MessageGrammarCheck messageGrammarCheck) throws Exception;

    List<DtScriptAgentLabel> getDtScriptAgentLabel(MessageDtScriptAgentLabel messageDtScriptAgentLabel) throws Exception;
}
