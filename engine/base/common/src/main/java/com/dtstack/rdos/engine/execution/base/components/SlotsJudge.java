package com.dtstack.rdos.engine.execution.base.components;

import java.util.Map;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;

/**
 * 
 * @author sishu.yss
 *
 */
public class SlotsJudge {

    public static final String SQL_MAX_ENV_PARALLELISM = "sql.max.env.parallelism";

    /**
	 * 判断job所依赖的执行引擎的资源是否足够
	 * @param jobClient
	 * @param slotsInfo
	 * @return
	 */
	public boolean judgeSlots(JobClient jobClient, Map<String,Map<String,Map<String,Object>>> slotsInfo){

		if(EngineType.isFlink(jobClient.getEngineType())){
			String flinkKey = null;
			for(String key : slotsInfo.keySet()){
				if(EngineType.isFlink(key)){
					flinkKey = key;
					break;
				}
			}

			if(flinkKey == null){
				throw new RdosException("not support engine type:" + jobClient.getEngineType());
			}

			return judgeFlinkResource(jobClient, slotsInfo.get(flinkKey));
		}else if(EngineType.isSpark(jobClient.getEngineType())){

			String sparkKey = null;
			for(String key : slotsInfo.keySet()){
				if(EngineType.isFlink(key)){
					sparkKey = key;
					break;
				}
			}

			if(sparkKey == null){
				throw new RdosException("not support engine type:" + jobClient.getEngineType());
			}

			return judgeSparkResource(jobClient, slotsInfo.get(sparkKey));
		}else{
			throw new RdosException("not support engine type:" + jobClient.getEngineType());
		}
	}

	/**
	 * 必须为各个taskManager 预留1个slot
     * FIXME 当前只对在属性中设置了parallelism的任务进行控制
	 * @param jobClient
	 * @param slotsInfo
	 * @return
	 */
	public boolean judgeFlinkResource(JobClient jobClient, Map<String, Map<String,Object>> slotsInfo){

        int avaliableSlots = 0;
        for(Map<String, Object> value : slotsInfo.values()){
            int freeSlots = MathUtil.getIntegerVal(value.get("freeSlots"));
            freeSlots = freeSlots > 0 ? (freeSlots - 1) : 0;
            avaliableSlots += freeSlots;
        }

        if(jobClient.getConfProperties().containsKey(SQL_MAX_ENV_PARALLELISM)){
            int maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(SQL_MAX_ENV_PARALLELISM));
            return avaliableSlots >= maxParall;
        }else{//没有填写最大并行度则返回true.
            return true;
        }
	}

	/**
	 * TODO
	 * @param jobClient
	 * @param slotsInfo
	 * @return
	 */
	public boolean judgeSparkResource(JobClient jobClient, Map<String, Map<String,Object>> slotsInfo){
		return false;
	}


	
}
