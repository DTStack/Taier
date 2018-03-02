package com.dtstack.rdos.engine.execution.mysql;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;


public class MysqlClientTest {
    private static final String MYSQL_TEST_CONFIG_PATH = "MYSQL_TEST_CONFIG_PATH";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static AbsClient client;

    @BeforeClass
    public static void before() throws Exception {
//        Map<String,Object> nodeConfig =
//        ConfigParse.setConfigs(nodeConfig);
        Map<String, Object> pluginStoreInfoMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("dbUrl", "jdbc:mysql://172.16.10.61:3306/rdos?charset=utf8");
        map.put("dbUserName", "dtstack");
        map.put("dbPwd", "abc123");
        pluginStoreInfoMap.put("pluginStoreInfo", map);
        ConfigParse.setConfigs(pluginStoreInfoMap);

        System.out.println("before");
        String configPath = System.getenv(MYSQL_TEST_CONFIG_PATH);
        Properties prop = new Properties();
        try(FileInputStream fis = new FileInputStream(configPath)) {
            prop.load(fis);
            client = new MysqlClient();
            client.init(prop);

        }

    }

    @Test
    public void runSql() throws IOException, ClassNotFoundException {
        String query = "select * from tb1;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  client.submitSqlJob(jobClient);
        System.out.println(jobResult);
    }

    @Test
    public void getRunningStatus() throws Exception {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  client.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        RdosTaskStatus status = client.getJobStatus(jobId);
        System.out.println(status);
    }

    @Test
    public void getFinishedStatus() throws Exception {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  client.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        Thread.sleep(10000);
        RdosTaskStatus status = client.getJobStatus(jobId);
        System.out.println(status);
    }

    @Test
    @Ignore
    public void cancelJob() throws Exception {
        String query = "select * from tb250; select 111 from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  client.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        RdosTaskStatus status = client.getJobStatus(jobId);

        JobResult jobResult1 =  client.cancelJob(jobId);
        System.out.println("cancel result: " + jobResult1);

        Thread.sleep(3000);
        RdosTaskStatus cancelStatus = client.getJobStatus(jobId);
        System.out.println("cancel status: " + cancelStatus);
    }

    @Test
    @Ignore
    public void getLog() throws Exception {
        //String query = "select * from tb250; select 111 from tb250;";
        String query = "select * from tb250;";
        JobClient jobClient = new JobClient();
        jobClient.setSql(query);
        JobResult jobResult =  client.submitSqlJob(jobClient);
        String jobId = jobResult.getData("jobid");
        System.out.println("my jobid: " + jobId);
        RdosTaskStatus status = client.getJobStatus(jobId);

        Thread.sleep(3000);
        String log = client.getJobLog(jobId);
        System.out.println("log: " + log);
    }

    @Test
    @Ignore
    public void getSlot() throws Exception {
        client.getAvailSlots();
    }

    @Test
    @Ignore
    public void createResource() throws Exception {
//        Odps odps = odpsClient.getOdps();
//        FileResource resource = new FileResource();
//        resource.setName("hyf_heheda");
//
//        String source = "/Users/softfly/company/backbone/README.md";
//        File file = new File(source);
//        InputStream is = new FileInputStream(file);
//
//        odps.resources().create(resource, is);

    }

    @Test
    public void findResource() throws Exception {
//        Odps odps = odpsClient.getOdps();
//        for(Resource resource : odps.resources()) {
//            if(resource.getName().equals("hyf_heheda")) {
//                System.out.println("fuck you");
//            }
//        }
    }
}