package com.dtstack.rdos.engine.entrance.service;

import java.util.Map;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.service.paramObject.ParamAction;
import com.dtstack.rdos.engine.entrance.sql.SqlParser;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;


public class ActionServiceImpl{
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	public void start(Map<String,Object> params) throws Exception{
		ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
		new JobClient(SqlParser.parser(paramAction)).submit();
	}
	
	public void stop(Map<String,Object> params) throws Exception{
		ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
		JobClient.stop(paramAction.getEngineTaskId());
	}
}
