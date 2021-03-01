package com.dtstack.engine.flink.factory;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.YarnMockUtil;
import com.dtstack.engine.flink.entity.SessionHealthCheckedInfo;
import com.dtstack.engine.flink.util.FlinkUtil;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/11/30
 * Company: www.dtstack.com
 * @author xiuzhu
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SessionClientFactory.class, YarnClient.class,
        File.class, SessionClientFactory.AppStatusMonitor.class,
        FlinkUtil.class, PoolHttpClient.class, CuratorFrameworkFactory.class})
@PowerMockIgnore("javax.net.ssl.*")
public class SessionClientFactoryTest {

    @Mock
    YarnClient yarnClient;

    @Mock
    FlinkClientBuilder flinkClientBuilder;

    @InjectMocks
    SessionClientFactory sessionClientFactory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitYarnClusterClient() throws Exception {
        Configuration flinkConfiguration = new Configuration();
        MemberModifier.field(SessionClientFactory.class, "flinkConfiguration").set(sessionClientFactory, flinkConfiguration);
        MemberModifier.field(SessionClientFactory.class, "flinkClientBuilder").set(sessionClientFactory, flinkClientBuilder);

        List<ApplicationReport> applicationReports = YarnMockUtil.mockApplicationReports();
        when(yarnClient.getApplications(any(), any())).thenReturn(applicationReports);
        when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

        YarnConfiguration yarnConf = new YarnConfiguration();
        when(flinkClientBuilder.getYarnConf()).thenReturn(yarnConf);

        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setSessionStartAuto(true);
        flinkConfig.setQueue("default");
        MemberModifier.field(SessionClientFactory.class, "flinkConfig").set(sessionClientFactory, flinkConfig);
        MemberModifier.field(SessionClientFactory.class, "sessionAppNameSuffix").set(sessionClientFactory, "session");

        PowerMockito.mockStatic(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yarnClient);
        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(yarnClient).init(null);

        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(yarnClient).start();

        ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
        YarnClusterDescriptor descriptor = YarnMockUtil.mockYarnClusterDescriptor(clusterClient);
        PowerMockito.whenNew(YarnClusterDescriptor.class).withArguments(any(Configuration.class), any(YarnConfiguration.class), any(String.class), any(YarnClient.class), any(boolean.class))
                .thenReturn(descriptor);

        ClusterClient<ApplicationId> client = sessionClientFactory.initYarnClusterClient();
        Assert.assertNotNull(client);
    }

    @Test
    public void testCreateYarnSessionClusterDescriptor() throws Exception {
        File file = PowerMockito.mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(anyString()).thenReturn(file);
        when(file.exists()).thenReturn(true);
        File[] clusterKeytabFiles = new File[]{new File("/tmp/hdfs.keytab")};
        when(file.listFiles()).thenReturn(clusterKeytabFiles);
        when(file.getName()).thenReturn("fileName");
        URI uri = new URI("file", null, "/tmp", null);
        when(file.toURI()).thenReturn(uri);

        Configuration flinkConfiguration = new Configuration();
        MemberModifier.field(SessionClientFactory.class, "flinkConfiguration").set(sessionClientFactory, flinkConfiguration);
        MemberModifier.field(SessionClientFactory.class, "flinkClientBuilder").set(sessionClientFactory, flinkClientBuilder);

        YarnConfiguration yarnConf = new YarnConfiguration();
        when(flinkClientBuilder.getYarnConf()).thenReturn(yarnConf);

        String flinkJar = temporaryFolder.newFolder("flinkJar").getAbsolutePath();
        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setFlinkJarPath(flinkJar);
        flinkConfig.setPluginLoadMode("shipfile");
        flinkConfig.setOpenKerberos(true);
        flinkConfig.setPrincipalFile("hive.keytab");

        MemberModifier.field(SessionClientFactory.class, "flinkConfig").set(sessionClientFactory, flinkConfig);

        PowerMockito.mockStatic(YarnClient.class);
        when(YarnClient.createYarnClient()).thenReturn(yarnClient);
        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(yarnClient).init(null);

        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(yarnClient).start();

        ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
        YarnClusterDescriptor mockDescriptor = YarnMockUtil.mockYarnClusterDescriptor(clusterClient);
        PowerMockito.whenNew(YarnClusterDescriptor.class).withArguments(any(Configuration.class), any(YarnConfiguration.class), any(String.class), any(YarnClient.class), any(boolean.class))
                .thenReturn(mockDescriptor);

        AbstractYarnClusterDescriptor descriptor = sessionClientFactory.createYarnSessionClusterDescriptor();
        Assert.assertNotNull(descriptor);
    }

