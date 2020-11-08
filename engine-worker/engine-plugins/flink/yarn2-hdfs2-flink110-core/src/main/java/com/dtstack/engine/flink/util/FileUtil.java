package com.dtstack.engine.flink.util;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DtStringUtil;
import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.flink.FlinkConfig;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    private static final int BUFFER_SIZE = 10240;

    private static final String HTTP_PROTOCAL = "http://";

    private static final String HDFS_PROTOCAL = "hdfs://";

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);

    /**
     * @param urlStr
     * @param dstFileName
     * @return
     */
    public static boolean downLoadFile(String urlStr, String dstFileName, Configuration hadoopConf){

        if(urlStr.startsWith(HTTP_PROTOCAL)){
            return downLoadFileFromHttp(urlStr, dstFileName);
        }else if(urlStr.startsWith(HDFS_PROTOCAL)){

            try{
                return downLoadFileFromHdfs(urlStr, dstFileName, hadoopConf);
            }catch (Exception e){
                logger.error("", e);
                throw new RdosDefineException(" get exception download from hdfs, error:" + e.getCause().toString());
            }
        }

        return false;
    }

    public static boolean downLoadFileFromHttp(String urlStr, String dstFileName){
        try {
            File outFile = new File(dstFileName);
            //如果当前文件存在则删除,覆盖最新的文件
            if(outFile.exists()){
                outFile.delete();
            }
            Files.createParentDirs(outFile);//如果父目录不存在则创建
            outFile.createNewFile();

            FileOutputStream fout = new FileOutputStream(outFile);
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            BufferedInputStream bfInputStream = new BufferedInputStream(httpURLConnection.getInputStream());

            byte[] buf = new byte[BUFFER_SIZE];
            int readSize = -1;
            while((readSize = bfInputStream.read(buf)) != -1){
                fout.write(buf, 0, readSize);
            }

            //释放资源
            fout.close();
            bfInputStream.close();
            httpURLConnection.disconnect();
            logger.info("download from remote url:{} success,dest file name is {}.", urlStr, dstFileName);
        } catch (IOException e) {
            logger.error("download from remote url:" + urlStr +"failure.", e);
            throw new RdosDefineException("download from remote url:" + urlStr +"failure." + e.getMessage());
        }

        return true;
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

    public static JsonObject readJsonFromHdfs(String filePath, Configuration hadoopConf) throws URISyntaxException, IOException {
        InputStream is = readStreamFromFile(filePath, hadoopConf);
        if (is == null) {
            throw new RdosDefineException("can't read file from hdfs, file path:" + filePath);
        }
        JsonParser jsonParser = new JsonParser();
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return (JsonObject) jsonParser.parse(reader);
        }
    }

    public static boolean downLoadFileFromHdfs(String uriStr, String dstFileName, Configuration hadoopConf) throws URISyntaxException, IOException {

        File file = new File(dstFileName);
        if(!file.getParentFile().exists()){
            Files.createParentDirs(file);
        }

        //读取文件
        InputStream is = readStreamFromFile(uriStr, hadoopConf);
        if (is == null) {
            return false;
        }

        IOUtils.copyBytes(is, new FileOutputStream(file),2048, true);//保存到本地

        return true;
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

    public static void checkFileExist(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            if (!new File(filePath).exists()) {
                throw new RdosDefineException(String.format("The file jar %s  path is not exist ", filePath));
            }
        }
    }

    public static void downloadKafkaKeyTab(JobClient jobClient, FlinkConfig flinkConfig) {
        SFTPHandler handler = null;
        try {
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
            handler = SFTPHandler.getInstance(flinkConfig.getSftpConf());
            handler.downloadFile(sftpKeytab, localKafkaKeytab);
        } catch (Exception e) {
            logger.error("Download keytab from sftp failed", e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

}
