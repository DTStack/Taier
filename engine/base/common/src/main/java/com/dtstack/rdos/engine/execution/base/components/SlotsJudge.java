package com.dtstack.rdos.engine.execution.base.components;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.EDeployType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.util.EngineRestParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author sishu.yss
 *
 */
public class SlotsJudge {

    private static final Logger logger = LoggerFactory.getLogger(SlotsJudge.class);

    public static final String FLINK_SQL_ENV_PARALLELISM = "sql.env.parallelism";

    public static final String FLINK_MR_PARALLELISM = "mr.job.parallelism";

    public static final String SPARK_EXE_MEM = "executor.memory";

    public static final String SPARK_DRIVER_MEM = "driver.memory";

    public static final String SPARK_DRIVER_CPU = "driver.cores";

    public static final String SPARK_EXE_CPU = "cores.max";

    public static final Pattern capacityPattern = Pattern.compile("(\\d+)\\s*([a-zA-Z]{1,2})");

    /**
	 * 判断job所依赖的执行引擎的资源是否足够
	 * @param jobClient
	 * @param slotsInfo
	 * @return
	 */
	public boolean judgeSlots(JobClient jobClient, Map<String, Map<String,Map<String,Object>>> slotsInfo){

        EDeployType deployType = JobSubmitExecutor.getInstance().getEngineDeployType(jobClient.getEngineType());

		if(EngineType.isFlink(jobClient.getEngineType()) && deployType == EDeployType.STANDALONE){
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
		}else if(EngineType.isSpark(jobClient.getEngineType()) && deployType == EDeployType.STANDALONE){

			String sparkKey = null;
			for(String key : slotsInfo.keySet()){
				if(EngineType.isSpark(key)){
					sparkKey = key;
					break;
				}
			}

			if(sparkKey == null){
				throw new RdosException("not support engine type:" + jobClient.getEngineType());
			}

			return judgeSparkResource(jobClient, slotsInfo.get(sparkKey));
		}else{
			logger.info("not support engine type:{} and return default true.", jobClient.getEngineType());
			return true;
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

        boolean result = true;
        if(jobClient.getConfProperties().containsKey(FLINK_SQL_ENV_PARALLELISM)){
            int maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_SQL_ENV_PARALLELISM));
            result = result && avaliableSlots >= maxParall;
        }

        if(jobClient.getConfProperties().containsKey(FLINK_MR_PARALLELISM)){
            int maxParall = MathUtil.getIntegerVal(jobClient.getConfProperties().get(FLINK_MR_PARALLELISM));
            result = result && avaliableSlots >= maxParall;
        }

        return result;
	}

	/**
	 * 为各个worker 预留1024M的剩余空间
	 * @param jobClient
	 * @param slotsInfo
	 * @return
	 */
	public boolean judgeSparkResource(JobClient jobClient, Map<String, Map<String,Object>> slotsInfo){

	    int coreNum = 0;
	    int memNum = 0;
	    for(Map<String, Object> tmpMap : slotsInfo.values()){
            int workerFreeMem = (int) tmpMap.get(EngineRestParseUtil.SparkRestParseUtil.MEMORY_FREE_KEY);
            int workerFreeCpu = (int) tmpMap.get(EngineRestParseUtil.SparkRestParseUtil.CORE_FREE_KEY);
            workerFreeMem = workerFreeMem - 1024;
            workerFreeMem = workerFreeMem > 0 ? workerFreeMem : 0;
            memNum += workerFreeMem;
            coreNum += workerFreeCpu;
        }

        return checkNeedMEMForSpark(jobClient, memNum) && checkNeedCPUForSpark(jobClient, coreNum);
	}

    /**
     * 判断
     * @param jobClient
     * @param memNum
     * @return
     */
	public boolean checkNeedMEMForSpark(JobClient jobClient, int memNum){
        int needMem = 0;
        if(jobClient.getConfProperties().containsKey(SPARK_DRIVER_MEM)) {
            String driverMem = (String) jobClient.getConfProperties().get(SPARK_DRIVER_MEM);
            needMem += convert2MB(driverMem);
        }else{//默认driver内存512
            needMem += 512;
        }

        if(jobClient.getConfProperties().containsKey(SPARK_EXE_MEM)){
            String exeMem = (String) jobClient.getConfProperties().get(SPARK_EXE_MEM);
            needMem += convert2MB(exeMem);
        }else{//默认app内存512
            needMem += 512;
        }

        if(needMem > memNum){
            return false;
        }

        return true;
    }

    /**
     * 判断core是否符合需求
     * @param jobClient
     * @param coreNum
     * @return
     */
	public boolean checkNeedCPUForSpark(JobClient jobClient, int coreNum){
        int neeCore = 0;
        if(jobClient.getConfProperties().containsKey(SPARK_DRIVER_CPU)){
            String driverCPU = (String) jobClient.getConfProperties().get(SPARK_DRIVER_CPU);
            neeCore += MathUtil.getIntegerVal(driverCPU);
        }else{
            neeCore += 1;
        }

        if(jobClient.getConfProperties().containsKey(SPARK_EXE_CPU)){
            String exeCPU = (String) jobClient.getConfProperties().get(SPARK_EXE_CPU);
            neeCore += MathUtil.getIntegerVal(exeCPU);
        }else{
            neeCore += 1;
        }

        if(neeCore > coreNum){
            return false;
        }
        return true;
    }

    /**
     * 暂时只做kb,mb,gb转换
     * @param memStr
     * @return
     */
	public Integer convert2MB(String memStr){
        Matcher matcher = capacityPattern.matcher(memStr);
        if(matcher.find() && matcher.groupCount() == 2){
            String num = matcher.group(1);
            String unit = matcher.group(2).toLowerCase();
            if(unit.contains("g")){
                Double mbNum = MathUtil.getDoubleVal(num) * 1024;
                return mbNum.intValue();
            }else if(unit.contains("m")){
                return MathUtil.getDoubleVal(num).intValue();
            }else if(unit.contains("k")){
                Double mbNum = MathUtil.getDoubleVal(num) / 1024;
                return mbNum.intValue();
            }else{
                 logger.error("can not convert memStr:" + memStr +", return default 512.");
            }
        }else{
            logger.error("can not convert memStr:" + memStr +", return default 512.");
        }

        return 512;
    }


	
}
