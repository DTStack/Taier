package com.dtstack.rdos.base.spark;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobType;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchExecutionOperator;
import com.google.common.collect.Maps;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/4/12
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class SparkSubmitTest {

    public void init() throws Exception{
        Map<String, Object> engineConfig = new HashMap<>();
        engineConfig.put("slots", 1);

        List<Map<String, Object>> engineList = new ArrayList<>();
        Map<String, Object> flinkEngine = Maps.newHashMap();
        flinkEngine.put("typeName", "spark");
        flinkEngine.put("sparkMaster", "spark://172.16.1.151:6066");
        flinkEngine.put("sparkSqlProxyPath", "hdfs://172.16.1.151:9000/user/spark/spark-0.0.1-SNAPSHOT.jar");
        flinkEngine.put("sparkSqlProxyMainClass", "com.dtstack.sql.main.SqlProxy");

        engineList.add(flinkEngine);
        engineConfig.put("engineTypes", engineList);

        JobSubmitExecutor.getInstance().init();
    }

    @Test
    public void submitJar() throws Exception{
        init();
        JobClient jobClient = new JobClient();
        jobClient.setEngineType("spark");
        jobClient.setJobType(EJobType.MR);
        BatchAddJarOperator addJarOperator = new BatchAddJarOperator();
        addJarOperator.setJarPath("hdfs://172.16.1.151:9000/user/spark/spark-0.0.1-SNAPSHOT.jar");
        addJarOperator.setMainClass("com.dtstack.main.SparkFirstTest");
        jobClient.addOperator(addJarOperator);
        jobClient.setJobName("engine_submit_job_test");

        JobSubmitExecutor.getInstance().submitJob(jobClient);
        System.out.println("---------submit spark mr job over--------");

    }

    @Test
    public void submitSql() throws Exception {
        init();
        JobClient jobClient = new JobClient();
        jobClient.setEngineType("spark");
        jobClient.setJobType(EJobType.SQL);
        jobClient.setComputeType(ComputeType.BATCH);
        BatchExecutionOperator executionOperator = new BatchExecutionOperator();

        String sql = "CREATE TABLE engine_test (a int, b int, c int);";
        executionOperator.createOperator(sql);
        jobClient.addOperator(executionOperator);
        jobClient.setJobName("engine_submit_job_test");

        JobSubmitExecutor.getInstance().submitJob(jobClient);
        System.out.println("---------submit spark sql job over--------");
    }

    @Test
    public void getJobStatus(){

    }

    @Test
    public void killJob(){

    }
}