package com.dtstack.engine.flink.util;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * http,hdfs文件下载
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

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

}
