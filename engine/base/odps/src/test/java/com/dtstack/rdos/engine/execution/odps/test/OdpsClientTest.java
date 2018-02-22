package com.dtstack.rdos.engine.execution.odps.test;


import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.odps.OdpsClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OdpsClientTest {
    private static final String ODPS_TEST_CONFIG_PATH = "ODPS_TEST_CONFIG_PATH";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static OdpsClient odpsClient;

    @BeforeClass
    public static void before() throws Exception {
        System.out.println("before");
        String configPath = System.getenv(ODPS_TEST_CONFIG_PATH);
        Properties prop = new Properties();
        try(FileInputStream fis = new FileInputStream(configPath)) {
            prop.load(fis);
            odpsClient = new OdpsClient();
            odpsClient.init(prop);
        }

    }

    @Test
    @Ignore
    public void runSql() throws IOException, ClassNotFoundException {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  odpsClient.submitSqlJob(jobClient);
        System.out.println(jobResult);
    }

    @Test
    @Ignore
    public void getRunningStatus() throws Exception {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  odpsClient.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        RdosTaskStatus status = odpsClient.getJobStatus(jobId);
        System.out.println(status);
    }

    @Test
    @Ignore
    public void getFinishedStatus() throws Exception {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  odpsClient.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        Thread.sleep(10000);
        RdosTaskStatus status = odpsClient.getJobStatus(jobId);
        System.out.println(status);
    }

    @Test
    @Ignore
    public void cancelJob() throws Exception {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  odpsClient.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        RdosTaskStatus status = odpsClient.getJobStatus(jobId);

        JobResult jobResult1 =  odpsClient.cancelJob(jobId);
        System.out.println("cancel result: " + jobResult1);

        Thread.sleep(3000);
        RdosTaskStatus cancelStatus = odpsClient.getJobStatus(jobId);
        System.out.println("cancel status: " + cancelStatus);
    }

}
