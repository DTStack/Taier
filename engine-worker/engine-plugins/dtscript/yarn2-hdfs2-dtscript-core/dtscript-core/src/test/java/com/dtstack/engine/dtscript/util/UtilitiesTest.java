package com.dtstack.engine.dtscript.util;

import com.sun.tools.javac.code.Attribute;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;


import java.util.List;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/28 11:14
 */
public class UtilitiesTest {

    @Test
    public void testListStatusRecursively() throws Exception{

        PowerMockito.mockStatic(Utilities.class);
        Path path = PowerMockito.mock(Path.class);
        FileSystem fs = PowerMockito.mock(FileSystem.class);
        List<FileStatus> fileStatuses = PowerMockito.mock(List.class);
//        FileStatus[] fileStatuses1 = PowerMockito.mock(Attribute.Array.Class);
//
//        PowerMockito.when(fs.listStatus(path)).thenReturn(fileStatuses);

    }
}
