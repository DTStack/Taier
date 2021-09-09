package com.dtstack.engine.dtscript;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.pojo.JobResult;
import com.dtstack.engine.pluginapi.pojo.JudgeResult;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.dtstack.engine.dtscript.am.ApplicationMaster;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Assert;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;


/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/19 09:52
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobConf.class, Client.class, DtScriptUtil.class,ExceptionUtil.class,
        ConverterUtils.class, FileSystem.class,YarnClientApplication.class,
        Gson.class, StringUtils.class, YarnClient.class, JobResult.class, DtYarnConstants.class,
        ApplicationConstants.class,DtScriptResourceInfo.class})
@PowerMockIgnore("javax.net.ssl.*")
public class DtScriptClientTest {

    @Mock
    Client client;

    private Properties properties = new Properties();

    private DtYarnConfiguration conf = new DtYarnConfiguration();

    private static DtScriptClient dtScriptClient;

    private BaseConfig configMap;

    @BeforeClass
    public static void initPrepare() throws Exception{

        System.setProperty("HADOOP_USER_NAME", "admin");
        PowerMockito.mockStatic(JobConf.class);
        String rootDir = System.getProperty("user.dir");
        String testFileDir;
        if (rootDir.contains("plugins")) {
            testFileDir = rootDir.substring(0, rootDir.indexOf("plugins")) + "testFile";
        } else {
            testFileDir = rootDir+ "testFile";
        }
        String defaultPath = DtYarnConfiguration.DEFAULT_DTSCRIPT_APPMASTERJAR_PATH;
        String[] pathArray = defaultPath.split("/");
        when(JobConf.findContainingJar(ApplicationMaster.class)).thenReturn(testFileDir + "/" + pathArray[pathArray.length - 1]);
    }

    @Test
    public void testDtscriptClient() throws  Exception{
        JobClient jobClient = DataUtil.getJobClient();
        org.apache.hadoop.fs.FileSystem fs = mock(org.apache.hadoop.fs.FileSystem.class);
        when(fs.exists(any())).thenReturn(true);
        PowerMockito.mockStatic(org.apache.hadoop.fs.FileSystem.class);
        when(org.apache.hadoop.fs.FileSystem.get(any())).thenReturn(fs);

        PowerMockito.mockStatic(YarnClient.class);
        YarnClient yc = mock(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yc);

        PowerMockito.mockStatic(YarnClientApplication.class);
        YarnClientApplication application = mock(YarnClientApplication.class);
        when(yc.createApplication()).thenReturn(application);

        GetNewApplicationResponse response = mock(GetNewApplicationResponse.class);
        when(application.getNewApplicationResponse()).thenReturn(response);
        ApplicationIdPBImpl applicationId = mock(ApplicationIdPBImpl.class);
        when(applicationId.toString()).thenReturn("application_1596620462099_1234");
        when(response.getApplicationId()).thenReturn(applicationId);

        FSDataOutputStream outputStream = mock(FSDataOutputStream.class);
        when(org.apache.hadoop.fs.FileSystem.create(any(), any(), any())).thenReturn(outputStream);

        FileStatus fileStatus = mock(FileStatus.class);
        when(fs.getFileStatus(any())).thenReturn(fileStatus);
        when(fileStatus.getLen()).thenReturn(1L);
        when(fileStatus.getModificationTime()).thenReturn(1L);

        Resource resource = mock(Resource.class);
        when(response.getMaximumResourceCapability()).thenReturn(resource);
        when(resource.getMemory()).thenReturn(512);

        ApplicationSubmissionContext context = mock(ApplicationSubmissionContext.class);
        when(application.getApplicationSubmissionContext()).thenReturn(context);

        when(yc.submitApplication(any())).thenReturn(applicationId);
        ApplicationReport report = mock(ApplicationReport.class);
        when(report.getYarnApplicationState()).thenReturn(YarnApplicationState.FINISHED);
        when(report.getFinalApplicationStatus()).thenReturn(FinalApplicationStatus.SUCCEEDED);
        when(yc.getApplicationReport(any())).thenReturn(report);

        properties = PublicUtil.jsonStrToObject(DataUtil.getPluginInfo(), Properties.class);
        dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        JobResult jobResult = dtScriptClient.submitJob(jobClient);
        Assert.assertTrue(jobResult.getData("msg_info").contains("success"));
        JobIdentifier jobIdentifier = new JobIdentifier(jobResult.getData("jobid"), jobClient.getApplicationId(), null);
        RdosTaskStatus status = dtScriptClient.getJobStatus(jobIdentifier);
        Assert.assertTrue(status == RdosTaskStatus.SCHEDULED || status == RdosTaskStatus.FINISHED);

    }

