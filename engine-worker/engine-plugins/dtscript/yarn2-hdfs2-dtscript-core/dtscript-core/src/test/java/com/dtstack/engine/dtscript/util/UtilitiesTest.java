package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.dtscript.am.AppArguments;
import com.dtstack.engine.dtscript.am.ApplicationContainerListener;
import com.dtstack.engine.dtscript.am.ApplicationMaster;
import com.dtstack.engine.dtscript.am.ApplicationMessageService;
import com.sun.tools.javac.code.Attribute;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/28 11:14
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Utilities.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class UtilitiesTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testListStatusRecursively() throws Exception{

        Path path = PowerMockito.mock(Path.class);
        FileSystem fs = PowerMockito.mock(FileSystem.class);
        FileStatus fileStatus = PowerMockito.mock(FileStatus.class);
        List<FileStatus> fileStatuses = new ArrayList<>(3);
        fileStatuses.add(fileStatus);
        fileStatuses.add(fileStatus);

        PowerMockito.mockStatic(Utilities.class);

//        PowerMockito.when(fs.listStatus(path)).thenReturn(fileStatuses1);

        Utilities.listStatusRecursively(path, fs, fileStatuses);

    }

    @Test
    public void testSetPathExecuttableRecursively() throws Exception{

        String path = temporaryFolder.newFolder("path").getAbsolutePath();
        temporaryFolder.newFile("path/file1.txt");
        temporaryFolder.newFile("path/file2.txt");
        temporaryFolder.newFile("path/file3.txt");
        File file = PowerMockito.mock(File.class);
        PowerMockito.when(file.exists()).thenReturn(true);
        PowerMockito.when(file.setExecutable(Mockito.anyBoolean())).thenReturn(true);
        PowerMockito.when(file.isDirectory()).thenReturn(true);
        PowerMockito.whenNew(File.class).withArguments(path).thenReturn(file);

        Utilities.setPathExecutableRecursively(path);
    }

    @Test
    public void testConverStatusToPath() throws Exception {
        String absolutePath = temporaryFolder.newFolder("path").getAbsolutePath();

        FileStatus fileStatus = new FileStatus();
        List<FileStatus> fileStatuses = new ArrayList<>(2);
        fileStatuses.add(fileStatus);
        fileStatuses.add(fileStatus);

        Utilities.convertStatusToPath(fileStatuses);
    }

    @Test
    public void testMkdirs() throws Exception {

    }

}
