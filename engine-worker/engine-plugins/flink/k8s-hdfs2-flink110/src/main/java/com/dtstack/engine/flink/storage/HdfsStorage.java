package com.dtstack.engine.flink.storage;

import com.dtstack.engine.base.util.HadoopConfTool;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.ResourceManagerOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.util.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HdfsStorage extends AbstractStorage {

    private static final Logger LOG = LoggerFactory.getLogger(PerJobClientFactory.class);

    public static final String HADOOP_CONF_STRING = "hadoop.conf.string";

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);

    private Configuration configuration;

    private Map<String, Object> hadoopConfMap;

    private Properties pluginInfo;

    @Override
    public void init(Properties pluginInfo) {
        this.pluginInfo = pluginInfo;
        hadoopConfMap = (Map<String, Object>) pluginInfo.get("hadoopConf");
        initHadoopConf(hadoopConfMap);
    }

    public void initHadoopConf(Map<String, Object> conf){
        if (conf == null || conf.size() == 0) {
            throw new RdosDefineException("No set hdfs config!");
        }
        configuration = new Configuration();
        HadoopConfTool.setFsHdfsImplDisableCache(configuration);
        conf.keySet().forEach(key ->{
            Object value = conf.get(key);
            if (value instanceof String){
                configuration.set(key, (String) value);
            } else if (value instanceof Boolean){
                configuration.setBoolean(key, (boolean) value);
            }
        });
    }

    @Override
    public void fillStorageConfig(org.apache.flink.configuration.Configuration config, FlinkConfig flinkConfig) {

        // set hadoop conf dir
        String hadoopConfDir = config.getString(KubernetesConfigOptions.HADOOP_CONF_DIR);
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir);

        // set hadoop name
        String hadoopUserName = config.getString(ConfigConstrant.HADOOP_USER_NAME, "");
        if (StringUtils.isBlank(hadoopUserName)) {
            hadoopUserName = System.getenv(ConfigConstrant.HADOOP_USER_NAME);
            if (StringUtils.isBlank(hadoopUserName)) {
                hadoopUserName = System.getProperty(ConfigConstrant.HADOOP_USER_NAME);
            }
        }
        config.setString(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);
        config.setString(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);

        LOG.info("hadoop env info, {}:{} {}:{}", ConfigConstrant.HADOOP_CONF_DIR, hadoopConfDir, ConfigConstrant.HADOOP_USER_NAME, hadoopUserName);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String hadoopConfString = objectMapper.writeValueAsString(hadoopConfMap);
            config.setString(HADOOP_CONF_STRING, hadoopConfString);
            FileSystem.initialize(config);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    public String getMessageFromJobArchive(String jobId, String urlPath) throws Exception {
        String archiveDir = pluginInfo.getProperty(JobManagerOptions.ARCHIVE_DIR.key());
        String jobArchivePath = archiveDir + ConfigConstrant.SP + jobId;
        InputStream is = readStreamFromHdfs(jobArchivePath, configuration);
        JsonParser jsonParser = new JsonParser();
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            JsonObject jobArchiveAll = (JsonObject) jsonParser.parse(reader);
            Preconditions.checkNotNull(jobArchiveAll, "jobArchive is null");

            JsonArray jsonArray = jobArchiveAll.getAsJsonArray("archive");
            for (JsonElement ele: jsonArray) {
                JsonObject obj = ele.getAsJsonObject();
                if (StringUtils.equals(obj.get("path").getAsString(), urlPath)) {
                    String exception = obj.get("json").getAsString();
                    return exception;
                }
            }
        }
        throw new RdosDefineException(String.format("Not found Message from jobArchive, jobId[%s], urlPath[%s]", jobId, urlPath));
    }

    @Override
    public Configuration getStorageConfig() {
        return configuration;
    }

    private static InputStream readStreamFromHdfs(String filePath, Configuration hadoopConf) throws URISyntaxException, IOException {
        Pair<String, String> pair = parseHdfsUri(filePath);
        if(pair == null){
            throw new RdosDefineException("can't parse hdfs url from given uriStr:" + filePath);
        }

        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();

        URI uri = new URI(hdfsUri);
        org.apache.hadoop.fs.FileSystem fs = org.apache.hadoop.fs.FileSystem.get(uri, hadoopConf);
        Path hdfsFilePath = new Path(hdfsFilePathStr);
        if(!fs.exists(hdfsFilePath)){
            throw new RuntimeException("Files not exit in hdfs");
        }

        return fs.open(hdfsFilePath);
    }

    private static Pair<String, String> parseHdfsUri(String path){
        Matcher matcher = pattern.matcher(path);
        if(matcher.find() && matcher.groupCount() == 2){
            String hdfsUri = matcher.group(1);
            String hdfsPath = matcher.group(2);
            return new MutablePair<>(hdfsUri, hdfsPath);
        }else{
            return null;
        }
    }

}
