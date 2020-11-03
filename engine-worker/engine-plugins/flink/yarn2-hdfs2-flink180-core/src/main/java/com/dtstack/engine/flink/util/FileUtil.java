package com.dtstack.engine.flink.util;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http,hdfs文件下载
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);


    public static void checkFileExist(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            if (!new File(filePath).exists()) {
                throw new RdosDefineException(String.format("The file jar %s  path is not exist ", filePath));
            }
        }
    }

    public static void downloadKafkaKeyTab(JobClient jobClient, FilesystemManager filesystemManager) {
        Properties confProperties = jobClient.getConfProperties();
        String sftpKeytab = confProperties.getProperty(ConfigConstrant.KAFKA_SFTP_KEYTAB);

        if (StringUtils.isBlank(sftpKeytab)) {
            logger.info("flink task submission has enabled keberos authentication, but kafka has not !!!");
            return;
        }

        String taskKeytabDirPath = ConfigConstrant.LOCAL_KEYTAB_DIR_PARENT + ConfigConstrant.SP + jobClient.getTaskId();
        File taskKeytabDir = new File(taskKeytabDirPath);
        if (!taskKeytabDir.exists()) {
            taskKeytabDir.mkdirs();
        }

        File kafkaKeytabFile = new File(sftpKeytab);
        String localKafkaKeytab = String.format("%s/%s", taskKeytabDirPath, kafkaKeytabFile.getName());
        File downloadKafkaKeytabFile = filesystemManager.downloadFile(sftpKeytab, localKafkaKeytab);
        logger.info("Download Kafka keytab file to :" + downloadKafkaKeytabFile.toPath());

    }

    public static JsonObject readJsonFromHdfs(String filePath, Configuration hadoopConf) throws URISyntaxException, IOException {
        InputStream is = readStreamFromFile(filePath, hadoopConf);
        if (is == null) {
            return null;
        }
        JsonParser jsonParser = new JsonParser();
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return (JsonObject) jsonParser.parse(reader);
        }
    }

    private static InputStream readStreamFromFile(String filePath, Configuration hadoopConf) throws URISyntaxException, IOException {
        Pair<String, String> pair = parseHdfsUri(filePath);
        if(pair == null){
            throw new RdosDefineException("can't parse hdfs url from given uriStr:" + filePath);
        }

        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();

        URI uri = new URI(hdfsUri);
        FileSystem fs = FileSystem.get(uri, hadoopConf);
        Path hdfsFilePath = new Path(hdfsFilePathStr);
        if(!fs.exists(hdfsFilePath)){
            return null;
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