    @Test
    public void testProcessSubmitJobWithType() throws Exception {


        JobClient jobClient = DataUtil.getJobClient();
        org.apache.hadoop.fs.FileSystem fs = mock(org.apache.hadoop.fs.FileSystem.class);
        when(fs.exists(any())).thenReturn(true);
        PowerMockito.mockStatic(org.apache.hadoop.fs.FileSystem.class);
        when(org.apache.hadoop.fs.FileSystem.get(any())).thenReturn(fs);

        PowerMockito.mockStatic(YarnClient.class);
        YarnClient yc = mock(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yc);

        PowerMockito.mockStatic(YarnClientApplication.class);
        YarnClientApplication application = mock(YarnClientApplication.class);
        when(yc.createApplication()).thenReturn(application);

        GetNewApplicationResponse response = mock(GetNewApplicationResponse.class);
        when(application.getNewApplicationResponse()).thenReturn(response);
        ApplicationIdPBImpl applicationId = mock(ApplicationIdPBImpl.class);
        when(applicationId.toString()).thenReturn("application_1596620462099_1234");
        when(response.getApplicationId()).thenReturn(applicationId);

        FSDataOutputStream outputStream = mock(FSDataOutputStream.class);
        when(org.apache.hadoop.fs.FileSystem.create(any(), any(), any())).thenReturn(outputStream);

        FileStatus fileStatus = mock(FileStatus.class);
        when(fs.getFileStatus(any())).thenReturn(fileStatus);
        when(fileStatus.getLen()).thenReturn(1L);
        when(fileStatus.getModificationTime()).thenReturn(1L);

        Resource resource = mock(Resource.class);
        when(response.getMaximumResourceCapability()).thenReturn(resource);
        when(resource.getMemory()).thenReturn(512);

        ApplicationSubmissionContext context = mock(ApplicationSubmissionContext.class);
        when(application.getApplicationSubmissionContext()).thenReturn(context);

        when(yc.submitApplication(any())).thenReturn(applicationId);
        ApplicationReport report = mock(ApplicationReport.class);
        when(report.getYarnApplicationState()).thenReturn(YarnApplicationState.FINISHED);
        when(report.getFinalApplicationStatus()).thenReturn(FinalApplicationStatus.SUCCEEDED);
        when(yc.getApplicationReport(any())).thenReturn(report);

        properties = PublicUtil.jsonStrToObject(DataUtil.getPluginInfo(), Properties.class);
        dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        JobResult jobResult = dtScriptClient.processSubmitJobWithType(jobClient);

        Assert.assertTrue(jobResult.getData("msg_info").contains("success"));
    }


    @Test
    public void testCancelJob() throws Exception {

        JobClient jobClient = DataUtil.getJobClient();
        JobIdentifier jobIdentifier = new JobIdentifier("application_1605237729642_127145","application_1605237729642_127145", "dsafew");
        org.apache.hadoop.fs.FileSystem fs = mock(org.apache.hadoop.fs.FileSystem.class);
        when(fs.exists(any())).thenReturn(true);
        PowerMockito.mockStatic(org.apache.hadoop.fs.FileSystem.class);
        when(org.apache.hadoop.fs.FileSystem.get(any())).thenReturn(fs);

        PowerMockito.mockStatic(YarnClient.class);
        YarnClient yc = mock(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yc);

        PowerMockito.mockStatic(YarnClientApplication.class);
        YarnClientApplication application = mock(YarnClientApplication.class);
        when(yc.createApplication()).thenReturn(application);

        GetNewApplicationResponse response = mock(GetNewApplicationResponse.class);
        when(application.getNewApplicationResponse()).thenReturn(response);
        ApplicationIdPBImpl applicationId = mock(ApplicationIdPBImpl.class);
        when(applicationId.toString()).thenReturn("application_1596620462099_1234");
        when(response.getApplicationId()).thenReturn(applicationId);

        FSDataOutputStream outputStream = mock(FSDataOutputStream.class);
        when(org.apache.hadoop.fs.FileSystem.create(any(), any(), any())).thenReturn(outputStream);

        FileStatus fileStatus = mock(FileStatus.class);
        when(fs.getFileStatus(any())).thenReturn(fileStatus);
        when(fileStatus.getLen()).thenReturn(1L);
        when(fileStatus.getModificationTime()).thenReturn(1L);

        Resource resource = mock(Resource.class);
        when(response.getMaximumResourceCapability()).thenReturn(resource);
        when(resource.getMemory()).thenReturn(512);

        ApplicationSubmissionContext context = mock(ApplicationSubmissionContext.class);
        when(application.getApplicationSubmissionContext()).thenReturn(context);

        when(yc.submitApplication(any())).thenReturn(applicationId);
        ApplicationReport report = mock(ApplicationReport.class);
        when(report.getYarnApplicationState()).thenReturn(YarnApplicationState.FINISHED);
        when(report.getFinalApplicationStatus()).thenReturn(FinalApplicationStatus.SUCCEEDED);
        when(yc.getApplicationReport(any())).thenReturn(report);

        properties = PublicUtil.jsonStrToObject(DataUtil.getPluginInfo(), Properties.class);
        dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        JobResult jobResult = dtScriptClient.cancelJob(jobIdentifier);
        Assert.assertTrue(jobResult.getData("msg_info").contains("success"));
    }

