package com.dtstack.rdos.engine.entrance.service;

import java.util.Map;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.service.paramObject.ParamAction;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;


public class ActionServiceImpl{
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	public void start(Map<String,Object> params) throws Exception{
		ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
	}
	
	public void stop(Map<String,Object> parmas){
		
	}
}
