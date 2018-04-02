package com.dtstack.rdos.engine.execution.hadoop;


import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
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

public class HadoopClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopClient.class);
    private static final String YARN_CONF_PATH = "yarnConfPath";
    private static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
    private static final String TMP_PATH = "/tmp";
    private static final String HDFS_PREFIX = "hdfs://";
    private EngineResourceInfo resourceInfo;
    private Configuration conf = new Configuration();
    private YarnClient yarnDelegate = YarnClient.createYarnClient();


    @Override
    public void init(Properties prop) throws Exception {
        System.out.println("hadoop client init...");
        resourceInfo = new HadoopResourceInfo();

        conf.clear();
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.scheduler.maximum-allocation-mb", "1024");
        conf.set("yarn.nodemanager.resource.memory-mb", "1024");
        conf.set("mapreduce.map.memory.mb","1024");
        conf.set("mapreduce.reduce.memory.mb","1024");
        conf.setBoolean("mapreduce.app-submission.cross-platform", true);

        String hadoopConfDir = null;
        if(System.getenv(HADOOP_CONF_DIR) != null) {
            hadoopConfDir = System.getenv(HADOOP_CONF_DIR);
        }

        if(StringUtils.isNotBlank(prop.getProperty(YARN_CONF_PATH))) {
            hadoopConfDir = prop.getProperty(YARN_CONF_PATH);
        }

        if(StringUtils.isNotBlank(hadoopConfDir)) {
            File dir = new File(prop.getProperty(YARN_CONF_PATH));
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.getPath().endsWith("xml")) {
                    conf.addResource(file.toURI().toURL());
                }
            }
        }

        prop.remove(YARN_CONF_PATH);
        prop.remove("type");
        for(Map.Entry entry : prop.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            conf.set(key, value);
        }

        yarnDelegate.init(conf);
    }

    @Override
    public JobResult cancelJob(String jobId) {
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
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
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
                    throw new RdosException("Unsupported application state");
            }
        } catch (YarnException e) {
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster() {
        throw new RdosException("hadoop client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException("hadoop client not support method 'getJobMaster'");
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        try {
            BatchAddJarOperator jarOperator = null;
            for(Operator operator : jobClient.getOperators()){
                if(operator instanceof BatchAddJarOperator){
                    jarOperator = (BatchAddJarOperator) operator;
                    break;
                }
            }

            if(jarOperator == null){
                throw new RdosException("submit type of MR need to add jar operator.");
            }

            String jarPath = jarOperator.getJarPath();
            if(StringUtils.isBlank(jarPath)) {
                throw new RdosException("jar path is needful");
            }

            if(!jarPath.startsWith(HDFS_PREFIX)) {
                throw new RdosException("only support hdfs protocol for jar path");
            }

            String localJarPath = TMP_PATH + File.separator + UUID.randomUUID().toString() + ".jar";
            downloadHdfsFile(jarPath, localJarPath);


            Map<String,String> params = new ObjectMapper().readValue(jobClient.getClassArgs(), Map.class);
            params.put(MapReduceTemplate.JAR, localJarPath);
            MapReduceTemplate mr = new MapReduceTemplate(jobClient.getJobName(), conf, params);
            mr.run();
            System.out.println("mr.jobId=" + mr.getJobId());
            return JobResult.createSuccessResult(mr.getJobId());
        } catch (Exception ex) {
            ex.printStackTrace();
            return JobResult.createErrorResult(ex);
        }

    }

    private void downloadHdfsFile(String from, String to) throws IOException {
        Path hdfsFilePath = new Path(from);
        InputStream is= FileSystem.get(conf).open(hdfsFilePath);//读取文件
        IOUtils.copyBytes(is, new FileOutputStream(new File(to)),2048, true);//保存到本地
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        return new HadoopResourceInfo();
    }

    @Override
    public String getJobLog(String jobId) {
        try {
            ApplicationReport applicationReport = yarnDelegate.getApplicationReport(generateApplicationId(jobId));
            return applicationReport.getDiagnostics();
        } catch (Exception e) {
            LOG.error("", e);
        }

        return null;
    }


}
