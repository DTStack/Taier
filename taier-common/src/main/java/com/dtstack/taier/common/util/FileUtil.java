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

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * file util
 *
 * @author ：wangchuan
 * date：Created in 17:06 2022/10/8
 * company: www.dtstack.com
 */
public class FileUtil {

    public static void mkdirsIfNotExist(String directoryPath) {
        if (StringUtils.isEmpty(directoryPath)) {
            throw new DtCenterDefException(ErrorCode.INVALID_PARAMETERS);
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            try {
                FileUtils.forceMkdir(directory);
            } catch (IOException e) {
                throw new DtCenterDefException(e.getMessage());
            }
        }
    }

    public static String getFilename(String filepath) {
        if (StringUtils.isEmpty(filepath)) {
            throw new TaierDefineException("filepath cannot be empty");
        }
        int lastIndexOf = filepath.lastIndexOf(File.separator);
        if (lastIndexOf == filepath.length() - 1 || lastIndexOf == -1) {
            throw new TaierDefineException("file does not exist");
        }
        return filepath.substring(lastIndexOf);
    }

    public static String getFiletype(String filepath) {
        String filename = getFilename(filepath);
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == filename.length() -1 || lastIndexOf == -1) {
            throw new TaierDefineException("file type is undefined");
        }
        return filename.substring(lastIndexOf + 1);
    }
}
