//package com.dtstack.rdos.engine.execution.odps.test;
//
//
//import com.aliyun.odps.FileResource;
//import com.aliyun.odps.Instance;
//import com.aliyun.odps.Job;
//import com.aliyun.odps.Odps;
//import com.aliyun.odps.Resource;
//import com.aliyun.odps.account.Account;
//import com.aliyun.odps.account.AliyunAccount;
//import com.aliyun.odps.task.SQLTask;
//import com.dtstack.rdos.commom.exception.RdosException;
//import JobClient;
//import RdosTaskStatus;
//import JobResult;
//import com.dtstack.rdos.engine.execution.odps.OdpsClient;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Calendar;
//import java.util.Map;
//import java.util.Properties;
//import java.util.UUID;
//
//public class OdpsClientTest {
//    private static final String ODPS_TEST_CONFIG_PATH = "ODPS_TEST_CONFIG_PATH";
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//    private static OdpsClient odpsClient;
//
//    public static void main(String[] args) throws Exception {
//        Account account = new AliyunAccount("LTAIljBeC8ei9Yy0", "gwTWasH7sEE0pSUEuiXnw7JecXyfGF");
//        Odps odps = new Odps(account);
//        odps.setEndpoint("http://service.odps.aliyun.com/api");
//        odps.setDefaultProject("dtstack_dev");
//        JobClient jobClient = new JobClient();
//        jobClient.setSql("select * from tb250; select 111 from tb250;");
//        String[] sqls = jobClient.getSql().split(";");
//        Job job = new Job();
//        String guid = UUID.randomUUID().toString();
//        for (String sql : sqls) {
//            String taskName = "query_task_" + Calendar.getInstance().getTimeInMillis();
//            SQLTask task = new SQLTask();
//            task.setName(taskName);
//            task.setQuery(sql+";");
//            task.setProperty("guid", guid);
//            job.addTask(task);
//        }
//        Instance instance = odps.instances().create(job);
//        System.out.println(instance.getId());
//
//        String jobId = instance.getId();
//        Instance.TaskStatus.Status taskStatus = null;
//
//        while (true) {
//            try {
//                Map<String, Instance.TaskStatus> statusMap = instance.getTaskStatus();
//                if (statusMap == null || statusMap.size() == 0) {
//                    throw new RuntimeException("statusMap empty: " + jobId);
//                }
//                taskStatus = statusMap.entrySet().iterator().next().getValue().getStatus();
//
//                if (taskStatus == null) {
//                    throw new RuntimeException("can't find task status for task: " + jobId);
//                }
//            } catch (Exception e) {
//                throw new RdosException(e.getMessage());
//            }
//
//            RdosTaskStatus rdosTaskStatus = null;
//            switch (taskStatus) {
//                case WAITING:
//                    rdosTaskStatus = RdosTaskStatus.SUBMITTING;
//                    break;
//                case SUCCESS:
//                    rdosTaskStatus = RdosTaskStatus.FINISHED;
//                    System.out.println(rdosTaskStatus);
//                    return;
//                case SUSPENDED:
//                    rdosTaskStatus = RdosTaskStatus.KILLED;
//                    break;
//                case CANCELLED:
//                    rdosTaskStatus = RdosTaskStatus.CANCELED;
//                    break;
//                default:
//                    rdosTaskStatus = RdosTaskStatus.valueOf(taskStatus.name().toUpperCase());
//            }
//            System.out.println(rdosTaskStatus);
//        }
//    }
//
//    @BeforeClass
//    public static void before() throws Exception {
//        System.out.println("before");
//        String configPath = System.getenv(ODPS_TEST_CONFIG_PATH);
//        Properties prop = new Properties();
//        try (FileInputStream fis = new FileInputStream(configPath)) {
//            prop.load(fis);
//            odpsClient = new OdpsClient();
//            odpsClient.init(prop);
//        }
//
//    }
//
//    @Test
//    @Ignore
//    public void runSql() throws IOException, ClassNotFoundException {
//        String query = "select * from tb250; select 111 from tb250;";
//        JobClient jobClient = new JobClient();
//        jobClient.setSql(query);
//        JobResult jobResult = odpsClient.submitSqlJob(jobClient);
//        System.out.println(jobResult);
//    }
//
//    @Test
//    @Ignore
//    public void getRunningStatus() throws Exception {
//        String query = "select * from tb250; select 111 from tb250;";
//        JobClient jobClient = new JobClient();
//        jobClient.setSql(query);
//        JobResult jobResult = odpsClient.submitSqlJob(jobClient);
//        String jobId = jobResult.getData("jobid");
//        System.out.println("my jobid: " + jobId);
//        RdosTaskStatus status = odpsClient.getJobStatus(jobId);
//        System.out.println(status);
//    }
//
//    @Test
//    @Ignore
//    public void getFinishedStatus() throws Exception {
//        String query = "select * from tb250; select 111 from tb250;";
//        JobClient jobClient = new JobClient();
//        jobClient.setSql(query);
//        JobResult jobResult = odpsClient.submitSqlJob(jobClient);
//        String jobId = jobResult.getData("jobid");
//        System.out.println("my jobid: " + jobId);
//        Thread.sleep(10000);
//        RdosTaskStatus status = odpsClient.getJobStatus(jobId);
//        System.out.println(status);
//    }
//
//    @Test
//    @Ignore
//    public void cancelJob() throws Exception {
//        String query = "select * from tb250; select 111 from tb250;";
//        JobClient jobClient = new JobClient();
//        jobClient.setSql(query);
//        JobResult jobResult = odpsClient.submitSqlJob(jobClient);
//        String jobId = jobResult.getData("jobid");
//        System.out.println("my jobid: " + jobId);
//        RdosTaskStatus status = odpsClient.getJobStatus(jobId);
//
//        JobResult jobResult1 = odpsClient.cancelJob(jobId);
//        System.out.println("cancel result: " + jobResult1);
//
//        Thread.sleep(3000);
//        RdosTaskStatus cancelStatus = odpsClient.getJobStatus(jobId);
//        System.out.println("cancel status: " + cancelStatus);
//    }
//
//    @Test
//    @Ignore
//    public void getLog() throws Exception {
//        //String query = "select * from tb250; select 111 from tb250;";
//        String query = "select * from tb250;";
//        JobClient jobClient = new JobClient();
//        jobClient.setSql(query);
//        JobResult jobResult = odpsClient.submitSqlJob(jobClient);
//        String jobId = jobResult.getData("jobid");
//        System.out.println("my jobid: " + jobId);
//        RdosTaskStatus status = odpsClient.getJobStatus(jobId);
//
//        Thread.sleep(3000);
//        String log = odpsClient.getJobLog(jobId);
//        System.out.println("log: " + log);
//    }
//
//    @Test
//    @Ignore
//    public void getSlot() throws Exception {
//        odpsClient.getAvailSlots();
//    }
//
//    @Test
//    @Ignore
//    public void createResource() throws Exception {
//        Odps odps = odpsClient.getOdps();
//        FileResource resource = new FileResource();
//        resource.setName("hyf_heheda");
//
//        String source = "/Users/softfly/company/backbone/README.md";
//        File file = new File(source);
//        InputStream is = new FileInputStream(file);
//
//        odps.resources().create(resource, is);
//
//    }
//
//    @Test
//    public void findResource() throws Exception {
//        Odps odps = odpsClient.getOdps();
//        for (Resource resource : odps.resources()) {
//            if (resource.getName().equals("hyf_heheda")) {
//                System.out.println("fuck you");
//            }
//        }
//    }
//
//}