    @Test
    public void testAppStatusMonitorRun() throws Exception {

        ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
        MemberModifier.field(SessionClientFactory.class, "clusterClient").set(sessionClientFactory, clusterClient);

        YarnClient mockYarnClient = PowerMockito.mock(YarnClient.class);
        ApplicationReport report = YarnMockUtil.mockApplicationReports().get(0);
        when(mockYarnClient.getApplicationReport(any(ApplicationId.class))).thenReturn(report);

        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setCheckSubmitJobGraphInterval(10);
        when(flinkClientBuilder.getFlinkConfig()).thenReturn(flinkConfig);
        when(mockYarnClient.isInState(any(Service.STATE.class))).thenReturn(true);

        when(flinkClientBuilder.getYarnClient()).thenReturn(mockYarnClient);

        Configuration flinkConfiguration = new Configuration();
        flinkConfiguration.setString(CheckpointingOptions.CHECKPOINTS_DIRECTORY, "100");
        MemberModifier.field(SessionClientFactory.class, "flinkConfiguration").set(sessionClientFactory, flinkConfiguration);
        MemberModifier.field(SessionClientFactory.class, "isLeader").set(sessionClientFactory, new AtomicBoolean(true));

        PackagedProgram packagedProgram = PowerMockito.mock(PackagedProgram.class);
        PowerMockito.mockStatic(FlinkUtil.class);
//        when(FlinkUtil.buildProgram(any(String.class), any(String.class), any(List.class), any(),
//                any(), any(String[].class), any(SavepointRestoreSettings.class), any(FilesystemManager.class)))
//                .thenReturn(packagedProgram);

        PowerMockito.mockStatic(PoolHttpClient.class);
        when(PoolHttpClient.get(any())).thenReturn("{\"state\":\"FINISHED\"}");

        SessionHealthCheckedInfo sessionHealthCheckedInfo = new SessionHealthCheckedInfo();
        sessionHealthCheckedInfo.reset();

        MemberModifier.field(SessionClientFactory.class, "sessionHealthCheckedInfo").set(sessionClientFactory, sessionHealthCheckedInfo);
        SessionClientFactory.AppStatusMonitor appStatusMonitor = PowerMockito.spy(new SessionClientFactory.AppStatusMonitor(flinkClientBuilder, sessionClientFactory));

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("flink_yarnclient"));
        try {
            RetryUtil.asyncExecuteWithRetry(() -> {appStatusMonitor.run(); return null;},
                    1,
                    0,
                    false,
                    10000L,
                    threadPoolExecutor);
        } catch (Exception e) {
        }
    }

    @Test
    public void testStartAndGetSessionClusterClient() throws Exception {
        Configuration flinkConfiguration = new Configuration();
        MemberModifier.field(SessionClientFactory.class, "flinkConfiguration").set(sessionClientFactory, flinkConfiguration);
        MemberModifier.field(SessionClientFactory.class, "isLeader").set(sessionClientFactory, new AtomicBoolean(true));

        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setSessionStartAuto(true);
        MemberModifier.field(SessionClientFactory.class, "flinkConfig").set(sessionClientFactory, flinkConfig);

        SessionHealthCheckedInfo sessionHealthCheckedInfo = new SessionHealthCheckedInfo();
        sessionHealthCheckedInfo.reset();

        MemberModifier.field(SessionClientFactory.class, "sessionHealthCheckedInfo").set(sessionClientFactory, sessionHealthCheckedInfo);

        try {
            sessionClientFactory.startAndGetSessionClusterClient();
        } catch (Exception e) {
        }
    }

}
