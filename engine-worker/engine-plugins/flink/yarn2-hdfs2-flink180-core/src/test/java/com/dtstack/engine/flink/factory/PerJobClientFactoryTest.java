package com.dtstack.engine.flink.factory;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.flink.FlinkClientBuilder;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.YarnMockUtil;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
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
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/19/09
 * Company: www.dtstack.com
 * @author xiuzhu
 */


@RunWith(PowerMockRunner.class)
@PrepareForTest({PerJobClientFactory.class, YarnClient.class, File.class})
@PowerMockIgnore("javax.net.ssl.*")
public class PerJobClientFactoryTest {

    @Mock
    YarnClient yarnClient;

    @Mock
    FlinkClientBuilder flinkClientBuilder;

    @InjectMocks
    PerJobClientFactory perJobClientFactory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetClusterClient() throws Exception {
        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

        Cache<String, ClusterClient> perJobClientCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
        MemberModifier.field(PerJobClientFactory.class, "perJobClientCache").set(perJobClientFactory, perJobClientCache);

        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        when(flinkClientBuilder.getYarnConf()).thenReturn(yarnConfiguration);

        try {
            perJobClientFactory.getClusterClient(jobIdentifier);
        } catch (Exception e) {}
    }

    @Test
    public void testCreatePerJobClusterDescriptor() throws Exception {
        String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
        JobClient jobClient = YarnMockUtil.mockJobClient("perJob", absolutePath);
        jobClient.setJobName("test");

        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setQueue("default");
        flinkConfig.setFlinkJarPath(absolutePath);
        MemberModifier.field(PerJobClientFactory.class, "flinkConfig").set(perJobClientFactory, flinkConfig);

        Configuration configuration = new Configuration();
        MemberModifier.field(PerJobClientFactory.class, "flinkConfiguration").set(perJobClientFactory, configuration);

        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        when(flinkClientBuilder.getYarnConf()).thenReturn(yarnConfiguration);
        MemberModifier.field(PerJobClientFactory.class, "flinkClientBuilder").set(perJobClientFactory, flinkClientBuilder);

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

        File file = PowerMockito.mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(anyString()).thenReturn(file);
        when(file.exists()).thenReturn(true);
        File[] files = new File[]{new File("/tmp/test.jar")};
        when(file.listFiles()).thenReturn(files);
        when(file.getAbsolutePath()).thenReturn("/tmp/test.keytab");
        when(file.getName()).thenReturn("fileName");
        URI uri = new URI("file", null, "/tmp", null);
        when(file.toURI()).thenReturn(uri);

        AbstractYarnClusterDescriptor perJobClusterDescriptor = perJobClientFactory.createPerJobClusterDescriptor(jobClient);
        Assert.assertNotNull(perJobClusterDescriptor);
    }

    @Test
    public void testDeleteTaskIfExist() throws Exception {
        String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
        JobClient jobClient = YarnMockUtil.mockJobClient("perJob", absolutePath);

        FlinkConfig flinkConfig = new FlinkConfig();
        flinkConfig.setQueue("default");
        MemberModifier.field(PerJobClientFactory.class, "flinkConfig").set(perJobClientFactory, flinkConfig);

        List<ApplicationReport> applicationReports = YarnMockUtil.mockApplicationReports();
        when(yarnClient.getApplications((EnumSet<YarnApplicationState>) any(EnumSet.class))).thenReturn(applicationReports);
        when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);
        MemberModifier.field(PerJobClientFactory.class, "flinkClientBuilder").set(perJobClientFactory, flinkClientBuilder);

        PowerMockito.doAnswer(new Answer() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "test";
            }
        }).when(yarnClient).killApplication(null);

        perJobClientFactory.deleteTaskIfExist(jobClient);

    }
}
