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

package com.dtstack.taier.develop.controller.console;

import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.dto.Resource;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class UploadControllerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetResourcesFromFilesSaveFileUnderUploadDir() throws Exception {
        File uploadDir = temporaryFolder.newFolder("file-uploads");
        ReflectionTestUtils.setField(UploadController.class, "uploadsDir", uploadDir.getAbsolutePath());
        UploadController uploadController = new UploadController();
        MultipartFile multipartFile = new MockMultipartFile("fileName", "config.json",
                "application/json", "{\"k\":\"v\"}".getBytes(StandardCharsets.UTF_8));

        List<Resource> resources = ReflectionTestUtils.invokeMethod(uploadController,
                "getResourcesFromFiles", Lists.newArrayList(multipartFile));

        Assert.assertNotNull(resources);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.get(0);
        Assert.assertEquals("config.json", resource.getFileName());
        Assert.assertEquals("fileName", resource.getKey());
        File savedFile = new File(resource.getUploadedFileName());
        Assert.assertTrue(savedFile.exists());
        Assert.assertEquals(uploadDir.getCanonicalPath(), savedFile.getParentFile().getCanonicalPath());
        Assert.assertEquals("{\"k\":\"v\"}", new String(Files.readAllBytes(savedFile.toPath()), StandardCharsets.UTF_8));
    }

    @Test(expected = TaierDefineException.class)
    public void testGetResourcesFromFilesRejectPathTraversalFileName() throws Exception {
        File uploadDir = temporaryFolder.newFolder("file-uploads");
        ReflectionTestUtils.setField(UploadController.class, "uploadsDir", uploadDir.getAbsolutePath());
        UploadController uploadController = new UploadController();
        MultipartFile multipartFile = new MockMultipartFile("fileName", "../../../../tmp/x.txt",
                "application/octet-stream", "x".getBytes(StandardCharsets.UTF_8));

        ReflectionTestUtils.invokeMethod(uploadController,
                "getResourcesFromFiles", Lists.newArrayList(multipartFile));
    }
}
