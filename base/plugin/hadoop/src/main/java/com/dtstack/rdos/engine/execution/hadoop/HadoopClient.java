package com.dtstack.rdos.engine.execution.hadoop;


import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.stream.AddJarOperator;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.google.common.io.Files;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

public class HadoopClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopClient.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String YARN_CONF_PATH = "yarnConfPath";
    private static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
    private static final String TMP_PATH = "./tmp";
    private static final String HDFS_PREFIX = "hdfs://";
    private EngineResourceInfo resourceInfo;
    private Configuration conf = new Configuration();


    @Override
    public void init(Properties prop) throws Exception {
        System.out.println("hadoop client init...");
        resourceInfo = new HadoopResourceInfo();

        conf.clear();
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

    }

    @Override
    public JobResult cancelJob(String jobId) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster() {
        return null;
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        try {
            AddJarOperator jarOperator = null;
            for(Operator operator : jobClient.getOperators()){
                if(operator instanceof AddJarOperator){
                    jarOperator = (AddJarOperator) operator;
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

            String localJarPath = TMP_PATH + File.separator + jarPath.substring(HDFS_PREFIX.length());
            downloadHdfsFile(jarPath, localJarPath);

            Map<String,String> params = new ObjectMapper().readValue(jobClient.getClassArgs(), Map.class);
            params.put(MapReduceTemplate.JAR, jarOperator.getJarPath());
            MapReduceTemplate mr = new MapReduceTemplate(jobClient.getJobName(), conf, params);
            mr.run();
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

}
