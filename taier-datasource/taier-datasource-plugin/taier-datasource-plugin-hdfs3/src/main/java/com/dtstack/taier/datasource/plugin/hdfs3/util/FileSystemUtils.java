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
package com.dtstack.taier.datasource.plugin.hdfs3.util;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.gson.GsonBuilder;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class FileSystemUtils {


    public static void backupDirector(Path sourcePath, Path backupPath, FileSystem fs, Configuration configuration) throws IOException {
        if (isExists(fs, sourcePath)) {
            //判断是不是文件夹
            if (fs.isDirectory(sourcePath)) {
                if (!FileUtil.copy(fs, sourcePath, fs, backupPath, false, configuration)) {
                    throw new SourceException("copy " + sourcePath.toString() + " to " + backupPath.toString() + " failed");
                }
            } else {
                throw new SourceException(sourcePath.toString() + "is not a directory");
            }
        } else {
            throw new SourceException(sourcePath.toString() + " is not exists");
        }
    }

    public static void backupFile(Path sourcePath, Path backupPath, FileSystem fs, Configuration configuration) throws IOException {
        //判断是不是文件
        if (isExists(fs, sourcePath)) {
            if (fs.isFile(sourcePath)) {
                if (!FileUtil.copy(fs, sourcePath, fs, backupPath, false, configuration)) {
                    throw new SourceException("copy " + sourcePath.toString() + " to " + backupPath.toString() + " failed");
                }
            } else {
                throw new SourceException(sourcePath.toString() + "is not a file");
            }

        } else {
            throw new SourceException(sourcePath.toString() + " is not exists");
        }
    }

    public static boolean isExists(FileSystem fs, Path path) throws IOException {
        return fs.exists(path);
    }

    /**
     * 判断文件是否是gzip压缩
     */
    public static boolean isGzip(Path path, FileSystem fs) throws IOException {
        try (FSDataInputStream in = fs.open(path)) {

            byte[] bs = new byte[2];
            int read = in.read(bs);
            if (read != 2) {
                return false;
            }
            int b0 = bs[0];
            int b1 = bs[1];

            return GZIPInputStream.GZIP_MAGIC == ((b1 & 0xFF) << 8 | b0);
        }
    }


    /**
     * 判断文件是否是bzip2
     * 只要获取 BZip2CompressorInputStream 成功即可
     */
    public static boolean isBzip2(Path path, FileSystem fs) {
        try (BZip2CompressorInputStream in = new BZip2CompressorInputStream(fs.open(path))) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 将configuration的配置信息转为json
     */
    public static String printConfiguration(Configuration configuration) {
        HashMap<String, String> config = new HashMap<>(32);
        for (Map.Entry<String, String> entry : configuration) {
            config.put(entry.getKey(), entry.getValue());
        }
        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create().toJson(config);
    }
}
