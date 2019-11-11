package com.dtstack.engine.dtscript.execution.spark210;

import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.UnitConvertUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.base.resource.AbstractYarnResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * spark 资源相关
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SparkResourceInfo extends AbstractYarnResourceInfo {

    private static final Logger logger = LoggerFactory.getLogger(SparkResourceInfo.class);

    public static final String SPARK_EXE_MEM = "executor.memory";

    public static final String SPARK_DRIVER_MEM = "driver.memory";

    public static final String SPARK_DRIVER_CPU = "driver.cores";

    public static final String STANDALONE_SPARK_EXECUTOR_CORES = "executor.cores";

    public static final String STANDALONE_SPARK_MAX_CORES = "cores.max";

    public static final int DEFAULT_EXECUTOR_CORES = 1;

    public static final int DEFAULT_CORES_MAX = 1;

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        super.calc();

        if(totalFreeCore == 0 || totalFreeMem == 0){
            return false;
        }

        Properties properties = jobClient.getConfProperties();
        int coresMax = properties.containsKey(STANDALONE_SPARK_MAX_CORES) ?
                MathUtil.getIntegerVal(properties.get(STANDALONE_SPARK_MAX_CORES)) : DEFAULT_CORES_MAX;

        int executorCores = properties.contains(STANDALONE_SPARK_EXECUTOR_CORES) ?
                MathUtil.getIntegerVal(properties.get(STANDALONE_SPARK_EXECUTOR_CORES)) : DEFAULT_EXECUTOR_CORES;

        int executorNum = coresMax/executorCores;
        executorNum = executorNum > 0 ? executorNum : 1;

        return checkNeedMEMForSparkStandalone(jobClient, totalFreeMem, executorNum, totalMem)
                && checkNeedCPUForSparkStandalone(jobClient, totalFreeCore, executorCores, totalCore);
    }

    public boolean checkNeedMEMForSparkStandalone(JobClient jobClient, int freeMemNum, int executorNum, int totalMem){
        int needMem = 512; //默认driver内存512
        if(jobClient.getConfProperties().containsKey(SPARK_DRIVER_MEM)) {
            String driverMem = (String) jobClient.getConfProperties().get(SPARK_DRIVER_MEM);
            needMem = UnitConvertUtil.convert2MB(driverMem);
        }

        int executorMem = 512; //默认app内存512M
        if(jobClient.getConfProperties().containsKey(SPARK_EXE_MEM)){
            String exeMem = (String) jobClient.getConfProperties().get(SPARK_EXE_MEM);
            executorMem = UnitConvertUtil.convert2MB(exeMem);
        }

        executorMem = executorMem * executorNum;
        needMem += executorMem;

        if(needMem > totalMem){
            throw new LimitResourceException("task's MEM rather then cluster's MEM");
        }

        if(needMem > freeMemNum){
            return false;
        }

        return true;
    }

    /**
     * 判断core是否符合需求
     * @param jobClient
     * @param freeCoreNum
     * @return
     */
    public boolean checkNeedCPUForSparkStandalone(JobClient jobClient, int freeCoreNum, int executorNum, int totalCore){
        int needCore = 1;
        if(jobClient.getConfProperties().containsKey(SPARK_DRIVER_CPU)){
            String driverCPU = (String) jobClient.getConfProperties().get(SPARK_DRIVER_CPU);
            needCore = MathUtil.getIntegerVal(driverCPU);
        }

        int executorCores = 1;
        if(jobClient.getConfProperties().containsKey(STANDALONE_SPARK_MAX_CORES)){
            String exeCPU = (String) jobClient.getConfProperties().get(STANDALONE_SPARK_MAX_CORES);
            executorCores = MathUtil.getIntegerVal(exeCPU);
        }

        needCore += executorCores * executorNum;

        if(needCore > totalCore){
            throw new LimitResourceException("task's Core rather then cluster's Core");
        }

        if(needCore > freeCoreNum){
            return false;
        }
        return true;
    }

}
