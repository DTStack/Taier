package com.dtstack.engine.base.spark;

import com.dtstack.engine.common.JobSubmitExecutor;
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

    }

    @Test
    public void submitSql() throws Exception {
    }

    @Test
    public void getJobStatus(){

    }

    @Test
    public void killJob(){

    }
}