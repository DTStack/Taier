package com.dtstack.engine.hdfs;


import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.PublicUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class DtHdfsClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(DtHdfsClient.class);
    private Config config;
    private Configuration configuration;

    @Override
    public void init(Properties prop) throws Exception {
        LOG.info("hadoop client init...");

        String configStr = PublicUtil.objToString(prop);
        config = PublicUtil.jsonStrToObject(configStr, Config.class);
        configuration =  this.initYarnConf(config.getYarnConf());
    }

    private Configuration initYarnConf(Map<String, Object> conf){
        if(null == conf){
            return null;
        }

        Configuration  configuration = new Configuration();

        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                configuration.set(key, (String) value);
            } else if (value instanceof Boolean){
                configuration.setBoolean(key, (boolean) value);
            }
        });
        return configuration;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return null;
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
            return this.checkHdfsConnect(allConfig);

        } catch (Exception e) {
            LOG.error("test yarn connect error", e);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return testResult;
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
            configuration =  this.initYarnConf(config.getYarnConf());
            return KerberosUtils.login(config, () -> {
                FileSystem fs = null;
                try {
                    ByteArrayInputStream is = new ByteArrayInputStream(bytes.getBytes());
                    fs = FileSystem.get(configuration);
                    Path destP = new Path(hdfsPath);
                    FSDataOutputStream os = fs.create(destP);
                    IOUtils.copyBytes(is, os, 4096, true);
                } catch (IOException e) {
                    LOG.error("submit file {} to hdfs error", hdfsPath,e);
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
                return configuration.get("fs.defaultFS") + hdfsPath;
            }, configuration);
        } catch (Exception e) {
            throw new RdosDefineException("上传文件失败", e);
        }
    }

    @Override
    public ClusterResource getClusterResource() {
        return null;
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
                FileSystem fs = null;
                try {
                    Configuration configuration = this.initYarnConf(testConnectConf.getHadoopConf());
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


}
