package com.dtstack.rdos.engine.test;

import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.stream.AddJarOperator;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.*;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class FlinkSubmitTest {

    public void init(){
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

        JobSubmitExecutor.getInstance().init(engineConfig);
        JobSubmitExecutor.getInstance().start();
    }

    @Test
    public void submitJar(){
        init();
        JobClient jobClient = new JobClient();
        AddJarOperator addJarOperator = new AddJarOperator();
        addJarOperator.setJarPath("http://114.55.63.129/flinktest-1.0-SNAPSHOT.jar");
        jobClient.addOperator(addJarOperator);

        JobSubmitExecutor.getInstance().submitJob(jobClient);
        System.out.println("---------over--------");
    }

    @Test
    public void submitSql(){



    }

    @Test
    public void testGetJobInfo(){

        String reqUrl = "http://172.16.1.151:8081/jobs/ed27939c59a611fe54e256eb5b044c91";
        String response = PoolHttpClient.get(reqUrl);
        if(response != null){
            System.out.println(response);
        }

    }

    @Test
    public void testRdosTaskStatus(){
        RdosTaskStatus rdosTaskStatus = RdosTaskStatus.getTaskStatus("UNSUBMIT");
        System.out.println("----over---" + rdosTaskStatus);
    }
}
