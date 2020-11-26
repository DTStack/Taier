package com.dtstack.engine.hadoop;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.base.util.HadoopConfTool;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.hadoop.parser.AddJarOperator;
import com.dtstack.engine.hadoop.util.HadoopConf;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

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
    private static final String APP_TYPE = "Apache Flink";
    private static final String DEFAULT_APP_NAME_PREFIX = "Flink session";
    private static final String FLINK_URL_FORMAT = "http://%s/proxy/%s/taskmanagers";
    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";
    private static final String YARN_SCHEDULER_FORMAT = "http://%s/ws/v1/cluster/scheduler";
    private static final long ONE_MEGABYTE = 1024*1024L;

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
        conf.set("yarn.scheduler.maximum-allocation-mb", "1024");
        conf.set("yarn.nodemanager.resource.memory-mb", "1024");
        conf.set("mapreduce.map.memory.mb","1024");
        conf.set("mapreduce.reduce.memory.mb","1024");
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
            return KerberosUtils.login(config, ()->{
                String jobId = jobIdentifier.getEngineJobId();

                try {
                    getYarnClient().killApplication(generateApplicationId(jobId));
                } catch (YarnException | IOException e) {
                    return JobResult.createErrorResult(e);
                }

                JobResult jobResult = JobResult.newInstance(false);
                jobResult.setData("jobid", jobId);
                return jobResult;
            },conf);
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
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        try {
            return KerberosUtils.login(config, ()->{
                String jobId = jobIdentifier.getEngineJobId();
                ApplicationId appId = generateApplicationId(jobId);

                try {
                    ApplicationReport report = getYarnClient().getApplicationReport(appId);
                    YarnApplicationState applicationState = report.getYarnApplicationState();
                    switch(applicationState) {
                        case KILLED:
                            return RdosTaskStatus.KILLED;
                        case NEW:
                        case NEW_SAVING:
                            return RdosTaskStatus.CREATED;
                        case SUBMITTED:
                            //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                            return RdosTaskStatus.WAITCOMPUTE;
                        case ACCEPTED:
                            return RdosTaskStatus.SCHEDULED;
                        case RUNNING:
                            return RdosTaskStatus.RUNNING;
                        case FINISHED:
                            //state 为finished状态下需要兼顾判断finalStatus.
                            FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
                            if(finalApplicationStatus == FinalApplicationStatus.FAILED){
                                return RdosTaskStatus.FAILED;
                            }else if(finalApplicationStatus == FinalApplicationStatus.SUCCEEDED){
                                return RdosTaskStatus.FINISHED;
                            }else if(finalApplicationStatus == FinalApplicationStatus.KILLED){
                                return RdosTaskStatus.KILLED;
                            }else{
                                return RdosTaskStatus.RUNNING;
                            }

                        case FAILED:
                            return RdosTaskStatus.FAILED;
                        default:
                            throw new RdosDefineException("Unsupported application state");
                    }
                } catch (Exception e) {
                    return RdosTaskStatus.NOTFOUND;
                }
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new RdosDefineException("hadoop client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosDefineException("hadoop client not support method 'getJobMaster'");
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        try {
            setHadoopUserName(config);
            JobParam jobParam = new JobParam(jobClient);
            Map<String, Object> plugininfo = PublicUtil.jsonStrToObject(jobClient.getPluginInfo(),Map.class);
            Configuration jobConf = new Configuration(conf);
            if(plugininfo.containsKey(QUEUE)){
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

    private void downloadHdfsFile(String from, String to) throws IOException {

        try {
            KerberosUtils.login(config, ()-> {
                try {
                    File toFile = new File(to);
                    if(!toFile.getParentFile().exists()){
                        Files.createParentDirs(toFile);
                    }
                    Path hdfsFilePath = new Path(from);
                    InputStream is= FileSystem.get(conf).open(hdfsFilePath);//读取文件
                    IOUtils.copyBytes(is, new FileOutputStream(toFile),2048, true);//保存到本地
                } catch (Exception e) {
                    throw new RdosDefineException(e);
                }
                return null;
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e);
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return KerberosUtils.login(config, () -> resourceInfo.judgeSlots(jobClient), conf);
        } catch (Exception e) {
            LOG.error("jobId:{} judgeSlots error:", jobClient.getTaskId(), e);
            return JudgeResult.notOk("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(config, ()-> {
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

    private void setHadoopUserName(Config config){
        if(Strings.isNullOrEmpty(config.getHadoopUserName())){
            return;
        }

        UserGroupInformation.afterSetHadoopUserName(config.getHadoopUserName());
    }

    public YarnClient getYarnClient(){
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
            long endTime= System.currentTimeMillis();
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
            throw new RdosDefineException(e);
        }
    }


    @Override
    public void beforeSubmitFunc(JobClient jobClient) {
        String sql = jobClient.getSql();
        List<String> sqlArr = DtStringUtil.splitIgnoreQuota(sql, ';');
        if(sqlArr.size() == 0){
            return;
        }

        List<String> sqlList = Lists.newArrayList(sqlArr);
        Iterator<String> sqlItera = sqlList.iterator();
        List<String> fileList = Lists.newArrayList();

        while (sqlItera.hasNext()){
            String tmpSql = sqlItera.next();
            if(AddJarOperator.verific(tmpSql)){
                JarFileInfo jarFileInfo = AddJarOperator.parseSql(tmpSql);

                String addFilePath = jarFileInfo.getJarPath();
                //只支持hdfs
                if(!addFilePath.startsWith(HDFS_PREFIX)) {
                    throw new RdosDefineException("only support hdfs protocol for jar path");
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

        cacheFile.put(jobClient.getTaskId(), fileList);
    }

    @Override
    public void afterSubmitFunc(JobClient jobClient) {
        List<String> fileList = cacheFile.get(jobClient.getTaskId());
        if(CollectionUtils.isEmpty(fileList)){
            return;
        }

        //清理包含下载下来的临时jar文件
        for(String path : fileList){
            try{
                File file = new File(path);
                if(file.exists()){
                    file.delete();
                }

            }catch (Exception e1){
                LOG.error("", e1);
            }
        }
        cacheFile.remove(jobClient.getTaskId());
    }


    /**
     * 测试联通性 yarn需要返回集群队列信息
     * @param pluginInfo
     * @return
     */
    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult testResult = new ComponentTestResult();
        testResult.setResult(false);
        try {
            Config allConfig = PublicUtil.jsonStrToObject(pluginInfo, Config.class);
            if ("hdfs".equalsIgnoreCase(allConfig.getComponentName())) {
                //测试hdfs联通性
                return this.checkHdfsConnect(allConfig);
            }
            return KerberosUtils.login(allConfig,
                    () -> testYarnConnect(testResult, allConfig),
                    KerberosUtils.convertMapConfToConfiguration(allConfig.getYarnConf()));

        } catch (Exception e) {
            LOG.error("test yarn connect error", e);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return testResult;
    }

    private ComponentTestResult testYarnConnect(ComponentTestResult testResult, Config allConfig) {
        HadoopConf hadoopConf = new HadoopConf();
        hadoopConf.initYarnConf(allConfig.getYarnConf());
        YarnClient testYarnClient = YarnClient.createYarnClient();
        testYarnClient.init(hadoopConf.getYarnConfiguration());
        testYarnClient.start();
        List<NodeReport> nodes = new ArrayList<>();
        try {
            nodes = testYarnClient.getNodeReports(NodeState.RUNNING);
        } catch (Exception e) {
            LOG.error("test yarn connect error", e);
        }
        int totalMemory = 0;
        int totalCores = 0;
        for (NodeReport rep : nodes) {
            totalMemory += rep.getCapability().getMemory();
            totalCores += rep.getCapability().getVirtualCores();
        }
        try {
            List<ComponentTestResult.QueueDescription> descriptions = getQueueDescription(null, testYarnClient.getRootQueueInfos());
            testResult.setClusterResourceDescription(new ComponentTestResult.ClusterResourceDescription(nodes.size(), totalMemory, totalCores, descriptions));
        } catch (Exception e) {
            LOG.error("getRootQueueInfos error", e);
        } finally {
            if(testYarnClient != null){
                try {
                    testYarnClient.close();
                } catch (IOException e) {
                    LOG.error("close yarn client error",e);
                }
            }
        }
        testResult.setResult(true);
        return testResult;
    }

    private List<ComponentTestResult.QueueDescription> getQueueDescription(String parentPath, List<QueueInfo> queueInfos) {
        List<ComponentTestResult.QueueDescription> descriptions = new ArrayList<>(queueInfos.size());
        parentPath = StringUtils.isBlank(parentPath) ? "" : parentPath + ".";
        for (QueueInfo queueInfo : queueInfos) {
            String queuePath = queueInfo.getQueueName().startsWith(parentPath) ? queueInfo.getQueueName() : parentPath + queueInfo.getQueueName();
            ComponentTestResult.QueueDescription queueDescription = new ComponentTestResult.QueueDescription();
            queueDescription.setQueueName(queueInfo.getQueueName());
            queueDescription.setCapacity(String.valueOf(queueInfo.getCapacity()));
            queueDescription.setMaximumCapacity(String.valueOf(queueInfo.getMaximumCapacity()));
            queueDescription.setQueueState(queueInfo.getQueueState().name());
            queueDescription.setQueuePath(queuePath);
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                List<ComponentTestResult.QueueDescription> childQueues = getQueueDescription(queueInfo.getQueueName(), queueInfo.getChildQueues());
                queueDescription.setChildQueues(childQueues);
            }
            descriptions.add(queueDescription);
        }
        return descriptions;
    }


    /**
     * 上传文件到hdfs中
     * @param bytes
     * @param hdfsPath 文件路径
     * @return
     */
    @Override
    public String uploadStringToHdfs(String bytes, String hdfsPath) {
        try {
            return KerberosUtils.login(config, () -> {
                FileSystem fs = null;
                try {
                    ByteArrayInputStream is = new ByteArrayInputStream(bytes.getBytes());
                    fs = FileSystem.get(conf);
                    Path destP = new Path(hdfsPath);
                    FSDataOutputStream os = fs.create(destP);
                    IOUtils.copyBytes(is, os, 4096, true);
                } catch (IOException e) {
                    LOG.error("submit file {} to hdfs error", hdfsPath,e);
                    throw new RdosDefineException("上传文件失败", e);
                } finally {
                    if (null != fs) {
                        try {
                            fs.close();
                        } catch (IOException e) {
                        }
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("submit file {} to hdfs success.", hdfsPath);
                }
                return conf.get("fs.defaultFS") + hdfsPath;
            }, conf);
        } catch (Exception e) {
            throw new RdosDefineException("上传文件失败", e);
        }
    }

    private ComponentTestResult checkHdfsConnect(Config testConnectConf) {
        //测试hdfs联通性
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            if (null == testConnectConf) {
                componentTestResult.setResult(false);
                componentTestResult.setErrorMsg("配置信息不能你为空");
                return componentTestResult;
            }
            KerberosUtils.login(testConnectConf, () -> {
                HadoopConf hadoopConf = new HadoopConf();
                hadoopConf.initHadoopConf(testConnectConf.getHadoopConf());
                Configuration configuration = hadoopConf.getConfiguration();
                FileSystem fs = null;
                try {
                    fs = FileSystem.get(configuration);
                } catch (Exception e) {
                    componentTestResult.setResult(false);
                    componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                    return componentTestResult;
                } finally {
                    if (null != fs) {
                        try {
                            fs.close();
                        } catch (IOException e) {
                            LOG.error("close file system error ", e);
                        }
                    }
                }

                componentTestResult.setResult(true);
                return componentTestResult;
            }, KerberosUtils.convertMapConfToConfiguration(testConnectConf.getHadoopConf()));

        } catch (Exception e) {
            LOG.error("close hdfs connect  error ", e);
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return componentTestResult;
    }

    @Override
    public ClusterResource getClusterResource() {
        ClusterResource clusterResource = new ClusterResource();
        try {
            KerberosUtils.login(config, () -> {
                YarnClient resourceClient = null;
                try {
                    resourceClient = YarnClient.createYarnClient();
                    resourceClient.init(conf);
                    resourceClient.start();
                    List<NodeReport> nodes = resourceClient.getNodeReports(NodeState.RUNNING);
                    List<ClusterResource.NodeDescription> clusterNodes = new ArrayList<>();

                    Integer totalMem = 0;
                    Integer totalCores = 0;
                    Integer usedMem = 0;
                    Integer usedCores = 0;

                    for (NodeReport rep : nodes) {
                        ClusterResource.NodeDescription node = new ClusterResource.NodeDescription();
                        String nodeName = rep.getHttpAddress().split(":")[0];
                        node.setNodeName(nodeName);
                        node.setMemory(rep.getCapability().getMemory());
                        node.setUsedMemory(rep.getUsed().getMemory());
                        node.setUsedVirtualCores(rep.getUsed().getVirtualCores());
                        node.setVirtualCores(rep.getCapability().getVirtualCores());
                        clusterNodes.add(node);

                        // 计算集群资源总量和使用量
                        Resource capability = rep.getCapability();
                        Resource used = rep.getUsed();
                        totalMem += capability.getMemory();
                        totalCores += capability.getVirtualCores();
                        usedMem += used.getMemory();
                        usedCores += used.getVirtualCores();
                    }

                    ClusterResource.ResourceMetrics metrics = createResourceMetrics(
                            totalMem, usedMem, totalCores, usedCores);

                    clusterResource.setNodes(clusterNodes);
                    clusterResource.setQueues(getQueueResource(yarnClient));
                    clusterResource.setResourceMetrics(metrics);

                } catch (Exception e) {
                    LOG.error("close reource error ", e);
                } finally {
                    if (null != resourceClient) {
                        try {
                            resourceClient.close();
                        } catch (IOException e) {
                            LOG.error("close reource error ", e);
                        }
                    }
                }
                return clusterResource;
            }, conf);

        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
        return clusterResource;
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
            JSONObject queueInfo = (JSONObject)ob;
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
                JSONObject userJSONObject = (JSONObject)user;
                userJSONObject.put("maxResource", userJSONObject.getJSONObject("userResourceLimit"));
                userJSONObject.put("maxAMResource", userJSONObject.getJSONObject("userResourceLimit"));
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

    public static void main(String[] args) throws Exception {

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {
            System.setProperty("HADOOP_USER_NAME", "admin");

            // input params json file path
            String filePath = args[0];
            File paramsFile = new File(filePath);
            fileInputStream = new FileInputStream(paramsFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            reader = new BufferedReader(inputStreamReader);
            String request = reader.readLine();
            Map params =  PublicUtil.jsonStrToObject(request, Map.class);
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);

            String pluginInfo = jobClient.getPluginInfo();
            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
            String md5plugin = MD5Util.getMd5String(pluginInfo);
            properties.setProperty("md5sum", md5plugin);

            HadoopClient client = new HadoopClient();
            client.init(properties);

            ClusterResource clusterResource = client.getClusterResource();

            LOG.info("submit success!");
            LOG.info(clusterResource.toString());
            System.exit(0);
        } catch (Exception e) {
            LOG.error("submit error!", e);
        } finally {
            if (reader != null){
                reader.close();
                inputStreamReader.close();
                fileInputStream.close();
            }
        }
    }


}
