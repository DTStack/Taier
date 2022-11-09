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

package com.dtstack.taier.sparkyarn.sparkyarn.file;

import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.sparkyarn.sparkyarn.SparkYarnConfig;
import com.dtstack.taier.sparkyarn.sparkyarn.SparkYarnResourceInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

public class SparkResourceUploader {

    private static final Logger logger = LoggerFactory.getLogger(SparkResourceUploader.class);

    public static final String SP = File.separator;

    // default hdfs resource cleaner rate
    public static final String SPARK_DEFAULT_CLEAR_RESOURCED_RATE = "30";

    private final YarnConfiguration yarnConf;

    private final Properties sparkExtProp;

    private final SparkYarnConfig sparkYarnConfig;

    private final FilesystemManager filesystemManager;

    public SparkResourceUploader(
            YarnConfiguration yarnConf,
            SparkYarnConfig sparkYarnConfig,
            Properties sparkExtProp,
            FilesystemManager filesystemManager) {
        this.yarnConf = yarnConf;
        this.sparkExtProp = sparkExtProp;
        this.sparkYarnConfig = sparkYarnConfig;
        this.filesystemManager = filesystemManager;
    }

    public void uploadSparkResource() {
        String sparkResourcesDirProp = sparkExtProp.get(SparkYarnResourceInfo.SPARK_RESOURCES_DIR).toString();
        if (StringUtils.isBlank(sparkResourcesDirProp)) {
            sparkResourcesDirProp = SparkYarnResourceInfo.DEFAULT_SPARK_RESOURCES_DIR;
        }
        final String sparkResourcesDir = sparkResourcesDirProp;
        String md5sum = sparkYarnConfig.getMd5sum();
        String sparkClearResourceRate =
                sparkExtProp
                        .getOrDefault(
                                SparkYarnResourceInfo.SPARK_CLEAR_RESOURCED_RATE,
                                SPARK_DEFAULT_CLEAR_RESOURCED_RATE)
                        .toString();
        try {
            KerberosUtils.login(
                    sparkYarnConfig,
                    () -> {
                        try {
                            FileSystem fileSystem = FileSystem.get(yarnConf);
                            String hostName = InetAddress.getLocalHost().getHostName();
                            String sparkResourcesDirHostName =
                                    sparkResourcesDir + SparkResourceUploader.SP + hostName;
                            String sparkResourcesDirMd5sum =
                                    sparkResourcesDir
                                            + SparkResourceUploader.SP
                                            + hostName
                                            + SparkResourceUploader.SP
                                            + md5sum;
                            ResourceCleaner.start(
                                    fileSystem,
                                    sparkResourcesDirHostName,
                                    sparkResourcesDirMd5sum,
                                    sparkClearResourceRate);
                            uploadSparkSqlProxy(fileSystem, sparkResourcesDirMd5sum);

                        } catch (IOException e) {
                            throw new PluginDefineException("upload hadoop conf", e);
                        }
                        return null;
                    },
                    yarnConf);
        } catch (Exception e) {
            throw new PluginDefineException("upload hadoop conf", e);
        }
    }

    private void uploadSparkSqlProxy(FileSystem fileSystem, String sparkResourcesDirMd5sum) {
        try {
            Path localPath = new Path(getSqlProxyJarPath());
            logger.info("local path {}", localPath);
            String sparkSqlProxyPath = sparkResourcesDirMd5sum + "/spark-sql-proxy.jar";
            Path remotePath = new Path(sparkSqlProxyPath);
            fileSystem.copyFromLocalFile(localPath, remotePath);
            sparkYarnConfig.setSparkSqlProxyPath(sparkSqlProxyPath);
        } catch (IOException e) {
            throw new PluginDefineException("upload spark sql proxy failed", e);
        }
    }

    private String getSqlProxyJarPath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        File pluginDir = new File(path).getParentFile().getParentFile();
        File[] sqlProxyDirs =
                pluginDir.listFiles(
                        (dir, name) ->
                                dir.isDirectory()
                                        && name.toLowerCase().startsWith("spark-sql-proxy"));
        if (sqlProxyDirs != null && sqlProxyDirs.length == 1) {
            File[] sqlProxyJars =
                    sqlProxyDirs[0].listFiles(
                            (dir, name) ->
                                    name.toLowerCase().startsWith("spark-sql-proxy")
                                            && name.toLowerCase().endsWith(".jar"));

            if (sqlProxyJars != null && sqlProxyJars.length == 1) {
                return sqlProxyJars[0].getAbsolutePath();
            }
        }
        throw new PluginDefineException(
                "Can not find spark sql proxy jar in path: " + pluginDir);
    }
}
