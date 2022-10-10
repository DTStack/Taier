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

package com.dtstack.taier.hadoop;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.base.resource.EngineResourceInfo;
import com.dtstack.taier.base.util.HadoopConfTool;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.hadoop.parser.AddJarOperator;
import com.dtstack.taier.hadoop.util.HadoopConf;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.JobParam;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.http.PoolHttpClient;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.DtStringUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class HadoopClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopClient.class);
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String TMP_PATH = USER_DIR + "/tmp";
    private static final String HDFS_PREFIX = "hdfs://";
    private static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";
    private static final String QUEUE = "queue";
    private EngineResourceInfo resourceInfo = new HadoopResourceInfo();
    private Configuration conf = new Configuration();
    private volatile YarnClient yarnClient;
    private Config config;
    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();
    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";
    private static final String YARN_SCHEDULER_FORMAT = "http://%s/ws/v1/cluster/scheduler";

    @Override
    public void init(Properties prop) throws Exception {
        LOG.info("hadoop client init...");

        String configStr = PublicUtil.objToString(prop);
        config = PublicUtil.jsonStrToObject(configStr, Config.class);
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(config.getHadoopConf());
        customerConf.initYarnConf(config.getYarnConf());
        conf = customerConf.getYarnConfiguration();

        HadoopConfTool.setFsHdfsImplDisableCache(conf);

        conf.set("mapreduce.framework.name", "yarn");
        conf.set("mapreduce.map.memory.mb", "1024");
        conf.set("mapreduce.reduce.memory.mb", "1024");
        conf.setBoolean("mapreduce.app-submission.cross-platform", true);

        setHadoopUserName(config);

        yarnClient = buildYarnClient();

        LOG.info("UGI info: " + UserGroupInformation.getCurrentUser());

    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        try {
            return KerberosUtils.login(config, () -> {
                EJobType jobType = jobClient.getJobType();
                if (EJobType.MR.equals(jobType)) {
                    return submitJobWithJar(jobClient);
                }
                return null;
            }, conf);
        } catch (Exception e) {
            LOG.error("submit error:", e);
            return JobResult.createErrorResult(e);
        }
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(config, () -> {
                String jobId = jobIdentifier.getEngineJobId();

                try {
                    getYarnClient().killApplication(generateApplicationId(jobId));
                } catch (YarnException | IOException e) {
                    return JobResult.createErrorResult(e);
                }

                JobResult jobResult = JobResult.newInstance(false);
                jobResult.setData("jobid", jobId);
                return jobResult;
            }, conf);
        } catch (Exception e) {
            LOG.error("cancelJob error:", e);
            return JobResult.createErrorResult(e);
        }
    }

    private ApplicationId generateApplicationId(String jobId) {
        String appId = jobId.replace("job_", "application_");
        return ConverterUtils.toApplicationId(appId);
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return KerberosUtils.login(config, () -> {
                String jobId = jobIdentifier.getEngineJobId();
                ApplicationId appId = generateApplicationId(jobId);

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
                    return TaskStatus.NOTFOUND;
                }
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            return TaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new PluginDefineException("hadoop client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new PluginDefineException("hadoop client not support method 'getJobMaster'");
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        try {
            setHadoopUserName(config);
            JobParam jobParam = new JobParam(jobClient);
            Map<String, Object> plugininfo = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(), Map.class);
            Configuration jobConf = fillJobConfig(jobClient, conf);
            if (plugininfo.containsKey(QUEUE)) {
                jobConf.set(MRJobConfig.QUEUE_NAME, plugininfo.get(QUEUE).toString());
            }

            MapReduceTemplate mr = new MapReduceTemplate(jobConf, jobParam);
            mr.run();
            LOG.info("mr jobId:{} jobName:{}", mr.getJobId(), jobParam.getJobName());
            return JobResult.createSuccessResult(mr.getJobId());
        } catch (Throwable ex) {
            LOG.error("", ex);
            return JobResult.createErrorResult(ex);
        }

    }

    private Configuration fillJobConfig(JobClient jobClient, Configuration conf) {
        Configuration jobConf = new Configuration(conf);
        Properties confProps = jobClient.getConfProperties();
        if (confProps != null) {
            confProps.stringPropertyNames()
                    .stream()
                    .filter(key -> key.toString().contains("."))
                    .forEach(key -> jobConf.set(key.toString(), confProps.getProperty(key)));
        }
        return jobConf;
    }

    private void downloadHdfsFile(String from, String to) throws IOException {

        try {
            KerberosUtils.login(config, () -> {
                try {
                    File toFile = new File(to);
                    if (!toFile.getParentFile().exists()) {
                        Files.createParentDirs(toFile);
                    }
                    Path hdfsFilePath = new Path(from);
                    InputStream is = FileSystem.get(conf).open(hdfsFilePath);//读取文件
                    IOUtils.copyBytes(is, new FileOutputStream(toFile), 2048, true);//保存到本地
                } catch (Exception e) {
                    throw new PluginDefineException(e);
                }
                return null;
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            throw new PluginDefineException(e);
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return KerberosUtils.login(config, () -> resourceInfo.judgeSlots(jobClient), conf);
        } catch (Exception e) {
            LOG.error("jobId:{} judgeSlots error:", jobClient.getJobId(), e);
            return JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(config, () -> {
                String jobId = jobIdentifier.getEngineJobId();

                try {
                    ApplicationReport applicationReport = getYarnClient().getApplicationReport(generateApplicationId(jobId));
                    return applicationReport.getDiagnostics();
                } catch (Exception e) {
                    LOG.error("", e);
                }

                return StringUtils.EMPTY;
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            return StringUtils.EMPTY;
        }

    }

    private void setHadoopUserName(Config config) {
        if (Strings.isNullOrEmpty(config.getHadoopUserName())) {
            return;
        }

        UserGroupInformation.afterSetHadoopUserName(config.getHadoopUserName());
    }

    public YarnClient getYarnClient() {
        long startTime = System.currentTimeMillis();
        try {
            if (yarnClient == null) {
                synchronized (this) {
                    if (yarnClient == null) {
                        LOG.info("buildYarnClient!");
                        YarnClient yarnClient1 = YarnClient.createYarnClient();
                        yarnClient1.init(conf);
                        yarnClient1.start();
                        yarnClient = yarnClient1;
                    }
                }
            } else {
                //判断下是否可用
                yarnClient.getAllQueues();
            }
        } catch (Throwable e) {
            LOG.error("buildYarnClient![backup]", e);
            YarnClient yarnClient1 = YarnClient.createYarnClient();
            yarnClient1.init(conf);
            yarnClient1.start();
            yarnClient = yarnClient1;
        } finally {
            long endTime = System.currentTimeMillis();
            LOG.info("cost getYarnClient start-time:{} end-time:{}, cost:{}.", startTime, endTime, endTime - startTime);
        }
        return yarnClient;
    }

    private YarnClient buildYarnClient() {
        try {
            return KerberosUtils.login(config, () -> {
                LOG.info("buildYarnClient, init YarnClient!");
                YarnClient yarnClient1 = YarnClient.createYarnClient();
                yarnClient1.init(conf);
                yarnClient1.start();
                yarnClient = yarnClient1;
                return yarnClient;
            }, conf);
        } catch (Exception e) {
            LOG.error("initSecurity happens error", e);
            throw new PluginDefineException(e);
        }
    }


    @Override
    public void beforeSubmitFunc(JobClient jobClient) {
        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if (sqlArr.size() == 0) {
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlItera = sqlList.iterator();
        List<String> fileList = Lists.newArrayList();

        while (sqlItera.hasNext()) {
            String tmpSql = sqlItera.next();
            // handle add jar statements and comment statements on the same line
            tmpSql = AddJarOperator.handleSql(tmpSql);
            if (AddJarOperator.verific(tmpSql)) {
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);

                String addFilePath = jarFileInfo.getJarPath();
                //只支持hdfs
                if (!addFilePath.startsWith(HDFS_PREFIX)) {
                    throw new PluginDefineException("only support hdfs protocol for jar path");
                }
                String localJarPath = TMP_PATH + File.separator + UUID.randomUUID().toString() + ".jar";
                try {
                    downloadHdfsFile(addFilePath, localJarPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                jarFileInfo.setJarPath(localJarPath);

                jobClient.setCoreJarInfo(jarFileInfo);
                fileList.add(localJarPath);

            }
        }

        cacheFile.put(jobClient.getJobId(), fileList);
    }

    @Override
    public void afterSubmitFunc(JobClient jobClient) {
        List<String> fileList = cacheFile.get(jobClient.getJobId());
        if (CollectionUtils.isEmpty(fileList)) {
            return;
        }

        //清理包含下载下来的临时jar文件
        for (String path : fileList) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }

            } catch (Exception e1) {
                LOG.error("", e1);
            }
        }
        cacheFile.remove(jobClient.getJobId());
    }


    /**
     * 测试联通性 yarn需要返回集群队列信息
     *
     * @param pluginInfo
     * @return
     */
    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult testResult = new ComponentTestResult();
        testResult.setResult(false);
        return testResult;
    }

    private ClusterResource.ResourceMetrics createResourceMetrics(
            Integer totalMem, Integer usedMem, Integer totalCores, Integer usedCores) {

        ClusterResource.ResourceMetrics metrics = new ClusterResource.ResourceMetrics();

        metrics.setTotalCores(totalCores);
        metrics.setUsedCores(usedCores);

        Double totalMemDouble = totalMem / (1024 * 1.0);
        Double totalMemNew = retainDecimal(2, totalMemDouble);
        metrics.setTotalMem(totalMemNew);

        Double usedMemDouble = usedMem / (1024 * 1.0);
        Double usedMemNew = retainDecimal(2, usedMemDouble);
        metrics.setUsedMem(usedMemNew);

        Double memRateDouble = usedMem / (totalMem * 1.0) * 100;
        Double memRate = retainDecimal(2, memRateDouble);
        metrics.setMemRate(memRate);

        Double coresRateDouble = usedCores / (totalCores * 1.0) * 100;
        Double coresRate = retainDecimal(2, coresRateDouble);
        metrics.setCoresRate(coresRate);
        return metrics;
    }

    private Double retainDecimal(Integer position, Double decimal) {
        BigDecimal retain = new BigDecimal(decimal);
        return retain.setScale(position, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private List<JSONObject> getQueueResource(YarnClient yarnClient) throws Exception {
        String webAddress = getYarnWebAddress(yarnClient);
        String schedulerUrl = String.format(YARN_SCHEDULER_FORMAT, webAddress);
        String schedulerInfoMsg = PoolHttpClient.get(schedulerUrl, null);
        JSONObject schedulerInfo = JSONObject.parseObject(schedulerInfoMsg);

        JSONObject schedulerJson = schedulerInfo.getJSONObject("scheduler");
        if (!schedulerJson.containsKey("schedulerInfo")) {
            LOG.error("get yarn queueInfo error! Miss schedulerInfo field");
            return null;
        }
        JSONObject schedulerInfoJson = schedulerJson.getJSONObject("schedulerInfo");
        if (!schedulerInfoJson.containsKey("queues")) {
            LOG.error("get yarn queueInfo error! Miss queues field");
            return null;
        }
        JSONObject queuesJson = schedulerInfoJson.getJSONObject("queues");
        List<JSONObject> modifyQueueInfos = modifyQueueInfo(null, queuesJson);
        return modifyQueueInfos;
    }

    private List<JSONObject> modifyQueueInfo(String parentName, JSONObject queueInfos) {
        List<JSONObject> queues = new ArrayList<>();
        if (!queueInfos.containsKey("queue")) {
            return null;
        }

        for (Object ob : queueInfos.getJSONArray("queue")) {
            JSONObject queueInfo = (JSONObject) ob;
            String queueName = queueInfo.getString("queueName");
            parentName = StringUtils.isBlank(parentName) ? "" : parentName + ".";
            String queueNewName = parentName + queueName;

            if (queueInfo.containsKey("queues")) {
                List<JSONObject> childQueues = modifyQueueInfo(queueNewName, queueInfo.getJSONObject("queues"));
                if (childQueues != null) {
                    queues.addAll(childQueues);
                }
            }

            queueInfo.put("queueName", queueNewName);
            if (!queueInfo.containsKey("queues")) {
                fillUser(queueInfo);
                retainCapacity(queueInfo);
                queues.add(queueInfo);
            }
        }
        return queues;
    }

    private void retainCapacity(JSONObject queueInfo) {
        Double capacity = queueInfo.getDouble("capacity");
        queueInfo.put("capacity", retainDecimal(2, capacity));

        Double usedCapacity = queueInfo.getDouble("usedCapacity");
        queueInfo.put("usedCapacity", retainDecimal(2, usedCapacity));

        Double maxCapacity = queueInfo.getDouble("maxCapacity");
        queueInfo.put("maxCapacity", retainDecimal(2, maxCapacity));

    }

    private void fillUser(JSONObject queueInfo) {
        boolean existUser = false;
        JSONObject queueUsers = queueInfo.getJSONObject("users");
        if (queueUsers == null) {
            existUser = false;
        } else {
            JSONArray users = queueUsers.getJSONArray("user");
            existUser = users == null ? false : true;
        }

        if (!existUser) {
            JSONObject userJSONObject = new JSONObject();
            userJSONObject.put("username", "admin");
            userJSONObject.put("resourcesUsed", queueInfo.getJSONObject("resourcesUsed"));
            userJSONObject.put("AMResourceUsed", queueInfo.getJSONObject("usedAMResource"));
            userJSONObject.put("userResourceLimit", queueInfo.getJSONObject("userAMResourceLimit"));
            userJSONObject.put("maxResource", queueInfo.getJSONObject("userAMResourceLimit"));
            userJSONObject.put("maxAMResource", queueInfo.getJSONObject("userAMResourceLimit"));
            List<JSONObject> users = new ArrayList<>();
            users.add(userJSONObject);
            queueInfo.put("users", users);
        } else {
            JSONArray users = queueUsers.getJSONArray("user");
            for (Object user : users) {
                JSONObject userJSONObject = (JSONObject) user;
                userJSONObject.put("maxResource", userJSONObject.getJSONObject("userResourceLimit"));
                userJSONObject.put("maxAMResource", queueInfo.getJSONObject("userAMResourceLimit"));
            }
            queueInfo.put("users", users);
        }
    }

    private String getYarnWebAddress(YarnClient yarnClient) throws Exception {
        Field rmClientField = yarnClient.getClass().getDeclaredField("rmClient");
        rmClientField.setAccessible(true);
        Object rmClient = rmClientField.get(yarnClient);

        Field hField = rmClient.getClass().getSuperclass().getDeclaredField("h");
        hField.setAccessible(true);
        //获取指定对象中此字段的值
        Object h = hField.get(rmClient);
        Object currentProxy = null;
        try {
            Field currentProxyField = h.getClass().getDeclaredField("currentProxy");
            currentProxyField.setAccessible(true);
            currentProxy = currentProxyField.get(h);
        } catch (Exception e) {
            //兼容Hadoop 2.7.3 2.6.4.91-3
            LOG.warn("get currentProxy error: ", e);
            Field proxyDescriptorField = h.getClass().getDeclaredField("proxyDescriptor");
            proxyDescriptorField.setAccessible(true);
            Object proxyDescriptor = proxyDescriptorField.get(h);
            Field currentProxyField = proxyDescriptor.getClass().getDeclaredField("proxyInfo");
            currentProxyField.setAccessible(true);
            currentProxy = currentProxyField.get(proxyDescriptor);
        }

        Field proxyInfoField = currentProxy.getClass().getDeclaredField("proxyInfo");
        proxyInfoField.setAccessible(true);
        String proxyInfoKey = (String) proxyInfoField.get(currentProxy);

        YarnConfiguration config = (YarnConfiguration) yarnClient.getConfig();
        String key = YARN_RM_WEB_KEY_PREFIX + proxyInfoKey;
        String webAddress = config.get(key);

        if (webAddress == null) {
            webAddress = config.get("yarn.resourcemanager.webapp.address");
        }
        return webAddress;
    }
}
