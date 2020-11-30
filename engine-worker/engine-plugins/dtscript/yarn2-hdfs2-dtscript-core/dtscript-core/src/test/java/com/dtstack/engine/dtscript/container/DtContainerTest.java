package com.dtstack.engine.dtscript.container;

import com.dtstack.engine.dtscript.DtYarnConfiguration;
import com.dtstack.engine.dtscript.am.ApplicationContainerListener;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.common.SecurityUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/26 11:24
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DtContainer.class, DtYarnConfiguration.class,RPC.class, FileSystem.class})
@PowerMockIgnore({"javax.net.ssl.*","org.apache.hadoop.security.UserGroupInformation"})
public class DtContainerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @BeforeClass
    public static void initPrepare() throws Exception {
        String rootDir = System.getProperty("user.dir");
        String testFileDir;
        if (rootDir.contains("plugins")) {
            testFileDir = rootDir.substring(0, rootDir.indexOf("plugins")) + "testFile";
        } else {
            testFileDir = rootDir+ "testFile";
        }

        System.setProperty("HADOOP_USER_NAME", "admin");
        System.setProperty("hadoop.job.ugi", "admin,admin");
    }

    @Test
    public void testStartContainer() throws Exception {

        Map<String, String> systemEnvs = new HashMap<>();
        systemEnvs.putAll(System.getenv());
        String containerId = "container_e19_1533108188813_12125_01_000002";

        String workDir = temporaryFolder.newFolder("worker").getAbsolutePath();
        temporaryFolder.newFile("worker/dterror.log");
        temporaryFolder.newFile("worker/dtstdout.log");

        systemEnvs.put(ApplicationConstants.Environment.CONTAINER_ID.toString(),
                containerId);
        systemEnvs.put(ApplicationConstants.Environment.LOG_DIRS.name(), workDir);
        PowerMockito.mockStatic(System.class);
        Mockito.when(System.getenv()).thenReturn(systemEnvs);
        Mockito.when(System.getenv(ApplicationConstants.Environment.CONTAINER_ID.name())).thenReturn(containerId);
        Mockito.when(System.getenv(DtYarnConstants.Environment.APPMASTER_HOST.toString())).thenReturn("127.0.0.1");
        Mockito.when(System.getenv(DtYarnConstants.Environment.APPMASTER_PORT.toString())).thenReturn("8080");

        DtYarnConfiguration yarnConfiguration = new DtYarnConfiguration();
        DtYarnConfiguration mockYarnConfiguration = Mockito.spy(yarnConfiguration);
        Mockito.when(mockYarnConfiguration.get("hadoop.job.ugi")).thenReturn("admin");
        PowerMockito.whenNew(DtYarnConfiguration.class).withAnyArguments().thenReturn(mockYarnConfiguration);

        ApplicationContainerListener protocol = Mockito.mock(ApplicationContainerListener.class);
        PowerMockito.mockStatic(RPC.class);
        Mockito.when(RPC.getProxy(Mockito.any(), Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(protocol);
        LocalRemotePath path = new LocalRemotePath();
        path.setLocalLocation("127.0.0.1");
        path.setDfsLocation("hdfs://ns1");
        LocalRemotePath[] paths = new LocalRemotePath[1];
        paths[0] = path;
        Mockito.when(protocol.getOutputLocation()).thenReturn(paths);


        FSDataOutputStream stream = Mockito.mock(FSDataOutputStream.class);
        LocalFileSystem fileSystem = PowerMockito.mock(LocalFileSystem.class);
        PowerMockito.mockStatic(FileSystem.class);
        PowerMockito.when(FileSystem.getLocal(Mockito.any(Configuration.class))).thenReturn(fileSystem);
        PowerMockito.when(FileSystem.create(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(stream);

        DtContainer.main(null);
    }


}
