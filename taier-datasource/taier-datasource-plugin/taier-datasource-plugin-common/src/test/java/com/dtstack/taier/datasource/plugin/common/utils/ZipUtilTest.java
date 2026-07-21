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

package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ZipUtilTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testUnzipFile() throws Exception {
        File zipFile = temporaryFolder.newFile("normal.zip");
        writeZip(zipFile, "conf/core-site.xml", "content");
        File targetDir = temporaryFolder.newFolder("normal");

        List<File> files = ZipUtil.unzipFile(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());

        Assert.assertEquals(1, files.size());
        File extractedFile = new File(targetDir, "conf/core-site.xml");
        Assert.assertTrue(extractedFile.isFile());
        Assert.assertEquals("content", new String(Files.readAllBytes(extractedFile.toPath()), StandardCharsets.UTF_8));
    }

    @Test
    public void testRejectZipSlipEntry() throws Exception {
        File zipFile = temporaryFolder.newFile("slip.zip");
        writeZip(zipFile, "../evil.txt", "evil");
        File targetDir = temporaryFolder.newFolder("slip");
        File escapedFile = new File(targetDir.getParentFile(), "evil.txt");

        try {
            ZipUtil.unzipFile(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());
            Assert.fail("Zip Slip entry should be rejected");
        } catch (SourceException e) {
            Assert.assertTrue(e.getMessage().contains("outside of target dir"));
        }
        Assert.assertFalse(escapedFile.exists());
    }

    @Test
    public void testRejectAbsolutePathZipEntry() throws Exception {
        File targetDir = temporaryFolder.newFolder("absolute");
        File escapedFile = temporaryFolder.newFile("absolute-evil.txt");
        Files.delete(escapedFile.toPath());
        File zipFile = temporaryFolder.newFile("absolute.zip");
        writeZip(zipFile, escapedFile.getAbsolutePath(), "evil");

        try {
            ZipUtil.unzipFile(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());
            Assert.fail("Absolute path zip entry should be rejected");
        } catch (SourceException e) {
            Assert.assertTrue(e.getMessage().contains("outside of target dir"));
        }
        Assert.assertFalse(escapedFile.exists());
    }

    private static void writeZip(File zipFile, String entryName, String content) throws Exception {
        try (org.apache.tools.zip.ZipOutputStream zipOutputStream =
                     new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(zipFile))) {
            zipOutputStream.putNextEntry(new org.apache.tools.zip.ZipEntry(entryName));
            zipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }
    }
}
