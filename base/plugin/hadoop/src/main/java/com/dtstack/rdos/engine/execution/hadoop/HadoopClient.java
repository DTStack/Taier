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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HadoopClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopClient.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String YARN_CONF_PATH = "yarnConfPath";
    private static final String HADOOP_CONF_DIR = "HADOOP_CONF_DIR";
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


        String argContent = jobClient.getClassArgs();
        Map<String,String> params = null;
        try {
            params = new ObjectMapper().readValue(argContent, Map.class);
            params.put(MapReduceTemplate.JAR, jarOperator.getJarPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return null;
    }


}
