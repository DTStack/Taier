package com.dtstack.engine.service;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.pojo.ParamAction;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/3/6
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class TestRunSql {

    public void initFlink(){
        Map<String, Object> engineConfig = new HashMap<>();
        engineConfig.put("slots", 1);

        List<Map<String, Object>> engineList = new ArrayList<>();
        Map<String, Object> flinkEngine = Maps.newHashMap();
        flinkEngine.put("typeName", "flink");
        flinkEngine.put("engineZkAddress", "172.16.1.151");
        flinkEngine.put("engineZkNamespace", "/flink");
        flinkEngine.put("engineClusterId", "default");
        flinkEngine.put("jarTmpDir", "D:\\tmp");

        engineList.add(flinkEngine);
        engineConfig.put("engineTypes", engineList);
    }

    @Test
    public void runFlinkJob() throws Exception {

        initFlink();
        String sql = "ADDJAR ADD JAR WITH http://114.55.63.129/flinktest-1.0-SNAPSHOT.jar;\n" +
                "CREATE SOURCE TABLE MyTable(\n" +
                "message STRING) WITH (\n" +
                "type='KAFKA09',\n" +
                "bootstrapServers='172.16.1.151:9092',\n" +
                "offsetReset='earliest',\n" +
                "topic='dt_distribute_log'\n" +
                ");\n" +
                "CREATE SCALA FUNCTION hashCode WITH com.xc.udf.MyHashCode;\n" +
                "select message, hashCode(message) as hashcode from MyTable;\n" +
                "CREATE RESULT TABLE MyResult(\n" +
                "message STRING, hashcode int) WITH (\n" +
                "type='mysql',\n" +
                "dbURL='jdbc:mysql://172.16.1.203:3306/flink_test',\n" +
                "userName='dtstack_xc',\n" +
                "password='dtstack_xc',\n" +
                "tableName='flink_test'\n" +
                ");";

        ParamAction paramAction = new ParamAction();
        paramAction.setSqlText(sql);
        paramAction.setComputeType(ComputeType.STREAM.ordinal());

        JobClient jobClient = new JobClient();
        jobClient.setJobType(EJobType.SQL);
        jobClient.setTaskId("test_sql_job");
        jobClient.setJobName("test_sql_job");
        jobClient.setComputeType(ComputeType.STREAM);

        System.out.println("---------wait----------");
    }

    public void initSpark(){
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
    }

    @Test
    public void runSparkJob() throws Exception {

        String sql = "ADD BATCH SQL WITH DROP TABLE IF EXISTS engine_test1;";
        ParamAction paramAction = new ParamAction();
        paramAction.setSqlText(sql);
        paramAction.setComputeType(ComputeType.BATCH.ordinal());

        initSpark();
        JobClient jobClient = new JobClient();
        jobClient.setEngineType("spark");
        jobClient.setJobType(EJobType.SQL);
        jobClient.setTaskId("test_sql_job");
        jobClient.setJobName("test_sql_job");
        jobClient.setComputeType(ComputeType.BATCH);

        System.out.println("---------wait----------");
    }

}
