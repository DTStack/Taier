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

package com.dtstack.taier.flink.util;

import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.flink.constrant.ConfigConstrant;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.base.enums.ClassLoaderType;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.JarUtils;
import org.apache.flink.yarn.Utils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlinkUtil.class);


    public static PackagedProgram buildProgram(String jarPath, List<URL> classpaths, EJobType jobType,
                                               String entryPointClass, String[] programArgs,
                                               SavepointRestoreSettings spSetting, org.apache.flink.configuration.Configuration flinkConfiguration, FilesystemManager filesystemManager)
            throws IOException, ProgramInvocationException {
        if (jarPath == null) {
            throw new IllegalArgumentException("The program JAR file was not specified.");
        }
        File jarFile = new File(jarPath);

        org.apache.flink.configuration.Configuration flinkConfig = new org.apache.flink.configuration.Configuration(flinkConfiguration);
        String classloaderCache = flinkConfig.getString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_TRUE);
        flinkConfig.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, classloaderCache);

        String append = flinkConfig.getString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL);
        if (jobType == EJobType.SQL || jobType == EJobType.SYNC) {
            String dtstackAppend = "com.fasterxml.jackson.";
            if (StringUtils.isNotEmpty(append)) {
                dtstackAppend = dtstackAppend + ";" + append;
            }
            flinkConfig.setString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL, dtstackAppend);
        }

        PackagedProgram program = PackagedProgram.newBuilder()
                .setJarFile(jarFile)
                .setUserClassPaths(classpaths)
                .setEntryPointClassName(entryPointClass)
                .setConfiguration(flinkConfig)
                .setArguments(programArgs)
                .setSavepointRestoreSettings(spSetting)
                .build();

        return program;
    }

    public static File downloadJar(String remotePath, String localDir, FilesystemManager filesystemManager, boolean localPriority) throws IOException {
        if(localPriority){
            //如果不是http 或者 hdfs协议的从本地读取
            File localFile = new File(remotePath);
            if(localFile.exists()){
                return localFile;
            }
        }

        String localJarPath = FlinkUtil.getTmpFileName(remotePath, localDir);
        File downloadFile = filesystemManager.downloadFile(remotePath, localJarPath);
        logger.info("downloadFile remotePath:{} localJarPath:{}", remotePath, localJarPath);

        URL jarFileUrl;

        try {
            jarFileUrl = downloadFile.getAbsoluteFile().toURI().toURL();
        } catch (MalformedURLException e1) {
            throw new IllegalArgumentException("The jar file path is invalid.");
        }

        JarUtils.checkJarFile(jarFileUrl);

        return downloadFile;
    }

    private static String getTmpFileName(String fileUrl, String toPath){
        String fileName = StringUtils.substringAfterLast(fileUrl, File.separator);
        String tmpFileName = toPath  + File.separator + fileName;
        return tmpFileName;
    }

    /**
     *
     * FIXME 仅针对sql执行方式,暂时未找到区分设置source,transform,sink 并行度的方式
     * 设置job运行的并行度
     * @param properties
     */
    public static int getEnvParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.SQL_ENV_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):1;
    }


    /**
     * 针对MR类型整个job的并发度设置
     * @param properties
     * @return
     */
    public static int getJobParallelism(Properties properties){
        String parallelismStr = properties.getProperty(ConfigConstrant.MR_JOB_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr)?Integer.parseInt(parallelismStr):1;
    }

    /**
     * 获取hdfs文件Last Modified时间
     * @param dst   hdfs路径
     * @param fs    FileSystem
     * @param localSrcFile  本地路径
     * @return
     * @throws IOException
     */
    public static long getLastModifiedTime(Path dst, FileSystem fs, File localSrcFile) throws IOException {
        FileStatus[] fss = null;
        int iter = 1;

        while (iter <= Utils.REMOTE_RESOURCES_FETCH_NUM_RETRY + 1) {
            try {
                fss = fs.listStatus(dst);
                break;
            } catch (FileNotFoundException e) {
                logger.debug("Got FileNotFoundException while fetching uploaded remote resources at retry num {}", iter);
                try {
                    logger.debug("Sleeping for {}ms", Utils.REMOTE_RESOURCES_FETCH_WAIT_IN_MILLI);
                    TimeUnit.MILLISECONDS.sleep(Utils.REMOTE_RESOURCES_FETCH_WAIT_IN_MILLI);
                } catch (InterruptedException ie) {
                    logger.warn("Failed to sleep for {}ms at retry num {} while fetching uploaded remote resources",
                            Utils.REMOTE_RESOURCES_FETCH_WAIT_IN_MILLI, iter, ie);
                }
                iter++;
            }
        }

        final long dstModificationTime;
        if (fss != null && fss.length >  0) {
            dstModificationTime = fss[0].getModificationTime();
            logger.debug("Got modification time {} from remote path {}", dstModificationTime, dst);
        } else {
            dstModificationTime = localSrcFile.lastModified();
            logger.debug("Failed to fetch remote modification time from {}, using local timestamp {}", dst, dstModificationTime);
        }
        return dstModificationTime;
    }

    public static String getTaskWorkspace(String taskId) {
        return String.format("%s/%s_%s", ConfigConstrant.TMP_DIR, taskId, Thread.currentThread().getId());
    }

}