    @Test
    public void testGetJobStatus() throws Exception {

        DtScriptClient dtScriptClient = new DtScriptClient();
        JobIdentifier jobIdentifier = PowerMockito.mock(JobIdentifier.class);

        String jobId = null;
        when(jobIdentifier.getEngineJobId()).thenReturn(jobId);

        mockStatic(StringUtils.class);
        when(StringUtils.isEmpty(jobId)).thenReturn(true);
        Assert.assertNull(dtScriptClient.getJobStatus(jobIdentifier));

        jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        when(jobIdentifier.getEngineJobId()).thenReturn(jobId);

        mockStatic(StringUtils.class);
        when(StringUtils.isEmpty(jobId)).thenReturn(false);

        ApplicationReport report = PowerMockito.mock(ApplicationReport.class);
        when(client.getApplicationReport(jobIdentifier.getEngineJobId())).thenReturn(report);
        when(report.getYarnApplicationState()).thenReturn(YarnApplicationState.RUNNING);
        Assert.assertEquals(RdosTaskStatus.NOTFOUND, dtScriptClient.getJobStatus(jobIdentifier));


    }

    @Test
    public void testGetJobMaster() throws Exception {

        String url = "http://node001:8080";

        DtScriptClient dtScriptClient = new DtScriptClient();
        JobIdentifier jobIdentifier = PowerMockito.mock(JobIdentifier.class);

        try {
            YarnClient yarnClient = PowerMockito.mock(YarnClient.class);
            when(client.getYarnClient()).thenReturn(yarnClient);
            Assert.assertNotNull(yarnClient);

            yarnClient.getNodeReports();

            Field rmClientField = PowerMockito.mock(Field.class);
            when(yarnClient.getClass().getDeclaredField("rmClient")).thenReturn(rmClientField);
            rmClientField.setAccessible(true);

            Object rmClient = PowerMockito.mock(Object.class);
            when(rmClientField.get(yarnClient)).thenReturn(rmClient);

            Field hFild = PowerMockito.mock(Field.class);
            when(rmClient.getClass().getSuperclass().getDeclaredField("h")).thenReturn(hFild);
            hFild.setAccessible(true);

            Object h = PowerMockito.mock(Object.class);
            when(hFild.get(rmClient)).thenReturn(h);

            Field currentProxyField = PowerMockito.mock(Field.class);
            when(h.getClass().getDeclaredField("currentProxy")).thenReturn(currentProxyField);
            currentProxyField.setAccessible(true);

            Object currentProxy = PowerMockito.mock(Object.class);
            when(currentProxyField.get(h)).thenReturn(currentProxy);

            Field proxyInfoField = PowerMockito.mock(Field.class);
            when(currentProxy.getClass().getDeclaredField("proxyInfo")).thenReturn(proxyInfoField);
            proxyInfoField.setAccessible(true);

            String proxyInfoKey = (String) proxyInfoField.get(currentProxy);

            String addr = "node001:8080";
            String key = "yarn.resourcemanager.webapp.address." + proxyInfoKey;

            when(String.format("http://%s", addr)).thenReturn(url);
            Assert.assertEquals(url, dtScriptClient.getJobMaster(jobIdentifier));
        } catch (Exception e) {
            url = "http://node001:8080";
        }

    }

    @Test
    public void testGetMessageByHttp() throws Exception{
        String path = "";
        DtScriptClient dtScriptClient = new DtScriptClient();
        Assert.assertNull(dtScriptClient.getMessageByHttp(path));
    }

