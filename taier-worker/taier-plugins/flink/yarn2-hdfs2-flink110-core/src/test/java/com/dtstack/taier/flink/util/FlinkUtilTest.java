/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.flink.util;

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
