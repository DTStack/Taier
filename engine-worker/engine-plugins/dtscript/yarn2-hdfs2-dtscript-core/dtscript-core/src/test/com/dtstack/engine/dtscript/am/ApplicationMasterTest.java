package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.ServiceAuthorizationManager;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterResponsePBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.security.auth.Subject;
import java.net.InetSocketAddress;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/26 10:24
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppArguments.class, AMRMClientAsync.class,
        NMClientAsync.class, ApplicationMaster.class, ApplicationMessageService.class,
        System.class,
        NetUtils.class, ApplicationContainerListener.class})
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.auth.login.*",
        "org.apache.hadoop.security.UserGroupInformation",
        "org.apache.hadoop.security.JniBasedUnixGroupsMappingWithFallback",
        "org.apache.hadoop.security.GroupMappingServiceProvider"})
public class ApplicationMasterTest {

    private static String testFileDir;

    @Mock
    AMRMClientAsync<AMRMClient.ContainerRequest> amrmAsync;

    @Mock
    NMClientAsync nmAsync;

    @BeforeClass
    public static void beforeClassRun() {
        String rootDir = System.getProperty("user.dir");
        if (rootDir.contains("plugins")) {
            testFileDir = rootDir.substring(0, rootDir.indexOf("plugins")) + "testFile";
        } else {
            testFileDir = rootDir+ "testFile";
        }
    }

    @Test
    public void testApplicationMaster() throws Exception {
        System.setProperty("HADOOP_USER_NAME", "admin");
        Map<String, String> systemEnvs = new HashMap<>();
        systemEnvs.putAll(System.getenv());
        systemEnvs.put(ApplicationConstants.Environment.CONTAINER_ID.toString(), "container_e19_1533108188813_12125_01_000002");
        systemEnvs.put(DtYarnConstants.Environment.XLEARNING_JOB_CONF_LOCATION.toString(), testFileDir);
        systemEnvs.put("hadoop.job.ugi", "admin,admin");
        systemEnvs.put(DtYarnConstants.Environment.APP_JAR_LOCATION.toString(), testFileDir + "/" + "dtscript-core-1.0.0.jar");

        DtYarnConfiguration configuration = new DtYarnConfiguration();
        configuration.set("hadoop.job.ugi", "admin,admin");
        DtYarnConfiguration mockConfiguration = spy(configuration);
        when(mockConfiguration.get(DtYarnConfiguration.DTSCRIPT_APPMASTERJAR_PATH, DtYarnConfiguration.DEFAULT_DTSCRIPT_APPMASTERJAR_PATH))
                .thenReturn(testFileDir + "/" + "dtscript-core-1.0.0.jar");

        PowerMockito.whenNew(DtYarnConfiguration.class).withAnyArguments().thenReturn(mockConfiguration);

        mockStatic(System.class);
        when(System.getenv()).thenReturn(systemEnvs);

        mockStatic(AMRMClientAsync.class);
        mockStatic(NMClientAsync.class);

        when(AMRMClientAsync.createAMRMClientAsync(anyInt(),
                Mockito.any(RMCallbackHandler.class))).thenReturn(amrmAsync);

        when(NMClientAsync.createNMClientAsync(Mockito.any(NMCallbackHandler.class))).thenReturn(nmAsync);

        RPC.Builder builder = Mockito.mock(RPC.Builder.class);
        PowerMockito.whenNew(RPC.Builder.class).withAnyArguments().thenReturn(builder);
        Mockito.when(builder.setProtocol(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.setInstance(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.setBindAddress(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.setPort(Mockito.anyInt())).thenReturn(builder);

        RPC.Server server = Mockito.mock(RPC.Server.class);
        Mockito.when(server.getPort()).thenReturn(8080);
        ServiceAuthorizationManager manager = Mockito.mock(ServiceAuthorizationManager.class);
        Mockito.when(server.getServiceAuthorizationManager()).thenReturn(manager);

        Mockito.when(builder.build()).thenReturn(server);

        InetSocketAddress address = new InetSocketAddress(0);
        PowerMockito.mockStatic(NetUtils.class);
        Mockito.when(NetUtils.getConnectAddress(server)).thenReturn(address);

        RegisterApplicationMasterResponsePBImpl response = Mockito.mock(RegisterApplicationMasterResponsePBImpl.class);
        Mockito.when(amrmAsync.registerApplicationMaster(Mockito.any(),
                Mockito.anyInt(), Mockito.any())).thenReturn(response);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getVirtualCores()).thenReturn(1);
        Mockito.when(resource.getMemory()).thenReturn(512);
        Mockito.when(response.getMaximumResourceCapability()).thenReturn(resource);

        RMCallbackHandler handler = Mockito.mock(RMCallbackHandler.class);
        Mockito.when(handler.getAllocatedWorkerContainerNumber()).thenReturn(1);
        PowerMockito.whenNew(RMCallbackHandler.class).withAnyArguments().thenReturn(handler);

        ApplicationMaster.main(null);






    }

}
