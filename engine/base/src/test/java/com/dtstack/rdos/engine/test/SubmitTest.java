package com.dtstack.rdos.engine.test;

import com.dtstack.rdos.common.util.HttpClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;
import com.dtstack.rdos.engine.execution.base.operator.AddJarOperator;

import org.junit.Test;

import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class SubmitTest {

    @Test
    public void submitJar(){

        Properties properties = new Properties();
        properties.setProperty("host", "172.16.1.151");
        properties.setProperty("port", "6123");

        JobSubmitExecutor.getInstance().init(ClientType.Flink, properties);
        JobSubmitExecutor.getInstance().start();

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
        String response = HttpClient.get(reqUrl);
        if(response != null){
            System.out.println(response);
        }

    }
}
