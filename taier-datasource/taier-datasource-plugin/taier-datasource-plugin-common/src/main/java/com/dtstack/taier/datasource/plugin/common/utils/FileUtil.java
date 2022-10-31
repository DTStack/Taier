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

import java.io.File;

/**
 * 文件操作工具类
 *
 * @author luming
 * @date 2022/3/7
 */
@Slf4j
public class FileUtil {
    /**
     * 校验本地文件是否存在,存在则返回文件
     *
     * @param localPath
     * @return
     */
    public static File checkFileExists(String localPath) {
        File file = new File(localPath);
        if (!file.isFile() || !file.exists()) {
            log.error(" 文件 {} 不存在", localPath);
            throw new SourceException(String.format("文件 %s 不存在", localPath));
        }
        return file;
    }
}
