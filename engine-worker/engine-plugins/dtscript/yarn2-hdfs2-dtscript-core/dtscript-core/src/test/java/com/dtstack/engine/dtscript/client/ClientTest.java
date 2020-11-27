package com.dtstack.engine.dtscript.client;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.util.RetryUtil;
import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.google.gson.Gson;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/25 18:04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobConf.class, ClientTest.class, RetryUtil.class,
        ConverterUtils.class,
        Gson.class, UserGroupInformation.class, YarnClient.class,
        FileSystem.class, DtYarnConstants.class, ApplicationConstants.class})
@PowerMockIgnore("javax.net.ssl.*")
public class ClientTest {

    private static DtYarnConfiguration conf = new DtYarnConfiguration();

    private YarnConfiguration yarnConf = new YarnConfiguration(this.conf);

    private BaseConfig baseConfig = new BaseConfig();

    private static Properties properties;

    @InjectMocks
    private ClientTest client;

    @BeforeClass
    public static void initPrepare() {
        String rootDir = System.getProperty("user.dir");
        String testFileDir;
        if (rootDir.contains("plugins")) {
            testFileDir = rootDir.substring(0, rootDir.indexOf("plugins")) + "testFile";
        } else {
            testFileDir = rootDir+ "testFile";
        }

        System.setProperty("HADOOP_USER_NAME", "admin");
        System.setProperty("hadoop.job.ugi", "admin,admin");
        Map<String, String> systemEnvs = new HashMap<>();
        systemEnvs.putAll(System.getenv());

        conf.set("ipc.client.fallback-to-simple-auth-allowed", "true");

        String containerId = "container_e19_1533108188813_12125_01_000002";
        systemEnvs.put(ApplicationConstants.Environment.CONTAINER_ID.toString(),
                containerId);
        systemEnvs.put(ApplicationConstants.Environment.LOG_DIRS.name(), testFileDir);
        mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(systemEnvs);
        Mockito.when(System.getenv(ApplicationConstants.Environment.CONTAINER_ID.name())).thenReturn(containerId);
        Mockito.when(System.getenv(DtYarnConstants.Environment.APPMASTER_HOST.toString())).thenReturn("127.0.0.1");
        Mockito.when(System.getenv(DtYarnConstants.Environment.APPMASTER_PORT.toString())).thenReturn("8080");

    }

    @Test
    public void testInit() throws Exception {
        DtYarnConfiguration yarnConfiguration = new DtYarnConfiguration();
        DtYarnConfiguration mockYarnConfiguration = spy(yarnConfiguration);
        String appSubmitterUserName = System.getenv(ApplicationConstants.Environment.USER.name());

        ClientArguments clientArguments = mock(ClientArguments.class);
        mockStatic(UserGroupInformation.class);
        UserGroupInformation userGroupInformation = mock(UserGroupInformation.class);
        when(UserGroupInformation.createRemoteUser(appSubmitterUserName)).thenReturn(userGroupInformation);

        mock(ThreadPoolExecutor.class);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,1,1000L,
                TimeUnit.MINUTES, new LinkedBlockingDeque<>());

        whenNew(ThreadPoolExecutor.class).withAnyArguments().thenReturn(threadPoolExecutor);


        Client client = mock(Client.class);


        Assert.assertNotNull(client.init(clientArguments));

    }



    @Test
    public void testGetYarnClient() throws Exception {
        mockStatic(YarnClient.class);
        mock(ThreadPoolExecutor.class);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,1,1000L,
                TimeUnit.MINUTES, new LinkedBlockingDeque<>());
        YarnClient yc = mock(YarnClient.class);

        when(YarnClient.createYarnClient()).thenReturn(yc);
        yc.init(conf);
        yc.start();

        Assert.assertNotNull(yc);
        mockStatic(RetryUtil.class);
        RetryUtil.asyncExecuteWithRetry(() -> yc.getAllQueues(),
                1,0,false, 30000L, threadPoolExecutor);


        Client client = new Client(conf, baseConfig);
        Assert.assertEquals(yc,client.getYarnClient());

    }

}
