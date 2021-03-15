package com.dtstack.engine.flink.util;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

/**
 * Date: 2021/03/12
 * Company: www.dtstack.com
 *
 * @author tudou
 */
public class FlinkUtilTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetLastModifiedTime() throws Exception {
        Path dst = new Path("hdfs://ns/110_flinkplugin/syncplugin/flinkx-1.10_release.jar");
        FileSystem fs = Mockito.mock(FileSystem.class);
        File localSrcFile = new File("/data/110_flinkplugin/syncplugin/flinkx-1.10_release.jar");
        FileStatus fileStatus = new FileStatus(1024,
                false,
                3,
                128*1024*1024,
                1615542888048L,
                1615542888048L,
                new FsPermission((short) 0755),
                "root",
                "root",
                null,
                dst
                );
        Mockito.when(fs.listStatus(dst)).thenReturn(new FileStatus[]{fileStatus});
        long result = FlinkUtil.getLastModifiedTime(dst, fs, localSrcFile);
        Assert.assertEquals(1615542888048L, result);
    }
}
