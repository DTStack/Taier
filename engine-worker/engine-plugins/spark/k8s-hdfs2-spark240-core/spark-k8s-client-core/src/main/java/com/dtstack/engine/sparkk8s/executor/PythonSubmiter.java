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
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.sparkk8s.config.SparkK8sConfig;
import com.dtstack.engine.sparkk8s.utils.SparkConfigUtil;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Date: 2020/7/9
 * Company: www.dtstack.com
 * @author maqi
 */
public class PythonSubmiter extends AbstractSparkSubmiter {
    private static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";
    private static final String PYTHON_RUNNER_DEPENDENCY_RES_KEY = "extRefResource";

    private final JobClient jobClient;
    private Properties sparkDefaultProp;
    private SparkK8sConfig sparkK8sConfig;

    public PythonSubmiter(JobClient jobClient, SparkK8sConfig sparkK8sConfig, Properties sparkDefaultProp) {
        this.jobClient = jobClient;
        this.sparkDefaultProp = sparkDefaultProp;
        this.sparkK8sConfig = sparkK8sConfig;
    }


    @Override
    public JobResult submit() {
        JobParam jobParam = new JobParam(jobClient);
        //.py .egg .zip 存储在sftp
        String pyFilePath = jobParam.getJarPath();
        String pyFilePathImagePath = getJarImagePath(pyFilePath);
        String jarName = StringUtils.substring(pyFilePath, pyFilePath.lastIndexOf("/"));
        String sftpDir = StringUtils.substringBetween(pyFilePath, AbstractSparkSubmiter.SFTP_PREFIX, jarName);

        String appName = jobParam.getJobName();
        String exeArgsStr = jobParam.getClassArgs();

        if (Strings.isNullOrEmpty(pyFilePath)) {
            return JobResult.createErrorResult("exe python file can't be null.");
        }

        if (Strings.isNullOrEmpty(appName)) {
            return JobResult.createErrorResult("an application name must be set in your configuration");
        }

        List<String> argList = new ArrayList<>();
        argList.add("--primary-py-file");
        argList.add(pyFilePathImagePath);

        argList.add("--class");
        argList.add(PYTHON_RUNNER_CLASS);

        String[] appArgs = new String[]{};
        if (StringUtils.isNotBlank(exeArgsStr)) {
            appArgs = exeArgsStr.split("\\s+");
        }

        String dependencyResource = "";
        boolean nextIsDependencyVal = false;
        for (String appArg : appArgs) {
            if (nextIsDependencyVal) {
                dependencyResource = appArg;
                continue;
            }

            if (PYTHON_RUNNER_DEPENDENCY_RES_KEY.equals(appArg)) {
                nextIsDependencyVal = true;
                continue;
            }

            argList.add("--arg");
            argList.add(appArg);
            nextIsDependencyVal = false;
        }

        String pythonExtPath = sparkK8sConfig.getSparkPythonExtLibPath();
        if (Strings.isNullOrEmpty(pythonExtPath)) {
            return JobResult.createErrorResult("engine node.yml setting error, " +
                    "commit spark python job need to set param of sparkPythonExtLibPath.");
        }
        //TODO FIX 添加自定义的依赖包
        if (!Strings.isNullOrEmpty(dependencyResource)) {
            pythonExtPath = pythonExtPath + "," + dependencyResource;
        }

        argList.add("--other-py-files");
        argList.add(pythonExtPath);

        Properties confProp = jobClient.getConfProperties();
        SparkConf sparkConf = SparkConfigUtil.buildBasicSparkConf(sparkDefaultProp);
        SparkConfigUtil.replaceBasicSparkConf(sparkConf, confProp);

        sparkConf.setAppName(appName);
        // sftp config
        fillSftpConfig(sftpDir, sparkConf, sparkK8sConfig.getSftpConf());
        return runJobReturnResult(argList, sparkConf);
    }

    @Override
    public String buildJobParams() {
        return null;
    }
}
