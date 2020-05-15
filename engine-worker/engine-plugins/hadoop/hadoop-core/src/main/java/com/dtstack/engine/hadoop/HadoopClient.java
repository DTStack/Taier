package com.dtstack.engine.hadoop;


import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.JobParam;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.hadoop.parser.AddJarOperator;
import com.dtstack.engine.hadoop.util.HadoopConf;
import com.dtstack.engine.hadoop.util.KerberosUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
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
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private EngineResourceInfo resourceInfo;
    private Configuration conf = new Configuration();
    private YarnClient yarnClient;
    private Config config;
    private Map<String, List<String>> cacheFile = Maps.newConcurrentMap();


    @Override
    public void init(Properties prop) throws Exception {
        System.out.println("hadoop client init...");

        String configStr = PublicUtil.objToString(prop);
        config = PublicUtil.jsonStrToObject(configStr, Config.class);
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(config.getHadoopConf());
        customerConf.initYarnConf(config.getYarnConf());
        conf = customerConf.getYarnConfiguration();

        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.scheduler.maximum-allocation-mb", "1024");
        conf.set("yarn.nodemanager.resource.memory-mb", "1024");
        conf.set("mapreduce.map.memory.mb","1024");
        conf.set("mapreduce.reduce.memory.mb","1024");
        conf.setBoolean("mapreduce.app-submission.cross-platform", true);

        setHadoopUserName(config);

        if (config.isOpenKerberos()){
            initSecurity(config);
        }
        yarnClient = getYarnClient();
        resourceInfo = new HadoopResourceInfo();
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
            Map<String, Object> plugininfo = jobClient.getParamAction().getPluginInfo();
            Configuration jobConf = new Configuration(conf);
            if(plugininfo.containsKey(QUEUE)){
                jobConf.set(MRJobConfig.QUEUE_NAME, plugininfo.get(QUEUE).toString());
            }

            MapReduceTemplate mr = new MapReduceTemplate(jobConf, jobParam);
            mr.run();
            LOG.info("mr jobId:{} jobName:{}", mr.getJobId(), jobParam.getJobName());
            return JobResult.createSuccessResult(mr.getJobId());
        } catch (Exception ex) {
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


    private static void initSecurity(Config config) throws IOException {
        try {
            LOG.info("start init security!");
            KerberosUtils.login(config);
        } catch (IOException e) {
            LOG.error("initSecurity happens error", e);
            throw new IOException("InitSecurity happens error", e);
        }
        LOG.info("UGI info: " + UserGroupInformation.getCurrentUser());
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
}
