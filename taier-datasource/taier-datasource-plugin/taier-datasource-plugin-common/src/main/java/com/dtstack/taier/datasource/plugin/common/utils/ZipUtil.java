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
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:37 2020/8/27
 * @Description：压缩工具
 */
@Slf4j
public class ZipUtil {
    /**
     * 字节数
     */
    private static byte[] byte_simple = new byte[1024];

    private static final int MAX_ZIP_ENTRY_COUNT = 1000;
    private static final long MAX_ZIP_TOTAL_UNCOMPRESSED_SIZE = 100L * 1024 * 1024;
    private static final long MAX_ZIP_ENTRY_UNCOMPRESSED_SIZE = 50L * 1024 * 1024;
    private static final long MAX_ZIP_COMPRESSION_RATIO = 100L;

    /**
     * 压缩文件或路径
     *
     * @param zipLocation    压缩的目的地址
     * @param sourceLocation 压缩的源文件
     */
    public static void zipFile(String zipLocation, String sourceLocation) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(zipLocation))) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream)) {
                if (zipLocation.endsWith(".zip") || zipLocation.endsWith(".ZIP")) {
                    zipOut.setEncoding("GBK");
                    handlerFile(zipLocation, zipOut, sourceLocation, "");
                } else {
                    throw new SourceException(String.format("The target file %s is not a file ending in ZIP", zipLocation));
                }
            }
        } catch (FileNotFoundException e) {
            throw new SourceException(String.format("file not found,%s", e.getMessage()), e);
        } catch (IOException e) {
            throw new SourceException(String.format("Target file compression abnormal:%s", e.getMessage()), e);
        }
    }

    /**
     * 对.zip文件进行解压缩
     *
     * @param zipLocation    解压缩文件地址
     * @param targetLocation 压缩的目标地址，如：D:\\测试 或 /mnt/d/测试
     * @return
     */
    public static List<File> unzipFile(String zipLocation, String targetLocation) {
        List<File> files = new ArrayList<>();
        int entryCount = 0;
        long totalUncompressedSize = 0L;
        try {
            File baseDir = new File(targetLocation);
            String basePath = getCanonicalDirPath(baseDir);
            ZipFile zipFile = null;
            // 构建 ZIP 文件并遍历
            try {
                zipFile = new ZipFile(zipLocation, "GBK");
                for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    entryCount++;
                    if (entryCount > MAX_ZIP_ENTRY_COUNT) {
                        throw new SourceException(String.format("Zip entry count exceeds limit: %s", entry.getName()));
                    }
                    // 设置目标地址
                    File singleFile = resolveZipEntryFile(baseDir, basePath, entry.getName());
                    // 如果压缩文件是文件夹则创建
                    if (entry.isDirectory()) {
                        makeDirs(singleFile);
                    } else {
                        File parentFile = singleFile.getParentFile();
                        makeDirs(parentFile);
                        try (InputStream inputStream = zipFile.getInputStream(entry);) {
                            try (OutputStream outputStream = new FileOutputStream(singleFile);) {
                                int len = 0;
                                byte[] fileBuffer = new byte[1024];
                                long entryUncompressedSize = 0L;
                                while ((len = inputStream.read(fileBuffer)) > 0) {
                                    outputStream.write(fileBuffer, 0, len);
                                    entryUncompressedSize += len;
                                    totalUncompressedSize += len;
                                    validateEntrySize(entry, entryUncompressedSize);
                                    if (totalUncompressedSize > MAX_ZIP_TOTAL_UNCOMPRESSED_SIZE) {
                                        throw new SourceException("Zip total uncompressed size exceeds limit");
                                    }
                                }
                            }
                        }
                        files.add(singleFile);
                    }
                }
            } finally {
                if (zipFile != null) {
                    zipFile.close();
                }
            }
        } catch (IOException e) {
            throw new SourceException(String.format("Unzip exception : %s", e.getMessage()), e);
        }
        return files;
    }

    private static File resolveZipEntryFile(File baseDir, String basePath, String entryName) throws IOException {
        File targetFile = new File(baseDir, entryName);
        String targetPath = targetFile.getCanonicalPath();
        if (!targetPath.equals(basePath) && !targetPath.startsWith(basePath + File.separator)) {
            throw new SourceException(String.format("Zip entry is outside of target dir: %s", entryName));
        }
        return targetFile;
    }

    private static String getCanonicalDirPath(File dir) throws IOException {
        makeDirs(dir);
        return dir.getCanonicalPath();
    }

    private static void makeDirs(File dir) throws IOException {
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            throw new IOException(String.format("Failed to create directory: %s", dir));
        }
    }

    private static void validateEntrySize(ZipEntry entry, long entryUncompressedSize) {
        if (entryUncompressedSize > MAX_ZIP_ENTRY_UNCOMPRESSED_SIZE) {
            throw new SourceException(String.format("Zip entry size exceeds limit: %s", entry.getName()));
        }
        long compressedSize = entry.getCompressedSize();
        if (compressedSize > 0 && entryUncompressedSize > compressedSize * MAX_ZIP_COMPRESSION_RATIO) {
            throw new SourceException(String.format("Zip entry compression ratio exceeds limit: %s", entry.getName()));
        }
    }

    /**
     * @param zipLocation    压缩的目的地址
     * @param zipOut         ZIP 输出流
     * @param sourceLocation 被压缩的文件信息
     * @param path           在zip中的相对路径
     * @throws IOException
     */
    private static void handlerFile(String zipLocation, ZipOutputStream zipOut, String sourceLocation, String path) throws IOException {
        log.info("开始压缩文件 : {}", sourceLocation);
        // 补充文件分隔符
        if (!"".equals(path) && !path.endsWith(File.separator)) {
            path += File.separator;
        }
        if (!sourceLocation.equals(zipLocation)) {
            return;
        }

        File sourceFile = new File(sourceLocation);
        if (sourceFile.isDirectory()) {
            // 判断是文件夹需要获取到子文件
            File[] sourceChildFiles = sourceFile.listFiles();
            // 如果子文件为空，则直接压缩空文件
            if (sourceChildFiles.length == 0) {
                zipOut.putNextEntry(new ZipEntry(path + sourceLocation + File.separator));
                zipOut.closeEntry();
            } else {
                // 否则再一个一个压缩
                for (File file : sourceChildFiles) {
                    handlerFile(zipLocation, zipOut, file.getAbsolutePath(), path + sourceFile.getName());
                }
            }
        } else {
            // 压缩单个文件
            try (InputStream inputStream = new FileInputStream(sourceFile);) {
                zipOut.putNextEntry(new ZipEntry(path + sourceFile.getName()));
                int len = 0;
                while ((len = inputStream.read(byte_simple)) > 0) {
                    zipOut.write(byte_simple, 0, len);
                }
            }

            zipOut.closeEntry();
        }
    }
}
