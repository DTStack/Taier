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
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.sparkk8s.config.SparkK8sConfig;
import com.dtstack.engine.sparkk8s.utils.SparkConfigUtil;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Date: 2020/7/8
 * Company: www.dtstack.com
 * @author maqi
 */
public class MrSubmiter extends AbstractSparkSubmiter {
    private static final Logger LOG = LoggerFactory.getLogger(MrSubmiter.class);

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
        String jarName = StringUtils.substring(jarPath, jarPath.lastIndexOf("/"));
        String sftpDir = StringUtils.substringBetween(jarPath, SFTP_PREFIX, jarName);

        if (Strings.isNullOrEmpty(appName)) {
            throw new RdosDefineException("spark jar must set app name!");
        }

        List<String> argList = new ArrayList<>();
        argList.add("--primary-java-resource");
        argList.add(jarImagePath);
        argList.add("--main-class");
        argList.add(mainClass);

        String jobArgs = buildJobParams();
        if (!StringUtils.isEmpty(jobArgs)) {
            argList.add(jobArgs);
        }

        LOG.info("jarPath:{}, jarImagePath:{}, sftpDir:{}, jobArgs:{}", jarPath, jarImagePath, sftpDir, jobArgs);
        Properties confProp = jobClient.getConfProperties();
        SparkConf sparkConf = SparkConfigUtil.buildBasicSparkConf(sparkDefaultProp);
        SparkConfigUtil.replaceBasicSparkConf(sparkConf, confProp);

        // operator hdfs
        SparkConfigUtil.setHadoopUserName(sparkK8sConfig, sparkConf);
        sparkConf.setAppName(appName);
        // sftp config
        fillSftpConfig(sftpDir, sparkConf, sparkK8sConfig.getSftpConf());

        return runJobReturnResult(argList, sparkConf);
    }

    @Override
    public String buildJobParams() {
        JobParam jobParam = new JobParam(jobClient);
        String exeArgsStr = jobParam.getClassArgs();
        String[] appArgs = new String[]{};

        if (StringUtils.isNotBlank(exeArgsStr)) {
            appArgs = exeArgsStr.split("\\s+");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String appArg : appArgs) {
            if (StringUtils.isBlank(appArg) || StringUtils.equalsIgnoreCase(appArg, "null")) {
                continue;
            }
            stringBuilder.append(" --arg ");
            stringBuilder.append(appArg);
        }
        return stringBuilder.toString();
    }
}
