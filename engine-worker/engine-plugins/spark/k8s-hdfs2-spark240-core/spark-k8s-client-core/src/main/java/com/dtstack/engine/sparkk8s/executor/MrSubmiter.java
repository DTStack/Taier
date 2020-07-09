/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.sparkk8s.executor;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.sparkk8s.config.SparkK8sConfig;
import com.dtstack.engine.sparkk8s.utils.SparkConfigUtil;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.k8s.submit.ClientArguments;
import org.apache.spark.deploy.k8s.submit.DtKubernetesClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Date: 2020/7/8
 * Company: www.dtstack.com
 * @author maqi
 */
public class MrSubmiter implements SparkSubmit {
    private static final Logger LOG = LoggerFactory.getLogger(MrSubmiter.class);

    // 用户jar存储到镜像中的默认文件夹
    private static final String DEFAULT_USERJAR_LOCATION = "/opt/dtstack/userjar";
    private static final String SFTP_PREFIX = "sftp://";
    private static final String SFTP_FLAG = "sftp_";
    private static final String SFTP_REMOTE_PATH_KEY = "sftp_remotePath";
    private static final String SFTP_LOCAL_PATH_KEY = "sftp_localPath";
    private static final String LOCAL_PREFIX = "local://";

    private final JobClient jobClient;
    private Properties sparkDefaultProp;
    private SparkK8sConfig sparkK8sConfig;

    public MrSubmiter(JobClient jobClient, SparkK8sConfig sparkK8sConfig, Properties sparkDefaultProp) {
        this.jobClient = jobClient;
        this.sparkDefaultProp = sparkDefaultProp;
        this.sparkK8sConfig = sparkK8sConfig;
    }

    @Override
    public JobResult submit() {
        JobParam jobParam = new JobParam(jobClient);
        String appName = jobParam.getJobName();
        String mainClass = jobParam.getMainClass();
        String jarPath = jobParam.getJarPath();
        String jarImagePath = getJarImagePath(jarPath);
        String sftpDir = StringUtils.substringBetween(jarPath, SFTP_PREFIX, jarImagePath);

        if (Strings.isNullOrEmpty(appName)) {
            throw new RdosDefineException("spark jar must set app name!");
        }

        String jobArgs = buildJobParams();
        List<String> argList = new ArrayList<>();
        argList.add("--primary-java-resource");
        argList.add(jarImagePath);
        argList.add("--main-class");
        argList.add(mainClass);
        argList.add("--arg");
        argList.add(jobArgs);

        LOG.info("jarPath:{}, jarImagePath:{}, sftpDir:{}, jobArgs:{}", jarPath, jarImagePath, sftpDir, jobArgs);

        DtKubernetesClientApplication k8sClientApp = new DtKubernetesClientApplication();
        ClientArguments clientArguments = ClientArguments.fromCommandLineArgs(argList.toArray(new String[argList.size()]));

        Properties confProp = jobClient.getConfProperties();
        SparkConf sparkConf = SparkConfigUtil.buildBasicSparkConf(sparkDefaultProp);
        SparkConfigUtil.replaceBasicSparkConf(sparkConf, confProp);

        // operator hdfs
        SparkConfigUtil.setHadoopUserName(sparkK8sConfig, sparkConf);
        sparkConf.setAppName(appName);
        // sftp config
        fillSftpConfig(sftpDir, sparkConf);

        try {
            String appId = k8sClientApp.run(clientArguments, sparkConf);
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    /**
     * 通过将sftp配置绑定到环境变量，来选择是否下载user jar
     * @param sftpDir
     * @param sparkConf
     */
    private void fillSftpConfig(String sftpDir, SparkConf sparkConf) {
        sparkK8sConfig.getSftpConf().forEach((k, v) -> sparkConf.set(SFTP_FLAG + k, v));
        sparkConf.set(SFTP_REMOTE_PATH_KEY, sftpDir);
        sparkConf.set(SFTP_LOCAL_PATH_KEY, DEFAULT_USERJAR_LOCATION);
    }

    /**
     *  sftp文件会下载到镜像的指定路径下，重命名为镜像内部的地址。
     *
     * @param jarPath
     * @return
     */
    private String getJarImagePath(String jarPath) {
        if (!jarPath.startsWith(SFTP_PREFIX)) {
            throw new RdosDefineException("spark jar path protocol must be " + SFTP_PREFIX);
        }
        String jarName = StringUtils.substring(jarPath, jarPath.lastIndexOf("/"));
        String jarUrl = LOCAL_PREFIX + DEFAULT_USERJAR_LOCATION + jarName;
        LOG.info("the storage location of user jar packages in the image is :{} ", jarUrl);
        return jarUrl;
    }

    @Override
    public String buildJobParams() {
        JobParam jobParam = new JobParam(jobClient);
        String exeArgsStr = jobParam.getClassArgs();
        String args = "";
        if (StringUtils.isNotBlank(exeArgsStr)) {
            args = Arrays.stream(exeArgsStr.split("\\s+")).collect(Collectors.joining("--arg"));
        }
        return args;
    }
}
