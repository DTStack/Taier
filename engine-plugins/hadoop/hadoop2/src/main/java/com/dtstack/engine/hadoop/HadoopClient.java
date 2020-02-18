package com.dtstack.engine.hadoop;


import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.AbstractClient;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.hadoop.parser.AddJarOperator;
import com.dtstack.engine.hadoop.util.HadoopConf;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class HadoopClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopClient.class);
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String TMP_PATH = USER_DIR + "/tmp";
    private static final String HDFS_PREFIX = "hdfs://";
    private static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";
    private EngineResourceInfo resourceInfo;
    private Configuration conf = new Configuration();
    private YarnClient yarnDelegate = YarnClient.createYarnClient();
    private Config config;


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
        resourceInfo = new HadoopResourceInfo();
        yarnDelegate.init(conf);
        yarnDelegate.start();
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
            yarnDelegate.killApplication(generateApplicationId(jobId));
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
            ApplicationReport report = yarnDelegate.getApplicationReport(appId);
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
            JarFileInfo jarFileInfo = null;
            String sql = jobClient.getSql();
            for(String tmpSql : DtStringUtil.splitIgnoreQuota(sql, ';')){
                if(AddJarOperator.verific(tmpSql)){
                    jarFileInfo = AddJarOperator.parseSql(tmpSql);
                    break;
                }
            }

            if(jarFileInfo == null){
                throw new RdosDefineException("need at least one jar.");
            }

            String jarPath = jarFileInfo.getJarPath();
            if(StringUtils.isBlank(jarPath)) {
                throw new RdosDefineException("jar path is needful");
            }

            if(!jarPath.startsWith(HDFS_PREFIX)) {
                throw new RdosDefineException("only support hdfs protocol for jar path");
            }

            setHadoopUserName(config);
            String localJarPath = TMP_PATH + File.separator + UUID.randomUUID().toString() + ".jar";
            downloadHdfsFile(jarPath, localJarPath);

            Map<String,String> params = new ObjectMapper().readValue(jobClient.getClassArgs(), Map.class);

            Map<String, Object> plugininfo = jobClient.getParamAction().getPluginInfo();
            if(plugininfo.containsKey(MapReduceTemplate.QUEUE)){
                params.put(MapReduceTemplate.QUEUE,plugininfo.get(MapReduceTemplate.QUEUE).toString());
            }

            params.put(MapReduceTemplate.JAR, localJarPath);
            MapReduceTemplate mr = new MapReduceTemplate(jobClient.getJobName(), conf, params);
            mr.run();
            System.out.println("mr.jobId=" + mr.getJobId());
            return JobResult.createSuccessResult(mr.getJobId());
        } catch (Exception ex) {
            ex.printStackTrace();
            return JobResult.createErrorResult(ex);
        }finally {
            //TODO 删除临时文件
        }

    }

    private void downloadHdfsFile(String from, String to) throws IOException {
        Path hdfsFilePath = new Path(from);
        InputStream is= FileSystem.get(conf).open(hdfsFilePath);//读取文件
        IOUtils.copyBytes(is, new FileOutputStream(new File(to)),2048, true);//保存到本地
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return resourceInfo.judgeSlots(jobClient);
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();

        try {
            ApplicationReport applicationReport = yarnDelegate.getApplicationReport(generateApplicationId(jobId));
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

        UserGroupInformation.setThreadLocalData(HADOOP_USER_NAME, config.getHadoopUserName());
    }


}
