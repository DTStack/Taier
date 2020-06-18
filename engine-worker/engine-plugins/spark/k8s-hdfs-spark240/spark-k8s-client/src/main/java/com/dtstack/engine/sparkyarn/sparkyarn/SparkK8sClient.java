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

package com.dtstack.engine.sparkyarn.sparkyarn;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.sparkyarn.sparkyarn.config.SparkK8sConfig;
import com.dtstack.engine.sparkyarn.sparkyarn.utils.SparkConfigUtil;
import com.google.common.base.Charsets;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.k8s.submit.ClientArguments;
import org.apache.spark.deploy.k8s.submit.DtKubernetesClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Date: 2020/6/18
 * Company: www.dtstack.com
 * @author maqi
 */
public class SparkK8sClient extends AbstractClient {
    private static final Logger logger = LoggerFactory.getLogger(SparkK8sClient.class);

    private static final String LOG_LEVEL_KEY = "logLevel";

    private Properties sparkDefaultProp;

    private SparkK8sConfig sparkK8sConfig;

    @Override
    public void init(Properties prop) throws Exception {
        this.sparkDefaultProp = prop;
        this.sparkK8sConfig = PublicUtil.jsonStrToObject(PublicUtil.objToString(prop), SparkK8sConfig.class);
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return submitSqlJob(jobClient);
    }

    private JobResult submitSqlJob(JobClient jobClient) {
        ComputeType computeType = jobClient.getComputeType();
        if (computeType == null) {
            throw new RdosDefineException("need to set compute type.");
        }

        switch (computeType) {
            case BATCH:
                return submitSparkSqlJobForBatch(jobClient);
            default:
                //do nothing
        }
        throw new RdosDefineException("not support for compute type :" + computeType);
    }

    private JobResult submitSparkSqlJobForBatch(JobClient jobClient) {
        String sqlJobArgs = buildSparkSqlJobParams(jobClient);

        List<String> argList = new ArrayList<>();
        argList.add("--primary-java-resource");
        argList.add(sparkK8sConfig.getSparkSqlProxyPath());
        argList.add("--main-class");
        argList.add(sparkK8sConfig.getSparkSqlProxyMainClass());
        argList.add("--arg");
        argList.add(sqlJobArgs);

        DtKubernetesClientApplication k8sClientApp = new DtKubernetesClientApplication();
        ClientArguments clientArguments = ClientArguments.fromCommandLineArgs(argList.toArray(new String[argList.size()]));

        Properties confProp = jobClient.getConfProperties();
        SparkConf sparkConf = SparkConfigUtil.buildBasicSparkConf(sparkDefaultProp);
        SparkConfigUtil.replaceBasicSparkConf(sparkConf, confProp);
        SparkConfigUtil.buildHadoopSparkConf(sparkConf, sparkK8sConfig);

        sparkConf.setAppName(jobClient.getJobName());

        try {
            String appId = k8sClientApp.run(clientArguments, sparkConf);
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }
    }

    private String buildSparkSqlJobParams(JobClient jobClient) {
        Properties confProp = jobClient.getConfProperties();
        String zipSql = DtStringUtil.zip(jobClient.getSql());
        String logLevel = MathUtil.getString(confProp.get(LOG_LEVEL_KEY));

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("sql", zipSql);
        paramsMap.put("appName", jobClient.getJobName());
        paramsMap.put("sparkSessionConf", SparkConfigUtil.getSparkSessionConf(confProp));

        if (StringUtils.isNotEmpty(logLevel)) {
            paramsMap.put("logLevel", logLevel);
        }

        String sqlExeJson = null;
        try {
            sqlExeJson = PublicUtil.objToString(paramsMap);
            sqlExeJson = URLEncoder.encode(sqlExeJson, Charsets.UTF_8.name());
        } catch (Exception e) {
            logger.error("", e);
            throw new RdosDefineException("get unexpected exception:" + e.getMessage());
        }
        return sqlExeJson;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        // TODO 创建KubernetesClient
        KubernetesClient client = new DefaultKubernetesClient();

        List<Pod> items = client.pods()
                .withLabel("spark-app-selector", jobIdentifier.getApplicationId())
                .list()
                .getItems();

        if (items.size() > 0) {
            String phase = items.get(0).getStatus().getPhase();

            switch (phase) {
//                case KILLED:
//                    return RdosTaskStatus.KILLED;
//                case NEW:
//                case NEW_SAVING:
//                    return RdosTaskStatus.CREATED;
//                case SUBMITTED:
//                    //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
//                    return RdosTaskStatus.WAITCOMPUTE;
//                case ACCEPTED:
//                    return RdosTaskStatus.SCHEDULED;
//                case RUNNING:
//                    return RdosTaskStatus.RUNNING;
//                case FINISHED:
//                    //state 为finished状态下需要兼顾判断finalStatus.
//                    FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
//                    if (finalApplicationStatus == FinalApplicationStatus.FAILED) {
//                        return RdosTaskStatus.FAILED;
//                    } else if (finalApplicationStatus == FinalApplicationStatus.SUCCEEDED) {
//                        return RdosTaskStatus.FINISHED;
//                    } else if (finalApplicationStatus == FinalApplicationStatus.KILLED) {
//                        return RdosTaskStatus.KILLED;
//                    } else {
//                        return RdosTaskStatus.RUNNING;
//                    }
//
//                case FAILED:
//                    return RdosTaskStatus.FAILED;
                default:
                    throw new RdosDefineException("Unsupported application state");
            }
        }
        return null;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }

}
