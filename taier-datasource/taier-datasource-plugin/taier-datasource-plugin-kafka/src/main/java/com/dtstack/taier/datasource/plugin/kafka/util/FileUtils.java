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

package com.dtstack.taier.datasource.plugin.kafka.util;

import com.dtstack.taier.datasource.plugin.common.utils.IOUtils;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:29 2020/8/22
 * @Description：文件工具类
 */
public class FileUtils {
    /**
     * Writes a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
     *
     * @param file
     * @param data
     * @throws IOException
     */
    public static void write(File file, CharSequence data) throws IOException {
        write(file, data, Charset.defaultCharset(), false);
    }

    /**
     * Writes a CharSequence to a file creating the file if it does not exist.
     *
     * @param file
     * @param data
     * @param encoding
     * @param append
     * @throws IOException
     */
    public static void write(File file, CharSequence data, Charset encoding, boolean append) throws IOException {
        String str = data == null ? null : data.toString();
        writeStringToFile(file, str, encoding, append);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file
     * @param data
     * @param encoding
     * @param append
     * @throws IOException
     */
    public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     *
     * @param file
     * @param append
     * @return
     * @throws IOException
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new SourceException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new SourceException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new SourceException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }
}
