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

package com.dtstack.taier.scheduler.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.Xml2JsonUtil;
import com.dtstack.taier.common.util.ZipUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2020-05-25
 */
public class FileUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * parse file to map
     *
     * @param files
     * @return {fileName, Map<K,V>}
     */
    public static Map<String, Map<String, Object>> parse2Map(List<File> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, Object>> confMap = new HashMap<>(files.size());
        Map<String, Object> fileMap;
        for (File file : files) {
            if (file.getName().startsWith(CommonConstant.DOT)) {
                // .开头过滤
                continue;
            }
            if (file.getName().endsWith(CommonConstant.XML_SUFFIX)) {
                // xml文件
                try {
                    fileMap = Xml2JsonUtil.xml2map(file);
                } catch (Exception e) {
                    throw new RdosDefineException(CommonConstant.XML_SUFFIX + ErrorCode.FILE_PARSE_ERROR.getMsg(), e);
                }
            } else if (file.getName().endsWith(CommonConstant.JSON_SUFFIX)) {
                // json文件
                String jsonStr;
                try {
                    jsonStr = Xml2JsonUtil.readFile(file);
                } catch (IOException e) {
                    throw new RdosDefineException(CommonConstant.JSON_SUFFIX + ErrorCode.FILE_PARSE_ERROR.getMsg(), e);
                }
                if (StringUtils.isBlank(jsonStr)) {
                    continue;
                }
                fileMap = (Map<String, Object>) JSONObject.parseObject(jsonStr, Map.class);
            } else {
                throw new RdosDefineException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
            }
            if (null != fileMap) {
                confMap.put(file.getName(), fileMap);
            }
        }
        return confMap;
    }

    public static File getOneFileWithSuffix(String dir, String suffix) {
        List<File> fileWithSuffix = getFileWithSuffix(dir, suffix);
        return fileWithSuffix.stream().findFirst().orElse(null);
    }

    /**
     * 目录下带后缀的文件列表
     *
     * @param dir
     * @param suffix
     * @return
     */
    public static List<File> getFileWithSuffix(String dir, String suffix) {
        if (StringUtils.isBlank(dir) || StringUtils.isBlank(suffix)) {
            throw new RdosDefineException(ErrorCode.PARAM_NULL);
        }
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (files.length > 0) {
                return Arrays.stream(files)
                        .filter(f -> f.getName().endsWith(suffix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * 从 zip 包中寻找 xml 文件
     *
     * @param targetPath 临时解压目录
     * @param srcZip     zip 源
     * @return
     */
    public static List<File> getXmlFileFromZip(String targetPath, String srcZip) {
        if (StringUtils.isBlank(targetPath) || StringUtils.isBlank(srcZip)) {
            throw new RdosDefineException(ErrorCode.PARAM_NULL);
        }
        File targetPathFile = new File(targetPath);
        if (targetPathFile.exists()) {
            try {
                //删除本地目录
                FileUtils.forceDelete(targetPathFile);
            } catch (IOException e) {
                LOGGER.info("delete  local path  {} error ", targetPathFile, e);
            }
        }
        // 解压到本地
        FileUtil.unzip2TargetPath(targetPath, srcZip);
        if (!targetPathFile.isDirectory()) {
            throw new RdosDefineException("path not directory");
        }

        File oneFileWithSuffix = getOneFileWithSuffix(targetPathFile.getPath(), CommonConstant.XML_SUFFIX);
        File targetDirectory = null;
        // 容错，防止 zip 解压后仍嵌套了一层文件夹
        if (oneFileWithSuffix == null) {
            File[] files = targetPathFile.listFiles();
            if (null != files && files.length > 0 && files[0].isDirectory()) {
                targetDirectory = files[0];
            }
        } else {
            targetDirectory = oneFileWithSuffix.getParentFile();
        }
        if (targetDirectory != null) {
            return getFileWithSuffix(targetDirectory.getAbsolutePath(), CommonConstant.XML_SUFFIX);
        } else {
            return Collections.emptyList();
        }
    }

    public static void unzip2TargetPath(String targetPath, String srcFile) {
        try {
            ZipUtil.upzipFile(srcFile, targetPath);
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(new File(targetPath));
            } catch (IOException ioException) {
                LOGGER.error("delete zip directory {} error ", targetPath);
            }
        }
    }

    public static File newFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RdosDefineException("file does not exist");
        }
        return file;
    }

    public static String getUploadFileName(Integer taskType, String jobId) {
        EScheduleJobType jobType = EScheduleJobType.getByTaskType(taskType);
        String suffix = ".py";
        if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
            suffix = ".sh";
        }
        return String.format("%s_%s_%s%s", jobType.name(), jobId, System.currentTimeMillis(), suffix);
    }
}
