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

package com.dtstack.engine.datasource.common.utils.datakit;

import dt.insight.plat.lang.base.Charsets;
import dt.insight.plat.lang.base.Checks;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2020/12/15
 * @desc
 */
public class IOs {

    public static final Logger log = LoggerFactory.getLogger(IOs.class);

    private IOs() {
    }

    public static final String readAsString(InputStream ins) throws IOException {
        return readAsString(ins, Charsets.UTF8);
    }

    /**
     * 读取流并转为对应字节编码的字符串
     */
    public static final String readAsString(InputStream ins, Charset charset) {
        Checks.nonNull(ins, "输入流不能为空");
        Checks.nonNull(charset, "字符集不能为空");

        byte[] buffer = new byte[1024];
        int len;
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            while ((len = ins.read(buffer)) != -1) {
                bao.write(buffer, 0, len);
            }
            return new String(bao.toByteArray(), charset);
        } catch (IOException e) {
            log.error("读取流异常", e);
            return null;
        }
    }

    /**
     * 输入流与输出流的对拷
     *
     * @param ins 输入流
     * @param os  输出流
     */
    public static final void copy(InputStream ins, OutputStream os) {
        Optional.ofNullable(ins)
                .ifPresent(x -> {
                    try {
                        IOUtils.copy(ins, os);
                        ins.close();
                        os.flush();
                        os.close();
                    } catch (IOException e) {
                        log.error("输入输出流的对拷异常", e);
                    }
                });

    }

    /**
     * 文件转化为inputStream
     *
     * @param file 文件对象
     */
    public static final FileInputStream fileToStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("file not found.", e);
            return null;
        }
    }

    /**
     * 获取流的当前可读长度大小
     */
    public static int available(InputStream inputStream) {
        int available = 0;
        try {
            int i = 0;
            while (available == 0) {
                available = inputStream.available();
                if (i++ > 4) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error("获取IO流可读字节长度异常", e);
        }
        return available;
    }

    public static final void closeQuietly(InputStream ins) {
        if (Objects.nonNull(ins)) {
            try {
                ins.close();
            } catch (IOException ignored) {
            }
        }

    }

    public static final void closeQuietly(Writer writer) {
        if (Objects.nonNull(writer)) {
            try {
                writer.close();
            } catch (IOException ignored) {
            }
        }

    }

    public static final void closeQuietly(Reader reader) {
        if (Objects.nonNull(reader)) {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }

    }

    public static final void closeQuietly(OutputStream os) {
        if (Objects.nonNull(os)) {
            try {
                os.close();
            } catch (IOException ignored) {
            }
        }

    }

    public static boolean mkdirs(String path) {
        return mkdirs(new File(path));
    }

    public static boolean mkdirs(File file) {
        if (file.isFile()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                return parentFile.mkdirs();
            }
        } else {
            if (!file.exists()) {
                return file.mkdirs();
            }
        }
        return false;
    }

    /**
     * 根据名称定义临时文件（与类对象同级
     *
     * @param clazz    类对象
     * @param filename 文件名称 + 尾缀扩展名
     */
    public static File defineFile(Class clazz, String filename) {
        String tmpDir = clazz.getResource("").getPath() + "tmp";
        IOs.mkdirs(tmpDir);
        return new File(Strings.format("{}/{}", tmpDir, filename));
    }
}
