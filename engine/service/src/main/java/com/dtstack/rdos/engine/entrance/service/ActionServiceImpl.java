package com.dtstack.rdos.engine.entrance.service;

import java.util.Map;

import com.dtstack.rdos.common.annotation.Forbidden;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosBatchActionLogDAO;
import com.dtstack.rdos.engine.db.dao.RdosBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosStreamActionLogDAO;
import com.dtstack.rdos.engine.db.dao.RdosStreamTaskDAO;
import com.dtstack.rdos.engine.db.dataobject.base.ActionLog;
import com.dtstack.rdos.engine.entrance.enumeration.RdosActionLogStatus;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.*;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;

/**
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class ActionServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosStreamActionLogDAO streamActionLogDAO = new RdosStreamActionLogDAO();
    private RdosBatchActionLogDAO batchActionLogDAO = new RdosBatchActionLogDAO();

    private RdosStreamTaskDAO streamTaskDAO = new RdosStreamTaskDAO();
    private RdosBatchJobDAO batchJobDAO = new RdosBatchJobDAO();

    public void start(Map<String, Object> params) throws Exception {

        String jobId = null;
        Integer computeType = null;
        try {
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            jobId = paramAction.getTaskId();
            computeType = paramAction.getComputeType();
            ActionLog dbActionLog = getActionLog(paramAction.getActionLogId(), computeType);
            if (dbActionLog.getStatus() == RdosActionLogStatus.SUCCESS.getStatus()) {//已经提交过
                return;
            }
            updateActionLogStatus(paramAction.getActionLogId(), computeType, RdosActionLogStatus.SUCCESS.getStatus());
            String taskId = TaskIdUtil.getZkTaskId(paramAction.getComputeType(), paramAction.getEngineType(), paramAction.getTaskId());
            boolean isAlreadyInThisNode = zkDistributed.checkIsAlreadyInThisNode(taskId);

            String address = zkDistributed.getExcutionNode();
            if (isAlreadyInThisNode || paramAction.getRequestStart() == RequestStart.NODE.getStart() || zkDistributed.getLocalAddress().equals(address)) {
                JobClient jobClient = new JobClient(paramAction);

                jobClient.setJobClientCallBack(new JobClientCallBack() {
                    @Override
                    public void execute() {
                        BrokerDataNode brokerDataNode = BrokerDataNode.initBrokerDataNode();
                        brokerDataNode.getMetas().put(taskId, RdosTaskStatus.UNSUBMIT.getStatus().byteValue());
                        zkDistributed.updateSynchronizedBrokerData(zkDistributed.getLocalAddress(), brokerDataNode, false);
                        zkDistributed.updateLocalMemTaskStatus(brokerDataNode);
                    }
                });
                jobClient.submit();

            } else {
                paramAction.setRequestStart(RequestStart.NODE.getStart());
                HttpSendClient.actionStart(address, paramAction);
            }
        } catch (Throwable e) {
            //提交失败,修改对应的提交jobid为提交失败
            logger.error("", e);
            if (jobId != null) {
                updateJobStatus(jobId, computeType, RdosTaskStatus.SUBMITFAILD.getStatus());
            }
        }

    }

    public void stop(Map<String, Object> params) throws Exception {
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        JobClient.stop(paramAction);
        updateActionLogStatus(paramAction.getActionLogId(), paramAction.getComputeType(), RdosActionLogStatus.UNSTART.getStatus());
    }

    @Forbidden
    public ActionLog getActionLog(Long actionLogId, Integer computeType) {
        ActionLog actionLog = null;
        if (ComputeType.STREAM.getComputeType().equals(computeType)) {
            actionLog = streamActionLogDAO.findActionLogById(actionLogId);
        } else {
            actionLog = batchActionLogDAO.findActionLogById(actionLogId);
        }
        return actionLog;
    }

    @Forbidden
    public void updateActionLogStatus(Long actionLogId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getComputeType().equals(computeType)) {
            streamActionLogDAO.updateActionStatus(actionLogId, status);
        } else {
            batchActionLogDAO.updateActionStatus(actionLogId, status);
        }
    }

    @Forbidden
    public void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getComputeType().equals(computeType)) {
            streamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            batchJobDAO.updateJobStatus(jobId, status);
        }
    }


}
