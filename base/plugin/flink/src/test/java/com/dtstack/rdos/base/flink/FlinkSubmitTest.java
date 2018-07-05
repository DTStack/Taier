package com.dtstack.rdos.base.flink;

import com.dtstack.rdos.common.http.PoolHttpClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class FlinkSubmitTest {

    public void init() throws Exception{
        Map<String, Object> engineConfig = new HashMap<>();
        engineConfig.put("slots", 1);

        List<Map<String, Object>> engineList = new ArrayList<>();
        Map<String, Object> flinkEngine = Maps.newHashMap();
        flinkEngine.put("typeName", "flink120");
        flinkEngine.put("flinkZkAddress", "172.16.1.151");
        flinkEngine.put("flinkZkNamespace", "/flink");
        flinkEngine.put("flinkClusterId", "default");
        flinkEngine.put("jarTmpDir", "D:\\tmp");

        engineList.add(flinkEngine);
        engineConfig.put("engineTypes", engineList);

        JobSubmitExecutor.getInstance().init();
    }

    @Test
    public void submitJar() throws Exception{
    }

    @Test
    public void submitSql(){



    }

    @Test
    public void testGetJobInfo(){

        String reqUrl = "http://172.16.1.151:8081/jobs/ed27939c59a611fe54e256eb5b044c91";
        String response = null;
        try {
            response = PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
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