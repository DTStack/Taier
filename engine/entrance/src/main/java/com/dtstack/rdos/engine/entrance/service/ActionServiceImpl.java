package com.dtstack.rdos.engine.entrance.service;

import java.util.Map;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.db.dao.RdosActionLogDAO;
import com.dtstack.rdos.engine.entrance.enumeration.RdosActionLogStatus;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.entrance.service.paramObject.ParamAction;
import com.dtstack.rdos.engine.entrance.sql.SqlParser;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.enumeration.Restoration;
import com.dtstack.rdos.engine.entrance.http.HttpSendClient;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ActionServiceImpl{
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

	private RdosActionLogDAO rdosActionLogDAO = new RdosActionLogDAO();
	
	public void start(Map<String,Object> params) throws Exception{
		ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
		String address = zkDistributed.getExcutionNode();
		if(paramAction.getRequestStart()==RequestStart.NODE.getStart()||zkDistributed.getLocalAddress().equals(address)){
			BrokerDataNode brokerDataNode = BrokerDataNode.initBrokerDataNode();
			brokerDataNode.getMetas().put(paramAction.getTaskId(), RdosTaskStatus.UNSUBMIT.getStatus().byteValue());
			zkDistributed.updateSynchronizedBrokerData(zkDistributed.getLocalAddress(),brokerDataNode, false);
			zkDistributed.updateLocalMemTaskStatus(brokerDataNode);
			new JobClient(SqlParser.parser(paramAction),paramAction.getName(),paramAction.getTaskId(),paramAction.getEngineTaskId(), EJobType.getEJobType(paramAction.getTaskType()), ComputeType.getComputeType(paramAction.getComputeType()), Restoration.getRestoration(paramAction.getIsRestoration()),paramAction.getActionLogId()).submit();
			rdosActionLogDAO.updateActionStatus(paramAction.getActionLogId(), RdosActionLogStatus.SUCCESS.getStatus());
		}else{
 			paramAction.setRequestStart(RequestStart.NODE.getStart());
            HttpSendClient.actionStart(address,paramAction);
		}
	}
	
	public void stop(Map<String,Object> params) throws Exception{
		ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
		JobClient.stop(paramAction.getEngineTaskId());
		rdosActionLogDAO.updateActionStatus(paramAction.getActionLogId(), RdosActionLogStatus.SUCCESS.getStatus());
	}
}
