package com.dtstack.taier.flink.util;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;
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

    /**
     * HDFS文件路径格式
     */
    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static final Pattern pattern = Pattern.compile(HDFS_PATTERN);

    public static void checkFileExist(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            if (!new File(filePath).exists()) {
                throw new PluginDefineException(String.format("The file jar %s  path is not exist ", filePath));
            }
        }
    }

    public static InputStream readStreamFromFile(String filePath, Configuration hadoopConf) throws URISyntaxException, IOException {
        Pair<String, String> pair = parseHdfsUri(filePath);
        if(pair == null){
            throw new PluginDefineException("can't parse hdfs url from given uriStr:" + filePath);
        }

        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();

        URI uri = new URI(hdfsUri);
        FileSystem fs = FileSystem.get(uri, hadoopConf);
        Path hdfsFilePath = new Path(hdfsFilePathStr);
        if(!fs.exists(hdfsFilePath)){
            throw new RuntimeException(String.format("File[%s] not exit in hdfs", filePath));
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


    /**
     * delete file
     * @param filePath path
     * @param hadoopConf config
     */
    public static boolean deleteFile(String filePath, Configuration hadoopConf) throws URISyntaxException, IOException {
        Pair<String, String> pair = parseHdfsUri(filePath);
        // local file
        if(pair == null){
            File file = new File(filePath);
            if(file.exists()){
                return file.delete();
            }else{
                return true;
            }
        }

        // hdfs file
        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();
        URI uri = new URI(hdfsUri);
        FileSystem fs = FileSystem.get(uri, hadoopConf);
        Path hdfsFilePath = new Path(hdfsFilePathStr);
        if(!fs.exists(hdfsFilePath)){
            return true;
        }

        return fs.delete(hdfsFilePath, true);
    }
}