    @Test
    public void testSubmitPythonJob() throws Exception{

        JobClient jobClient = DataUtil.getJobClient();
        JobResult jobResult = PowerMockito.mock(JobResult.class);
        DtScriptClient dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        mockStatic(DtScriptUtil.class);
        String[] args = {"dtscrip", "echo", "1"};
        String jobId = "dsfjk234";
        try {
            when(DtScriptUtil.buildPythonArgs(jobClient)).thenReturn(args);
            System.out.println(Arrays.asList(args));

            when(client.submit(args)).thenReturn(jobId);
            mockStatic(JobResult.class);
            when(JobResult.createSuccessResult(jobId)).thenReturn(jobResult);
            PowerMockito.when(dtScriptClient, "submitPythonJob", jobClient).thenReturn(jobResult);

        } catch (Exception e) {
            mockStatic(ExceptionUtil.class);
            when(JobResult.createErrorResult("submit job get unknown error\n"+ ExceptionUtil.getErrorMessage(e)));
        }

        dtScriptClient.submitJob(jobClient);
    }

    @Test
    public void testJudgeSlots() throws Exception{

        JobClient jobClient = DataUtil.getJobClient();

        mockStatic(YarnClient.class);
        YarnClient yc = mock(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yc);
        Assert.assertNotNull(YarnClient.createYarnClient());

        mockStatic(DtScriptResourceInfo.class);
        DtScriptResourceInfo resourceInfo = mock(DtScriptResourceInfo.class);
        DtScriptResourceInfo.DtScriptResourceInfoBuilder();
        mockStatic(DtScriptResourceInfo.DtScriptResourceInfoBuilder.class);
        DtScriptResourceInfo.DtScriptResourceInfoBuilder dt = mock(DtScriptResourceInfo.DtScriptResourceInfoBuilder.class);
        when(DtScriptResourceInfo.DtScriptResourceInfoBuilder()).thenReturn(dt);
        Assert.assertNotNull(dt);
        when(dt.withYarnClient(yc)).thenReturn(dt);
        when(dt.withQueueName(conf.get(DtYarnConfiguration.DT_APP_QUEUE))).thenReturn(dt);
        when(dt.withYarnAccepterTaskNumber(conf.getInt(DtYarnConfiguration.DT_APP_YARN_ACCEPTER_TASK_NUMBER, 1))).thenReturn(dt);
        when(dt.build()).thenReturn(resourceInfo);

        when(resourceInfo.judgeSlots(jobClient)).thenReturn(JudgeResult.ok());

        DtScriptClient dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        JudgeResult judgeResult = dtScriptClient.judgeSlots(jobClient);
        Assert.assertEquals(JudgeResult.ok().available(),judgeResult.available() );
    }

    @Test
    public void testJobLog() throws Exception {
        JobClient jobClient = DataUtil.getJobClient();

        String jobId = jobClient.getTaskId();

        JobIdentifier jobIdentifier = PowerMockito.mock(JobIdentifier.class);
        when(jobIdentifier.getEngineJobId()).thenReturn(jobId);

        Map<String, Object> jobLog = new HashMap<>();
        String diagnostics = "submit job is success";
        String result = "{\"msg_info\":\"submit job is success\"}";

        mockStatic(YarnClient.class);
        mockStatic(ConverterUtils.class);
        YarnClient yc = mock(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yc);
        Assert.assertNotNull(YarnClient.createYarnClient());

        ApplicationReportPBImpl applicationReportPB = mock(ApplicationReportPBImpl.class);

        when(client.getApplicationReport(jobId)).thenReturn(applicationReportPB);
        Assert.assertNotNull(applicationReportPB);
        when(applicationReportPB.getDiagnostics()).thenReturn(diagnostics);
        Assert.assertEquals("submit job is success", diagnostics);
        jobLog.put("msg_info", diagnostics);

        Gson GSON = mock(Gson.class);
        whenNew(Gson.class).withNoArguments().thenReturn(GSON);
        when(GSON.toJson(jobLog, Map.class)).thenReturn(result);

        DtScriptClient dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        String json = dtScriptClient.getJobLog(jobIdentifier);
        Assert.assertNotEquals(result, json);

    }

    @Test
    public void testContainerInfos() throws Exception {
        JobClient jobClient = DataUtil.getJobClient();
        JobIdentifier jobIdentifier = PowerMockito.mock(JobIdentifier.class);
        List<String> results = new ArrayList<>();

        String jobId = jobClient.getTaskId();
        Path remotePath = new Path("/dtInsight/aiworks/staging/application_1605237729642_127145");

        FileStatus[] status =  null;

        when(jobIdentifier.getEngineJobId()).thenReturn(jobId);
        FileSystem fs = mock(FileSystem.class);
        when(client.getFileSystem()).thenReturn(fs);
        when(fs.listStatus(remotePath)).thenReturn(status);
        when(client.getContainerInfos(jobId)).thenReturn(results);

        DtScriptClient dtScriptClient = new DtScriptClient();
        dtScriptClient.init(properties);

        Assert.assertNull(dtScriptClient.getContainerInfos(jobIdentifier));
    }

}
