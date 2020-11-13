package com.dtstack.engine.flink;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.flink.factory.AbstractClientFactory;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.util.FileUtil;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.dtstack.engine.flink.util.HadoopConf;
import com.google.common.collect.Maps;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HistoryServerOptions;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author mowen
 * @ProjectName engine-all
 * @ClassName FlinkClientTest.java
 * @Description
 * @createTime 2020年09月23日 19:57:00
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FlinkClientBuilder.class,
        FlinkClusterClientManager.class, PoolHttpClient.class,
        FileSystem.class, FileUtil.class, PublicUtil.class,
        FlinkConfUtil.class, FlinkUtil.class, PerJobClientFactory.class,
        AbstractClientFactory.class, FlinkClient.class})
@PowerMockIgnore("javax.net.ssl.*")
public class FlinkClientTest {

    @Mock
    FlinkClientBuilder flinkClientBuilder;

    @Mock
    YarnClient yarnClient;

    @Mock
    FlinkClusterClientManager flinkClusterClientManager;


    @InjectMocks
    private FlinkClient flinkClient;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        File file = PowerMockito.mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(anyString()).thenReturn(file);
        when(file.exists()).thenReturn(true);
        when(file.isFile()).thenReturn(true);
        when(file.getParentFile()).thenReturn(file);
        when(file.getAbsolutePath()).thenReturn("hdfs://user/tmp/tmpJar.jar");

//        PowerMockito.mockStatic(SFTPHandler.class);
//        SFTPHandler sftpHandler = PowerMockito.mock(SFTPHandler.class);
//        when(SFTPHandler.getInstance(any())).thenReturn(sftpHandler);
//        when(sftpHandler.loadFromSftp(any(), any(), any())).thenReturn("test/path");
//        when(sftpHandler.downloadDir(any(), any())).thenReturn(1);

        FileSystem fs = PowerMockito.mock(FileSystem.class);
        when(fs.exists(any())).thenReturn(true);
        when(fs.open(any())).thenReturn(PowerMockito.mock(FSDataInputStream.class));
        PowerMockito.mockStatic(FileSystem.class);
        when(FileSystem.get(any(), any())).thenReturn(fs);

    }

    @Test
    public void testBeforeSubmitFunc() throws Exception {

        String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
        JobClient jobClient = YarnMockUtil.mockJobClient("session", absolutePath);

        FlinkConfig flinkConfig = new FlinkConfig();
        Map<String, String> map = new HashMap<>();
        map.put("test", "test");
//        flinkConfig.setSftpConf();
        MemberModifier.field(FlinkClient.class, "flinkConfig")
                .set(flinkClient, flinkConfig);
        MemberModifier.field(FlinkClient.class, "cacheFile")
                .set(flinkClient, Maps.newConcurrentMap());
        MemberModifier.field(FlinkClient.class, "hadoopConf")
                .set(flinkClient, new HadoopConf());

        flinkClient.beforeSubmitFunc(jobClient);
    }

    @Test
    public void testCancelJob() throws Exception {
        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";

        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);
        ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
        when(flinkClusterClientManager.getClusterClient(null)).thenReturn(clusterClient);
        JobResult jobResult = flinkClient.cancelJob(jobIdentifier);
        Assert.assertNotNull(jobResult);
    }

    @Test
    public void testGetJobLog() throws Exception {
        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

        PowerMockito.mockStatic(PoolHttpClient.class);
        when(PoolHttpClient.get(any())).thenReturn("{\"app\":{\"amContainerLogs\":\"http://dtstack01:8088/ws/v1/cluster/apps/application_9527\"}}");

        ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
        when(yarnClient.getApplicationReport(any())).thenReturn(report);
        when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

        ClusterClient currClient = YarnMockUtil.mockClusterClient();
        when(flinkClusterClientManager.getClusterClient(any())).thenReturn(currClient);

        String jobLog = flinkClient.getJobLog(jobIdentifier);
        Assert.assertNotNull(jobLog);

        ApplicationReportPBImpl reportFinish = YarnMockUtil.mockApplicationReport(YarnApplicationState.FINISHED);
        when(yarnClient.getApplicationReport(any())).thenReturn(reportFinish);
        when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

        Configuration flinkConfig = new Configuration();
        flinkConfig.setString(HistoryServerOptions.HISTORY_SERVER_WEB_ADDRESS, "dtstack01");
        flinkConfig.setString(String.valueOf(HistoryServerOptions.HISTORY_SERVER_WEB_PORT), "9527");
        when(flinkClientBuilder.getFlinkConfiguration()).thenReturn(flinkConfig);

        String jobLogFinished = flinkClient.getJobLog(jobIdentifier);
        Assert.assertNotNull(jobLogFinished);
    }

    @Test
    public void testGetCheckpoints() throws Exception {
        JobIdentifier jobIdentifier =
                new JobIdentifier("engineId","application_1593762151957_0080", "taskId");

        ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
        when(yarnClient.getApplicationReport(any())).thenReturn(report);
        when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

        ClusterClient currClient = YarnMockUtil.mockClusterClient();
        when(flinkClusterClientManager.getClusterClient(any())).thenReturn(currClient);

        PowerMockito.mockStatic(PoolHttpClient.class);
        when(PoolHttpClient.get(any())).thenReturn("{\"app\":{\"amContainerLogs\":\"http://dtstack01:8088/ws/v1/cluster/apps/application_9527\"}}");

        String checkpoints = flinkClient.getCheckpoints(jobIdentifier);
        Assert.assertNotNull(checkpoints);

    }


    @Test
    public void testGetJobStatus() throws Exception {
        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

        ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
        when(yarnClient.getApplicationReport(any())).thenReturn(report);
        when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

        RdosTaskStatus jobStatus = flinkClient.getJobStatus(jobIdentifier);
        Assert.assertNotNull(jobStatus);

        PowerMockito.mockStatic(PoolHttpClient.class);
        when(PoolHttpClient.get(any())).thenReturn("{\"state\":\"RUNNING\"}");

        ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
        when(flinkClusterClientManager.getClusterClient(null)).thenReturn(clusterClient);
        jobIdentifier.setApplicationId(null);
        RdosTaskStatus jobStatus2 = flinkClient.getJobStatus(jobIdentifier);
        Assert.assertNotNull(jobStatus2);
    }

}
