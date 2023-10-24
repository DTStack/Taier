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
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.sparkyarn.sparkyarn.SparkYarnConfig;
import com.dtstack.taier.sparkyarn.sparkyarn.SparkYarnResourceInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static com.dtstack.taier.sparkyarn.sparkyarn.constant.SparkConstants.*;

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
        Object sparkResourcesDirProp = sparkExtProp.get(SparkYarnResourceInfo.SPARK_RESOURCES_DIR);
        if (sparkResourcesDirProp == null || StringUtils.isBlank(sparkResourcesDirProp.toString())) {
            sparkResourcesDirProp = SparkYarnResourceInfo.DEFAULT_SPARK_RESOURCES_DIR;
        }
        final String sparkResourcesDir = sparkResourcesDirProp.toString();
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
                            uploadHadoopConf(fileSystem, sparkResourcesDirMd5sum);
                            uploadSparkSqlProxy(fileSystem, sparkResourcesDirMd5sum);
                            uploadKerberosConf(fileSystem, sparkResourcesDirMd5sum);


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
        File[] sqlProxyDir =
                pluginDir.listFiles(
                        (dir, name) ->
                                dir.isDirectory()
                                        && name.toLowerCase().startsWith("spark-sql-proxy"));
        if (sqlProxyDir != null && sqlProxyDir.length == 1) {
            File[] sqlProxyJars = sqlProxyDir[0].listFiles();
            if (sqlProxyJars != null && sqlProxyJars.length == 1) {
                String sqlProxyJar = sqlProxyJars[0].getName();
                if (sqlProxyJar.toLowerCase().startsWith("spark-sql-proxy") && sqlProxyJar.toLowerCase().endsWith(".jar")) {
                    return sqlProxyJars[0].getAbsolutePath();
                }
            }
        }
        throw new PluginDefineException(
                "Can not find spark sql proxy jar in path: " + pluginDir);
    }


    public void uploadKerberosConf(FileSystem fileSystem, String sparkResourcesDirMd5sum) {
        if (sparkYarnConfig.isOpenKerberos()) {
            try {
                String keytab = KerberosUtils.getKeytabPath(sparkYarnConfig);
                String krb5 = new File(keytab).getParent() + File.separator + ConfigConstant.KRB5_CONF;
                String remoteKeytab =
                        sparkResourcesDirMd5sum + File.separator + new File(keytab).getName();
                String remoteKrb5 =
                        sparkResourcesDirMd5sum + File.separator + new File(krb5).getName();

                fileSystem.copyFromLocalFile(new Path(keytab), new Path(remoteKeytab));
                fileSystem.copyFromLocalFile(new Path(krb5), new Path(remoteKrb5));
                sparkExtProp.setProperty(ConfigConstant.SPARK_KERBEROS_REMOTE_KEYTAB, remoteKeytab);
                sparkExtProp.setProperty(ConfigConstant.SPARK_KERBEROS_REMOTE_KRB5, remoteKrb5);
            } catch (IOException e) {
                throw new PluginDefineException("upload kerberos conf failed", e);
            }
        }
    }


    public void uploadHadoopConf(FileSystem fileSystem, String sparkResourcesDirMd5sum) {
        try {
            Class clazz = Class.forName("org.apache.hadoop.conf.Configuration");
            Method method = clazz.getDeclaredMethod("getOverlay");
            method.setAccessible(true);
            Properties yarnConfProp = (Properties) method.invoke(yarnConf);
            Map<String, Object> yarnConfMap = new HashMap<>();
            for (Map.Entry<Object, Object> yarnConfEntry : yarnConfProp.entrySet()) {
                if (FILTER_PARAM.contains((String) yarnConfEntry.getKey())) {
                    continue;
                }
                yarnConfMap.put((String) yarnConfEntry.getKey(), yarnConfEntry.getValue());
            }
            String coreSiteContent = getCoreSiteContent(yarnConfMap);
            File tmpHadoopConfFileDir =
                    new File(
                            String.format(
                                    "%s/%s/%s/%s",
                                    System.getProperty("user.dir"),
                                    "tmp",
                                    "spark",
                                    "local_hadoop_conf"));
            if (!tmpHadoopConfFileDir.exists()) {
                tmpHadoopConfFileDir.mkdirs();
            }
            File tmpHadoopConfFile =
                    File.createTempFile(
                            sparkYarnConfig.getMd5sum() + "core-site.xml",
                            null,
                            tmpHadoopConfFileDir);
            try (FileWriter fwrt = new FileWriter(tmpHadoopConfFile)) {
                fwrt.write(coreSiteContent);
                fwrt.flush();
            } catch (Exception e) {
                logger.error("Write yarnConf error " + ExceptionUtil.getErrorMessage(e));
                tmpHadoopConfFile.delete();
                throw new PluginDefineException(e);
            }

            String sparkHadoopConfDir =
                    sparkResourcesDirMd5sum + File.separator + HADOOP_CONF;
            String hiveSite = sparkHadoopConfDir + HIVE_SITE;
            String coreSite = sparkHadoopConfDir + CORE_SITE;
            String yarnSite = sparkHadoopConfDir + YARN_SITE;
            Path remoteHiveSitePath = new Path(hiveSite);
            logger.info("Upload hive-site.xml to remote path {}", remoteHiveSitePath);
            fileSystem.copyFromLocalFile(new Path(tmpHadoopConfFile.getPath()), remoteHiveSitePath);
            fileSystem.setPermission(remoteHiveSitePath, new FsPermission((short) 0777));

            Path remoteCoreSitePath = new Path(coreSite);
            logger.info("Upload core-site.xml to remote path {}", remoteCoreSitePath);
            fileSystem.copyFromLocalFile(new Path(tmpHadoopConfFile.getPath()), remoteCoreSitePath);
            fileSystem.setPermission(remoteCoreSitePath, new FsPermission((short) 0777));

            // upload yarn-site.xml
            Path remoteYarnSitePath = new Path(yarnSite);
            logger.info("Upload yarn-site.xml to remote path {}", remoteYarnSitePath);
            fileSystem.copyFromLocalFile(new Path(tmpHadoopConfFile.getPath()), remoteYarnSitePath);
            fileSystem.setPermission(remoteYarnSitePath, new FsPermission((short) 0777));

            sparkExtProp.setProperty(
                    ConfigConstant.SPARK_HADOOP_CONF_REMOTE_DIR, sparkHadoopConfDir);
            tmpHadoopConfFile.delete();

        } catch (Exception e) {
            throw new PluginDefineException("upload hadoop conf failed", e);
        }
    }

    private String getCoreSiteContent(Map hadoopConfMap) {
        StringBuilder hadoopConfContent = new StringBuilder();
        hadoopConfContent
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append(System.lineSeparator());
        hadoopConfContent
                .append("<?xml-stylesheet href=\"configuration.xsl\" type=\"text/xsl\"?>")
                .append(System.lineSeparator());
        hadoopConfContent.append("<configuration>").append(System.lineSeparator());
        Iterator<Entry<String, Object>> it = hadoopConfMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            String name = e.getKey();
            // xml文件校验&需要转换为xml文件可识别字符
            String value = e.getValue().toString().replaceAll("&", "&amp;");
            hadoopConfContent.append("    <property>").append(System.lineSeparator());
            hadoopConfContent
                    .append("        <name>")
                    .append(name)
                    .append("</name>")
                    .append(System.lineSeparator());
            hadoopConfContent
                    .append("        <value>")
                    .append(value)
                    .append("</value>")
                    .append(System.lineSeparator());
            hadoopConfContent.append("    </property>").append(System.lineSeparator());
        }
        hadoopConfContent.append("</configuration>").append(System.lineSeparator());

        return hadoopConfContent.toString();
    }
}
