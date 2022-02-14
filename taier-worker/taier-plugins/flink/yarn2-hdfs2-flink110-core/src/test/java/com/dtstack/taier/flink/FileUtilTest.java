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

package com.dtstack.taier.flink;

import com.dtstack.taier.flink.util.FileUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedInputStream;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author mowen
 * @ProjectName engine-all
 * @ClassName FileUtilTest.java
 * @Description TODO
 * @createTime 2020年09月23日 19:58:00
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FileUtil.class, URL.class, BufferedInputStream.class, FileSystem.class, IOUtils.class})
@PowerMockIgnore("javax.net.ssl.*")
public class FileUtilTest {

//    @Rule
//    public TemporaryFolder temporaryFolder = new TemporaryFolder();
//
//    @Test
//    public void testDownLoadFileFromHttp() throws Exception {
//        String absolutePath = temporaryFolder.newFolder("tmp").getAbsolutePath();
//
//        String urlStr = "http://HiLany.github.io/tmp/file";
//        String dstFileName = absolutePath + "/tmpFile";
//
//        HttpURLConnection httpUrlConnection = PowerMockito.mock(HttpURLConnection.class);
//        when(httpUrlConnection.getInputStream()).thenReturn(PowerMockito.mock(InputStream.class));
//        URL url = PowerMockito.mock(URL.class);
//        when(url.openConnection()).thenReturn(httpUrlConnection);
//        PowerMockito.whenNew(URL.class).withArguments(anyObject()).thenReturn(url);
//
//        PowerMockito.doAnswer((Answer) invocationOnMock -> null).when(httpUrlConnection).connect();
//
//        BufferedInputStream inputStream = PowerMockito.mock(BufferedInputStream.class);
//        when(inputStream.read(any())).thenReturn(-1);
//        PowerMockito.whenNew(BufferedInputStream.class).withArguments(any(InputStream.class))
//                .thenReturn(inputStream);
//
//        boolean downResult = FileUtil.downLoadFileFromHttp(urlStr, dstFileName);
//        Assert.assertTrue(downResult);
//    }
//
//    @Test
//    public void testDownLoadFileFromHdfs() throws Exception {
//        String absolutePath = temporaryFolder.newFolder("tmp").getAbsolutePath();
//
//        String uriStr = "hdfs://tmp/tmp.jar";
//        String dstFileName = absolutePath + "/tmpFile";
//
//        FSDataInputStream is = PowerMockito.mock(FSDataInputStream.class);
//        FileSystem fs = PowerMockito.mock(FileSystem.class);
//        when(fs.exists(any(Path.class))).thenReturn(true);
//        when(fs.open(any(Path.class))).thenReturn(is);
//        PowerMockito.mockStatic(FileSystem.class);
//        when(FileSystem.get(any(URI.class), any(Configuration.class))).thenReturn(fs);
//
//        PowerMockito.mockStatic(IOUtils.class);
//        PowerMockito.doAnswer((Answer) invocationOnMock -> 0).when(is).read(any(byte[].class));
//
//        boolean downResult = FileUtil.downLoadFileFromHdfs(uriStr, dstFileName, new Configuration());
//        Assert.assertTrue(downResult);
//    }

}
