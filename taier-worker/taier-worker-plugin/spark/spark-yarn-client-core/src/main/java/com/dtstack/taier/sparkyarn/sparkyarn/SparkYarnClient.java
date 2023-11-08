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

package com.dtstack.taier.sparkyarn.sparkyarn;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.base.monitor.AcceptedApplicationMonitor;
import com.dtstack.taier.base.util.HadoopConfTool;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.base.util.Splitter;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.JobParam;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.http.PoolHttpClient;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.DtStringUtil;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.sparkyarn.sparkext.ClientExt;
import com.dtstack.taier.sparkyarn.sparkext.ClientExtFactory;
import com.dtstack.taier.sparkyarn.sparkyarn.constant.AppEnvConstant;
import com.dtstack.taier.sparkyarn.sparkyarn.constant.SparkConstants;
import com.dtstack.taier.sparkyarn.sparkyarn.file.SparkResourceUploader;
import com.dtstack.taier.sparkyarn.sparkyarn.parser.AddJarOperator;
import com.dtstack.taier.sparkyarn.sparkyarn.util.HadoopConf;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(SparkYarnClient.class);

    private static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";

    private static final String SPARK_YARN_MODE = "SPARK_YARN_MODE";

    private static final String SESSION_CONF_KEY_PREFIX = "session.";

    private static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";

    private static final String DEFAULT_FILE_FORMAT = "orc";

    private static final String LOG_LEVEL_KEY = "logLevel";

    private static final String HDFS_PREFIX = "hdfs://";

    private static final String HTTP_PREFIX = "http://";

    private static final String KEY_PRE_STR = "spark.";

    private static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";

    private static final String PYTHON_RUNNER_DEPENDENCY_RES_KEY = "extRefResource";

    private static final String CLUSTER_INFO_WS_FORMAT = "%s/ws/v1/cluster";

    /**
     * 如果请求 CLUSTER_INFO_WS_FORMAT 返回信息包含该特征则表示是alive
     */
    private static final String ALIVE_WEB_FLAG = "clusterInfo";

    private List<String> webAppAddrList = Lists.newArrayList();

    private SparkYarnConfig sparkYarnConfig;

    private YarnConfiguration yarnConf;

    private volatile YarnClient yarnClient;

    private Properties sparkExtProp;

    private FilesystemManager filesystemManager;

    private ThreadPoolExecutor threadPoolExecutor;

    public static final String SPARK_LOG4J_FILE_NAME = "log4j-spark.properties";

    public static final String SPARK_LOCAL_LOG4J_KEY = "spark_local_log4j_key";

    @Override
    public void init(Properties prop) throws Exception {
        this.sparkExtProp = prop;
        String propStr = PublicUtil.objToString(prop);
        sparkYarnConfig = PublicUtil.jsonStrToObject(propStr, SparkYarnConfig.class);
        setHadoopUserName(sparkYarnConfig);
        initYarnConf(sparkYarnConfig);
        sparkYarnConfig.setDefaultFs(yarnConf.get(HadoopConfTool.FS_DEFAULTFS));
        System.setProperty(SPARK_YARN_MODE, "true");
        parseWebAppAddr();
        logger.info("UGI info: " + UserGroupInformation.getCurrentUser());
        yarnClient = this.buildYarnClient();

        this.filesystemManager = new FilesystemManager(yarnConf, sparkYarnConfig.getSftpConf());

        if (sparkYarnConfig.getMonitorAcceptedApp()) {
            AcceptedApplicationMonitor.start(yarnConf, sparkYarnConfig.getQueue(), sparkYarnConfig);
        }

        SparkResourceUploader sparkResourceUploader =
                new SparkResourceUploader(
                        yarnConf, sparkYarnConfig, sparkExtProp, filesystemManager);
        sparkResourceUploader.uploadSparkResource();

        this.threadPoolExecutor = new ThreadPoolExecutor(sparkYarnConfig.getAsyncCheckYarnClientThreadNum(), sparkYarnConfig.getAsyncCheckYarnClientThreadNum(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("spark_yarnclient"));

    }

    private void initYarnConf(SparkYarnConfig sparkConfig) {
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(sparkConfig.getHadoopConf());
        customerConf.initYarnConf(sparkConfig.getYarnConf());

        if (sparkYarnConfig.isOpenKerberos() && MapUtils.isNotEmpty(sparkConfig.getHiveConf())) {
            customerConf.initHiveSecurityConf(sparkConfig.getHiveConf());
        }

        yarnConf = customerConf.getYarnConfiguration();
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        try {
            return KerberosUtils.login(sparkYarnConfig, () -> {
                EJobType jobType = jobClient.getJobType();
                JobResult jobResult = null;
                if (EJobType.MR.equals(jobType)) {
                    jobResult = submitJobWithJar(jobClient);
                } else if (EJobType.SQL.equals(jobType)) {
                    jobResult = submitSqlJob(jobClient);
                } else if (EJobType.PYTHON.equals(jobType)) {
                    jobResult = submitPythonJob(jobClient);
                }
                return jobResult;
            }, yarnConf, true);
        } catch (Exception e) {
            logger.info("", e);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(e));
        }
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        setHadoopUserName(sparkYarnConfig);
        JobParam jobParam = new JobParam(jobClient);
        String mainClass = jobParam.getMainClass();
        //只支持hdfs
        String jarPath = jobParam.getJarPath();
        String appName = jobParam.getJobName();
        String exeArgsStr = jobParam.getClassArgs();

        if (!jarPath.startsWith(HDFS_PREFIX)) {
            throw new PluginDefineException("spark jar path protocol must be " + HDFS_PREFIX);
        }

        if (Strings.isNullOrEmpty(appName)) {
            throw new PluginDefineException("spark jar must set app name!");
        }

        String[] appArgs = new String[]{};
        if (StringUtils.isNotBlank(exeArgsStr)) {
            appArgs = exeArgsStr.split("\\s+");
        }

        List<String> argList = new ArrayList<>();
        argList.add("--jar");
        argList.add(jarPath);
        argList.add("--class");
        argList.add(mainClass);

        for (String appArg : appArgs) {
            if (StringUtils.isBlank(appArg)) {
                continue;
            }
            argList.add("--arg");
            argList.add(appArg);
        }

        ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
        SparkConf sparkConf = buildBasicSparkConf(jobClient);
        sparkConf.setAppName(appName);
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());
        setSparkLog4j(jobClient,sparkConf);

        ApplicationId appId = null;

        try {
            ClientExt clientExt = ClientExtFactory.getClientExt(clientArguments, yarnConf, sparkConf, yarnClient);
            String proxyUserName = sparkYarnConfig.getDtProxyUserName();
            if (StringUtils.isNotBlank(proxyUserName)) {
                logger.info("jobId {} ugi proxyUser is {}", jobClient.getJobId(), proxyUserName);
                appId = UserGroupInformation.createProxyUser(proxyUserName, UserGroupInformation.getLoginUser()).doAs((PrivilegedExceptionAction<ApplicationId>) () -> clientExt.submitApplication(jobClient.getApplicationPriority()));
            } else {
                appId = clientExt.submitApplication(jobClient.getApplicationPriority());
            }
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            logger.info("", ex);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    private JobResult submitPythonJob(JobClient jobClient) {
        setHadoopUserName(sparkYarnConfig);
        JobParam jobParam = new JobParam(jobClient);
        //.py .egg .zip 存储的hdfs路径
        String pyFilePath = jobParam.getJarPath();
        String appName = jobParam.getJobName();
        String exeArgsStr = jobParam.getClassArgs();

        if (Strings.isNullOrEmpty(pyFilePath)) {
            return JobResult.createErrorResult("exe python file can't be null.");
        }

        if (Strings.isNullOrEmpty(appName)) {
            return JobResult.createErrorResult("an application name must be set in your configuration");
        }
        ApplicationId appId = null;

        List<String> argList = new ArrayList<>();
        argList.add("--primary-py-file");
        argList.add(pyFilePath);

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
                nextIsDependencyVal = false;
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

        String pythonExtPath = sparkYarnConfig.getSparkPythonExtLibPath();
        if (Strings.isNullOrEmpty(pythonExtPath)) {
            return JobResult.createErrorResult("engine node.yml setting error, " +
                    "commit spark python job need to set param of sparkPythonExtLibPath.");
        }

        //添加自定义的依赖包
        if (!Strings.isNullOrEmpty(dependencyResource)) {
            pythonExtPath = pythonExtPath + "," + dependencyResource;
        }

        SparkConf sparkConf = buildBasicSparkConf(jobClient);

        // set  spark executor env.
        List<String> args = Arrays.asList(appArgs);
        int appEnvIndex = args.indexOf(AppEnvConstant.APP_ENV);
        if (appEnvIndex != -1) {
            try {
                String appEnv = args.get(appEnvIndex + 1);
                parseAppEnv(appEnv, sparkConf);
            } catch (Exception e) {
                return JobResult.createErrorResult("Could't set appEnv to spark executor env. parsePythonCmd failed. " +
                        "Reason :" + e.getMessage());
            }
        }

        sparkConf.set("spark.submit.pyFiles", pythonExtPath);
        sparkConf.setAppName(appName);
        fillExtSparkConf(sparkConf, jobClient.getConfProperties());
        setSparkLog4jConfiguration(sparkConf);

        try {
            ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
            ClientExt clientExt = new ClientExt(clientArguments, yarnConf, sparkConf, yarnClient);
            String proxyUserName = sparkYarnConfig.getDtProxyUserName();
            if (StringUtils.isNotBlank(proxyUserName)) {
                logger.info("ugi proxyUser is {}", proxyUserName);
                appId = UserGroupInformation.createProxyUser(proxyUserName, UserGroupInformation.getLoginUser()).doAs((PrivilegedExceptionAction<ApplicationId>) () -> clientExt.submitApplication(jobClient.getApplicationPriority()));
            } else {
                appId = clientExt.submitApplication(jobClient.getApplicationPriority());
            }
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            logger.info("", ex);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }
    }

    private void parseAppEnv(String appEnv, SparkConf sparkConf) {
        addEnv2SparkConf(appEnv, sparkConf);
    }

    /**
     * 1.parse cmd.
     * 2.add key-value into container envs.
     *
     * @param cmdStr
     */
    private void addEnv2SparkConf(String cmdStr, SparkConf sparkConf) {
        if (cmdStr == null) {
            return;
        }

        try {
            // envJson is encode, we need decode it.
            cmdStr = URLDecoder.decode(cmdStr, "UTF-8");
            logger.info("cmdStr decoded is : " + cmdStr);
            Map<String, Object> envMap = JSON.parseObject(cmdStr.trim());
            Iterator entries = envMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                String value;
                if (AppEnvConstant.MODEL_PARAM.equals(key)) {
                    value = URLEncoder.encode((String) entry.getValue(), "UTF-8");
                } else {
                    value = (String) entry.getValue();
                }
                //add prefix for app env, make it easier to recognize.
                sparkConf.setExecutorEnv(key, value);
            }
        } catch (Exception e) {
            String message = String.format("Could't parse {%s} to json format. Reason : {%s}", cmdStr, e.getMessage());
            logger.error(message);
            throw new PluginDefineException(message, e);
        }
    }

    /**
     * 执行spark 批处理sql
     *
     * @param jobClient
     * @return
     */
    private JobResult submitSparkSqlJobForBatch(JobClient jobClient) {

        Properties confProp = jobClient.getConfProperties();
        setHadoopUserName(sparkYarnConfig);
        Map<String, Object> paramsMap = new HashMap<>();

        String zipSql = DtStringUtil.zip(jobClient.getSql());
        paramsMap.put("sql", zipSql);
        paramsMap.put("appName", jobClient.getJobName());
        paramsMap.put("sparkSessionConf", getSparkSessionConf(confProp));

        String logLevel = MathUtil.getString(confProp.get(LOG_LEVEL_KEY));
        if (StringUtils.isNotEmpty(logLevel)) {
            paramsMap.put("logLevel", logLevel);
        }


        String sqlExeJson = null;
        try {
            sqlExeJson = PublicUtil.objToString(paramsMap);
            sqlExeJson = URLEncoder.encode(sqlExeJson, Charsets.UTF_8.name());
        } catch (Exception e) {
            logger.error("", e);
            throw new PluginDefineException("get unexpected exception:" + e.getMessage());
        }

        String sqlProxyClass = sparkYarnConfig.getSparkSqlProxyMainClass();

        List<String> argList = new ArrayList<>();
        argList.add("--jar");
        argList.add(sparkYarnConfig.getSparkSqlProxyPath());
        argList.add("--class");
        argList.add(sqlProxyClass);
        argList.add("--arg");
        argList.add(sqlExeJson);

        ClientArguments clientArguments = new ClientArguments(argList.toArray(new String[argList.size()]));
        SparkConf sparkConf = buildBasicSparkConf(jobClient);
        sparkConf.setAppName(jobClient.getJobName());
        setSparkLog4j(jobClient, sparkConf);
        fillExtSparkConf(sparkConf, confProp);
        setSparkLog4jConfiguration(sparkConf);

        ApplicationId appId = null;

        try {
            ClientExt clientExt = ClientExtFactory.getClientExt(clientArguments, yarnConf, sparkConf, yarnClient);
            String proxyUserName = sparkYarnConfig.getDtProxyUserName();
            if (StringUtils.isNotBlank(proxyUserName)) {
                logger.info("ugi proxyUser is {}", proxyUserName);
                appId = UserGroupInformation.createProxyUser(proxyUserName, UserGroupInformation.getLoginUser()).doAs((PrivilegedExceptionAction<ApplicationId>) () -> clientExt.submitApplication(jobClient.getApplicationPriority()));
            } else {
                appId = clientExt.submitApplication(jobClient.getApplicationPriority());
            }
            return JobResult.createSuccessResult(appId.toString());
        } catch (Exception ex) {
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }

    }

    private Map<String, String> getSparkSessionConf(Properties confProp) {
        Map<String, String> map = Maps.newHashMap();
        map.put(KEY_DEFAULT_FILE_FORMAT, DEFAULT_FILE_FORMAT);

        if (confProp == null || confProp.isEmpty()) {
            return map;
        }

        for (Map.Entry<Object, Object> param : confProp.entrySet()) {
            String key = (String) param.getKey();
            String val = (String) param.getValue();
            if (key.startsWith(SESSION_CONF_KEY_PREFIX)) {
                key = key.replaceFirst("session\\.", "");
                map.put(key, val);
            }
        }

        return map;
    }


    private SparkConf buildBasicSparkConf(JobClient jobClient) {

        SparkConf sparkConf = new SparkConf();
        sparkConf.remove("spark.jars");
        sparkConf.remove("spark.files");
        sparkConf.set("spark.yarn.archive", sparkYarnConfig.getSparkYarnArchive());
        sparkConf.set("spark.yarn.queue", sparkYarnConfig.getQueue());
        sparkConf.set("security", "false");

        if (sparkYarnConfig.isOpenKerberos()) {
            String[] kerberosFiles = KerberosUtils.getKerberosFile(sparkYarnConfig, null);
            String keytab = kerberosFiles[0];
            String principal = StringUtils.isNotBlank(sparkYarnConfig.getPrincipal()) ?
                    sparkYarnConfig.getPrincipal() : KerberosUtils.getPrincipal(keytab);
            sparkConf.set("spark.yarn.keytab", keytab);
            sparkConf.set("spark.yarn.principal", principal);
            sparkConf.set("security", String.valueOf(sparkYarnConfig.isOpenKerberos()));
        }
        if (sparkExtProp != null) {
            sparkExtProp.forEach((key, value) -> {
                if (key.toString().contains(".")) {
                    sparkConf.set(key.toString(), value.toString());
                }
            });
        }
        return sparkConf;
    }

    private void setSparkLog4jConfiguration(SparkConf sparkConf) {
        String localPath = sparkConf.get(SPARK_LOCAL_LOG4J_KEY, "");
        if (StringUtils.isBlank(localPath)) {
            return;
        }
        String configuration = "-Dlog4j.configuration=" + SPARK_LOG4J_FILE_NAME;
        String driverExtraJavaOptions = sparkConf.get("spark.driver.extraJavaOptions", "");
        if (StringUtils.isBlank(driverExtraJavaOptions)) {
            sparkConf.set("spark.driver.extraJavaOptions", configuration);
        } else {
            sparkConf.set("spark.driver.extraJavaOptions", driverExtraJavaOptions + " " + configuration);
        }
        String executorExtraJavaOptions = sparkConf.get("spark.executor.extraJavaOptions", "");
        if (StringUtils.isBlank(executorExtraJavaOptions)) {
            sparkConf.set("spark.executor.extraJavaOptions", configuration);
        } else {
            sparkConf.set("spark.executor.extraJavaOptions", executorExtraJavaOptions + " " + configuration);
        }
    }

    /**
     * 通过提交的paramsOperator 设置sparkConf
     * 解析传递过来的参数不带spark.前面缀的
     *
     * @param sparkConf
     * @param confProperties
     */
    private void fillExtSparkConf(SparkConf sparkConf, Properties confProperties) {

        if (confProperties == null) {
            return;
        }

        for (Map.Entry<Object, Object> param : confProperties.entrySet()) {
            String key = (String) param.getKey();
            String val = (String) param.getValue();
            if (!key.contains(KEY_PRE_STR)) {
                key = KEY_PRE_STR + key;
            }
            sparkConf.set(key, val);
        }
    }


    private JobResult submitSparkSqlJobForStream(JobClient jobClient) {
        throw new PluginDefineException("not support spark sql job for stream type.");
    }

    private JobResult submitSqlJob(JobClient jobClient) {

        ComputeType computeType = jobClient.getComputeType();
        if (computeType == null) {
            throw new PluginDefineException("need to set compute type.");
        }

        switch (computeType) {
            case BATCH:
                return submitSparkSqlJobForBatch(jobClient);
            case STREAM:
                return submitSparkSqlJobForStream(jobClient);
            default:
                //do nothing

        }

        throw new PluginDefineException("not support for compute type :" + computeType);

    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(sparkYarnConfig, () -> {
                String jobId = jobIdentifier.getApplicationId();
                try {
                    ApplicationId appId = ConverterUtils.toApplicationId(jobId);
                    getYarnClient().killApplication(appId);
                    return JobResult.createSuccessResult(jobId);
                } catch (Exception e) {
                    logger.error("", e);
                    return JobResult.createErrorResult(e.getMessage());
                }
            }, yarnConf, true);
        } catch (Exception e) {
            logger.error("cancelJob error:", e);
            return JobResult.createErrorResult(e);
        }
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return KerberosUtils.login(sparkYarnConfig, () -> {
                String jobId = jobIdentifier.getApplicationId();

                if (StringUtils.isEmpty(jobId)) {
                    return null;
                }

                ApplicationId appId = ConverterUtils.toApplicationId(jobId);
                try {
                    ApplicationReport report = getYarnClient().getApplicationReport(appId);
                    YarnApplicationState applicationState = report.getYarnApplicationState();
                    switch (applicationState) {
                        case KILLED:
                            return TaskStatus.KILLED;
                        case NEW:
                        case NEW_SAVING:
                            return TaskStatus.CREATED;
                        case SUBMITTED:
                            //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                            return TaskStatus.WAITCOMPUTE;
                        case ACCEPTED:
                            return TaskStatus.SCHEDULED;
                        case RUNNING:
                            return TaskStatus.RUNNING;
                        case FINISHED:
                            //state 为finished状态下需要兼顾判断finalStatus.
                            FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
                            if (finalApplicationStatus == FinalApplicationStatus.FAILED) {
                                return TaskStatus.FAILED;
                            } else if (finalApplicationStatus == FinalApplicationStatus.SUCCEEDED) {
                                return TaskStatus.FINISHED;
                            } else if (finalApplicationStatus == FinalApplicationStatus.KILLED) {
                                return TaskStatus.KILLED;
                            } else {
                                return TaskStatus.RUNNING;
                            }

                        case FAILED:
                            return TaskStatus.FAILED;
                        default:
                            throw new PluginDefineException("Unsupported application state");
                    }
                } catch (Exception e) {
                    logger.error("", e);
                    return TaskStatus.NOTFOUND;
                }
            }, yarnConf, false);
        } catch (Exception e) {
            logger.error("", e);
            return TaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        //解析config,获取web-address
        String aliveWebAddr = null;
        for (String addr : webAppAddrList) {
            String response = null;
            String reqUrl = String.format(CLUSTER_INFO_WS_FORMAT, addr);
            try {
                response = PoolHttpClient.get(reqUrl);
                if (response.contains(ALIVE_WEB_FLAG)) {
                    aliveWebAddr = addr;
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (!Strings.isNullOrEmpty(aliveWebAddr)) {
            webAppAddrList.remove(aliveWebAddr);
            webAppAddrList.add(0, aliveWebAddr);
        }

        return aliveWebAddr;
    }


    private void parseWebAppAddr() {
        Iterator<Map.Entry<String, String>> iterator = yarnConf.iterator();
        List<String> tmpWebAppAddr = Lists.newArrayList();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.contains("yarn.resourcemanager.webapp.address.")) {
                if (!value.startsWith(HTTP_PREFIX)) {
                    value = HTTP_PREFIX + value.trim();
                }
                tmpWebAppAddr.add(value);
            } else if (key.startsWith("yarn.resourcemanager.hostname.")) {
                String rm = key.substring("yarn.resourcemanager.hostname.".length());
                String addressKey = "yarn.resourcemanager.address." + rm;

                webAppAddrList.add(HTTP_PREFIX + value + ":" + YarnConfiguration.DEFAULT_RM_WEBAPP_PORT);
                if (yarnConf.get(addressKey) == null) {
                    yarnConf.set(addressKey, value + ":" + YarnConfiguration.DEFAULT_RM_PORT);
                }
            }
        }

        if (tmpWebAppAddr.size() != 0) {
            webAppAddrList = tmpWebAppAddr;
        }
    }

    @Override
    public String getMessageByHttp(String path) {
        String reqUrl = path;
        if (!path.startsWith(HTTP_PREFIX)) {
            reqUrl = String.format("%s%s", getJobMaster(null), path);
        }

        try {
            return PoolHttpClient.get(reqUrl);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        SparkJobLog sparkJobLog = new SparkJobLog();
        try {
            return KerberosUtils.login(sparkYarnConfig, () -> {
                String jobId = jobIdentifier.getApplicationId();
                ApplicationId applicationId = ConverterUtils.toApplicationId(jobId);

                try {
                    ApplicationReport applicationReport = getYarnClient().getApplicationReport(applicationId);
                    String msgInfo = applicationReport.getDiagnostics();
                    sparkJobLog.addAppLog(jobId, msgInfo);
                } catch (Exception e) {
                    logger.error("", e);
                    sparkJobLog.addAppLog(jobId, "get log from yarn err:" + e.getMessage());
                }

                return sparkJobLog.toString();
            }, yarnConf, false);
        } catch (Exception e) {
            logger.error("", e);
            sparkJobLog.addAppLog(jobIdentifier.getEngineJobId(), "get log from yarn err:" + e.getMessage());
            return sparkJobLog.toString();
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        try {
            return KerberosUtils.login(sparkYarnConfig, () -> {
                SparkYarnResourceInfo resourceInfo = SparkYarnResourceInfo.SparkYarnResourceInfoBuilder()
                        .withYarnClient(getYarnClient())
                        .withQueueName(sparkYarnConfig.getQueue())
                        .withYarnAccepterTaskNumber(sparkYarnConfig.getYarnAccepterTaskNumber())
                        .build();
                return resourceInfo.judgeSlots(jobClient);
            }, yarnConf, false);
        } catch (Exception e) {
            logger.error("jobId:{} judgeSlots error:", jobClient.getJobId(), e);
            return JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public void setHadoopUserName(SparkYarnConfig sparkYarnConfig) {
        if (Strings.isNullOrEmpty(sparkYarnConfig.getHadoopUserName())) {
            return;
        }

        UserGroupInformation.setThreadLocalData(HADOOP_USER_NAME, sparkYarnConfig.getHadoopUserName());
    }

    @Override
    public void beforeSubmitFunc(JobClient jobClient) {
        String sql = jobClient.getSql();
        Splitter splitter = new Splitter(';');
        List<String> sqlArr = splitter.splitEscaped(sql);
        if (sqlArr.size() == 0) {
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlItera = sqlList.iterator();

        while (sqlItera.hasNext()) {
            String tmpSql = sqlItera.next();
            // handle add jar statements and comment statements on the same line
            tmpSql = AddJarOperator.handleSql(tmpSql);
            if (AddJarOperator.verific(tmpSql)) {
                sqlItera.remove();
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);

                if (jobClient.getJobType() == EJobType.SQL) {
                    //SQL当前不允许提交jar包,自定义函数已经在web端处理了。
                } else {
                    //非sql任务只允许提交一个附件包
                    jobClient.setCoreJarInfo(jarFileInfo);
                    break;
                }
            }
        }

        jobClient.setSql(String.join(";", sqlList));
    }

    public YarnClient getYarnClient() {
        long startTime = System.currentTimeMillis();
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        logger.info("buildYarnClient!");
                        YarnClient yarnClient1 = YarnClient.createYarnClient();
                        yarnClient1.init(yarnConf);
                        yarnClient1.start();
                        yarnClient = yarnClient1;
                    }
                }
            } else {
                //异步超时判断下是否可用，kerberos 开启下会出现hang死情况
                RetryUtil.asyncExecuteWithRetry(() -> yarnClient.getAllQueues(),
                        1,
                        0,
                        false,
                        30000L,
                        threadPoolExecutor);
            }
        } catch (Throwable e) {
            logger.error("buildYarnClient![backup]", e);
            YarnClient yarnClient1 = YarnClient.createYarnClient();
            yarnClient1.init(yarnConf);
            yarnClient1.start();
            yarnClient = yarnClient1;
        } finally {
            if (logger.isDebugEnabled()) {
                long endTime = System.currentTimeMillis();
                logger.debug("cost getYarnClient start-time:{} end-time:{}, cost:{}.", startTime, endTime, endTime - startTime);
            }
        }
        return yarnClient;
    }

    /**
     * 创建YarnClient 增加KerberosUtils 逻辑
     *
     * @return
     */
    private YarnClient buildYarnClient() {
        try {
            return KerberosUtils.login(sparkYarnConfig, () -> {
                logger.info("buildYarnClient, init YarnClient!");
                YarnClient yarnClient1 = YarnClient.createYarnClient();
                yarnClient1.init(yarnConf);
                yarnClient1.start();
                return yarnClient1;
            }, yarnConf, true);
        } catch (Exception e) {
            logger.error("buildYarnClient initSecurity happens error", e);
            throw new PluginDefineException(e);
        }
    }



    private void setSparkLog4j(JobClient jobClient, SparkConf sparkConf) {
        Properties confProp = jobClient.getConfProperties();
        String logLevel = MathUtil.getString(confProp.get(SparkConstants.LOG_LEVEL_KEY), "info");
        sparkConf.set(
                "spark.log4j.content",
                StringUtils.replace(SparkConstants.SPARK_LOG4J_CONTENT, "INFO", logLevel));
        String log4jContent = SparkConstants.SPARK_JAVA_OPTIONS_LOG4J_CONTENT;
        setSparkExtraJavaOptions(log4jContent, sparkConf);
    }

    private void setSparkExtraJavaOptions(String options, SparkConf sparkConf) {
        String driverExtraJavaOptions =
                sparkConf.get(SparkConstants.SPARK_DRIVER_EXTRA_JAVA_OPTIONS, "");
        if (StringUtils.isBlank(driverExtraJavaOptions)) {
            sparkConf.set(SparkConstants.SPARK_DRIVER_EXTRA_JAVA_OPTIONS, options);
        } else {
            sparkConf.set(
                    SparkConstants.SPARK_DRIVER_EXTRA_JAVA_OPTIONS,
                    driverExtraJavaOptions + " " + options);
        }
        String executorExtraJavaOptions =
                sparkConf.get(SparkConstants.SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS, "");
        if (StringUtils.isBlank(executorExtraJavaOptions)) {
            sparkConf.set(SparkConstants.SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS, options);
        } else {
            sparkConf.set(
                    SparkConstants.SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS,
                    executorExtraJavaOptions + " " + options);
        }
    }


}
