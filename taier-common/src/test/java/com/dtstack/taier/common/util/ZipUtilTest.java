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

package com.dtstack.taier.common.util;

import com.dtstack.taier.common.exception.TaierDefineException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtilTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testUpzipFile() throws Exception {
        File zipFile = temporaryFolder.newFile("normal.zip");
        writeZip(zipFile, new ZipItem("conf/core-site.xml", "content"));
        File targetDir = temporaryFolder.newFolder("normal");

        List<File> files = ZipUtil.upzipFile(zipFile, targetDir.getAbsolutePath());

        Assert.assertEquals(1, files.size());
        File extractedFile = new File(targetDir, "conf/core-site.xml");
        Assert.assertTrue(extractedFile.isFile());
        Assert.assertEquals("content", new String(Files.readAllBytes(extractedFile.toPath()), StandardCharsets.UTF_8));
    }

    @Test
    public void testRejectZipSlipEntry() throws Exception {
        File zipFile = temporaryFolder.newFile("slip.zip");
        writeZip(zipFile, new ZipItem("../evil.txt", "evil"));
        File targetDir = temporaryFolder.newFolder("slip");
        File escapedFile = new File(targetDir.getParentFile(), "evil.txt");

        try {
            ZipUtil.upzipFile(zipFile, targetDir.getAbsolutePath());
            Assert.fail("Zip Slip entry should be rejected");
        } catch (TaierDefineException e) {
            Assert.assertTrue(e.getMessage().contains("outside of target dir"));
        }
        Assert.assertFalse(escapedFile.exists());
    }

    @Test
    public void testRejectTooManyEntries() throws Exception {
        File zipFile = temporaryFolder.newFile("too-many.zip");
        writeZip(zipFile, buildZipItems(1001));
        File targetDir = temporaryFolder.newFolder("too-many");

        try {
            ZipUtil.upzipFile(zipFile, targetDir.getAbsolutePath());
            Assert.fail("Zip with too many entries should be rejected");
        } catch (TaierDefineException e) {
            Assert.assertTrue(e.getMessage().contains("entry count exceeds limit"));
        }
    }

    @Test
    public void testRejectRecursiveZipBomb() throws Exception {
        File zipFile = temporaryFolder.newFile("nested.zip");
        writeNestedZip(zipFile, 4);
        File targetDir = temporaryFolder.newFolder("nested");

        try {
            ZipUtil.upzipFile(zipFile, targetDir.getAbsolutePath());
            Assert.fail("Recursive zip should be rejected");
        } catch (TaierDefineException e) {
            Assert.assertTrue(e.getMessage().contains("recursion depth exceeds limit"));
        }
    }

    private static ZipItem[] buildZipItems(int count) {
        ZipItem[] items = new ZipItem[count];
        for (int i = 0; i < count; i++) {
            items[i] = new ZipItem("file-" + i + ".txt", "a");
        }
        return items;
    }

    private static void writeNestedZip(File zipFile, int depth) throws IOException {
        if (depth == 0) {
            writeZip(zipFile, new ZipItem("leaf.txt", "leaf"));
            return;
        }
        File innerZip = File.createTempFile("inner", ".zip", zipFile.getParentFile());
        writeNestedZip(innerZip, depth - 1);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipOutputStream.putNextEntry(new ZipEntry("inner-" + depth + ".zip"));
            Files.copy(innerZip.toPath(), zipOutputStream);
            zipOutputStream.closeEntry();
        }
        Files.delete(innerZip.toPath());
    }

    private static void writeZip(File zipFile, ZipItem... items) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (ZipItem item : items) {
                zipOutputStream.putNextEntry(new ZipEntry(item.name));
                zipOutputStream.write(item.content.getBytes(StandardCharsets.UTF_8));
                zipOutputStream.closeEntry();
            }
        }
    }

    private static class ZipItem {
        private final String name;
        private final String content;

        private ZipItem(String name, String content) {
            this.name = name;
            this.content = content;
        }
    }
}
