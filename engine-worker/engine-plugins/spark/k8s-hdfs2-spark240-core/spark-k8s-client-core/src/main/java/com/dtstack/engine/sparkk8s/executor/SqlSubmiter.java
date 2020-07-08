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
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.sparkk8s.config.SparkK8sConfig;
import com.dtstack.engine.sparkk8s.utils.SparkConfigUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.k8s.submit.ClientArguments;
import org.apache.spark.deploy.k8s.submit.DtKubernetesClientApplication;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Date: 2020/7/8
 * Company: www.dtstack.com
 * @author maqi
 */
public class SqlSubmiter implements SparkSubmit {
    private static final Logger LOG = LoggerFactory.getLogger(SqlSubmiter.class);

    private final JobClient jobClient;
    private Properties sparkDefaultProp;
    private String hdfsConfPath;
    private SparkK8sConfig sparkK8sConfig;

    public SqlSubmiter(JobClient jobClient, SparkK8sConfig sparkK8sConfig, Properties sparkDefaultProp, String hdfsConfPath) {
        this.jobClient = jobClient;
        this.sparkDefaultProp = sparkDefaultProp;
        this.hdfsConfPath = hdfsConfPath;
        this.sparkK8sConfig = sparkK8sConfig;
    }

    @Override
    public JobResult submit() {
        String sqlJobArgs = buildJobParams();

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
        SparkConfigUtil.buildHadoopSparkConf(sparkConf, hdfsConfPath);
        // operator hdfs
        SparkConfigUtil.setHadoopUserName(sparkK8sConfig, sparkConf);

        sparkConf.setAppName(jobClient.getJobName());

        try {
            String appId = k8sClientApp.run(clientArguments, sparkConf);
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }
    }

    @Override
    public String buildJobParams() {
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
            LOG.error("", e);
            throw new RdosDefineException("get unexpected exception:" + e.getMessage());
        }
        return sqlExeJson;
    }

}
