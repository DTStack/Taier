package com.dtstack.engine.hadoop;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.dtstack.engine.api.pojo.ComponentTestResult;
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
import com.dtstack.engine.common.pojo.ClusterResource;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DtStringUtil;
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
import java.util.*;

public class HadoopClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopClient.class);
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String TMP_PATH = USER_DIR + "/tmp";
    private static final String HDFS_PREFIX = "hdfs://";
    private static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";
    private static final String QUEUE = "queue";
    private EngineResourceInfo resourceInfo;
    private Configuration conf = new Configuration();
    private YarnClient yarnClient;
    private Config config;
    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();
    private static final String APP_TYPE = "Apache Flink";
    private static final String DEFAULT_APP_NAME_PREFIX = "Flink session";
    private static final String FLINK_URL_FORMAT = "http://%s/proxy/%s/taskmanagers";
    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";
    private static final long ONE_MEGABYTE = 1024*1024;

    @Override
    public void init(Properties prop) throws Exception {
        System.out.println("hadoop client init...");

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

        try {
            LOG.info("start init security!");
            KerberosUtils.login(config, () -> {
                yarnClient = getYarnClient();
                resourceInfo = new HadoopResourceInfo();
                return null;
            }, conf);
        } catch (Exception e) {
            LOG.error("initSecurity happens error", e);
            throw new IOException("InitSecurity happens error", e);
        }
        LOG.info("UGI info: " + UserGroupInformation.getCurrentUser());

    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.MR.equals(jobType)){
            jobResult = submitJobWithJar(jobClient);
        }
        return jobResult;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();

        try {
            getYarnClient().killApplication(generateApplicationId(jobId));
        } catch (YarnException | IOException e) {
            return JobResult.createErrorResult(e);
        }

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData("jobid", jobId);
        return jobResult;
    }

    private ApplicationId generateApplicationId(String jobId) {
        String appId = jobId.replace("job_", "application_");
        return ConverterUtils.toApplicationId(appId);
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {

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
        } catch (YarnException e) {
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
        File toFile = new File(to);
        if(!toFile.getParentFile().exists()){
            Files.createParentDirs(toFile);
        }
        Path hdfsFilePath = new Path(from);
        InputStream is= FileSystem.get(conf).open(hdfsFilePath);//读取文件
        IOUtils.copyBytes(is, new FileOutputStream(toFile),2048, true);//保存到本地
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return resourceInfo.judgeSlots(jobClient);
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();

        try {
            ApplicationReport applicationReport = getYarnClient().getApplicationReport(generateApplicationId(jobId));
            return applicationReport.getDiagnostics();
        } catch (Exception e) {
            LOG.error("", e);
        }

        return null;
    }

    private void setHadoopUserName(Config config){
        if(Strings.isNullOrEmpty(config.getHadoopUserName())){
            return;
        }

        UserGroupInformation.afterSetHadoopUserName(config.getHadoopUserName());
    }


    public YarnClient getYarnClient(){
        try{
            if(yarnClient == null){
                synchronized (this){
                    if(yarnClient == null){
                        YarnClient yarnClient1 = YarnClient.createYarnClient();
                        yarnClient1.init(conf);
                        yarnClient1.start();
                        yarnClient = yarnClient1;
                    }
                }
            }else{
                //判断下是否可用
                yarnClient.getAllQueues();
            }
        }catch(Throwable e){
            LOG.error("getYarnClient error:{}",e);
            synchronized (this){
                if(yarnClient != null){
                    boolean flag = true;
                    try{
                        //判断下是否可用
                        yarnClient.getAllQueues();
                    }catch(Throwable e1){
                        LOG.error("getYarnClient error:{}",e1);
                        flag = false;
                    }
                    if(!flag){
                        try{
                            yarnClient.stop();
                        }finally {
                            yarnClient = null;
                        }
                    }
                }
                if(yarnClient == null){
                    YarnClient yarnClient1 = YarnClient.createYarnClient();
                    yarnClient1.init(conf);
                    yarnClient1.start();
                    yarnClient = yarnClient1;
                }
            }
        }
        return yarnClient;
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
                sqlItera.remove();
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
        jobClient.setSql(String.join(";", sqlList));
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
            return KerberosUtils.login(allConfig, () -> testYarnConnect(testResult, allConfig),conf);

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
        }
        testResult.setResult(true);
        return testResult;
    }

    private List<ComponentTestResult.QueueDescription> getQueueDescription(String parentPath, List<QueueInfo> queueInfos) {
        List<ComponentTestResult.QueueDescription> descriptions = new ArrayList<>(queueInfos.size());
        parentPath = StringUtils.isBlank(parentPath) ? "" : parentPath + ".";
        for (QueueInfo queueInfo : queueInfos) {
            String queuePath = parentPath + queueInfo.getQueueName();
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
                    throw new RdosDefineException("上传文件失败", e);
                } finally {
                    if (Objects.nonNull(fs)) {
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
            if (Objects.isNull(testConnectConf)) {
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
                    if (Objects.nonNull(fs)) {
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
                    for (NodeReport rep : nodes) {
                        ClusterResource.NodeDescription node = new ClusterResource.NodeDescription();
                        node.setMemory(rep.getCapability().getMemory());
                        node.setUsedMemory(rep.getUsed().getMemory());
                        node.setUsedVirtualCores(rep.getUsed().getVirtualCores());
                        node.setVirtualCores(rep.getCapability().getVirtualCores());
                        clusterNodes.add(node);
                    }
                    clusterResource.setYarn(clusterNodes);
                    clusterResource.setFlink(this.initTaskManagerResource(yarnClient));
                } catch (Exception e) {
                    LOG.error("close reource error ", e);
                } finally {
                    if (Objects.nonNull(resourceClient)) {
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



    public List<ClusterResource.TaskManagerDescription> initTaskManagerResource(YarnClient yarnClient) throws Exception {
        List<ApplicationId> applicationIds = acquireApplicationIds(yarnClient);

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
            //兼容Hadoop 2.7.3.2.6.4.91-3
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

        String key = YARN_RM_WEB_KEY_PREFIX + proxyInfoKey;
        String addr = yarnClient.getConfig().get(key);

        if (addr == null) {
            YarnConfiguration config = (YarnConfiguration) yarnClient.getConfig();
            addr = config.get("yarn.resourcemanager.webapp.address");
        }

        List<ClusterResource.TaskManagerDescription> taskManagerDescriptions = new ArrayList<>();
        for (ApplicationId applicationId : applicationIds) {
            String url = String.format(FLINK_URL_FORMAT, addr, applicationId.toString());
            String msg = PoolHttpClient.get(url, null);
            if (msg == null) {
                continue;
            }

            JSONObject taskManagerInfo = JSONObject.parseObject(msg);
            if (!taskManagerInfo.containsKey("taskmanagers")) {
                continue;
            }

            JSONArray taskManagers = taskManagerInfo.getJSONArray("taskmanagers");
            for (int i = 0; i < taskManagers.size(); i++) {
                JSONObject jsonObject = taskManagers.getJSONObject(i);
                if (jsonObject.containsKey("hardware")) {
                    jsonObject.putAll(jsonObject.getJSONObject("hardware"));
                }

                ClusterResource.TaskManagerDescription description = TypeUtils.castToJavaBean(jsonObject, ClusterResource.TaskManagerDescription.class);
                description.setFreeMemory(description.getFreeMemory() / ONE_MEGABYTE);
                description.setPhysicalMemory(description.getPhysicalMemory() / ONE_MEGABYTE);
                description.setManagedMemory(description.getManagedMemory() / ONE_MEGABYTE);
                taskManagerDescriptions.add(description);
            }
        }
        return taskManagerDescriptions;

    }

    private List<ApplicationId> acquireApplicationIds(YarnClient yarnClient) {
        try {
            Set<String> set = new HashSet<>();
            set.add(APP_TYPE);
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            List<ApplicationReport> reportList = yarnClient.getApplications(set, enumSet);
            List<ApplicationId> applicationIds = new ArrayList<>();
            for (ApplicationReport report : reportList) {
                if (!report.getName().startsWith(DEFAULT_APP_NAME_PREFIX)) {
                    continue;
                }
                applicationIds.add(report.getApplicationId());
            }
            return applicationIds;
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

}
