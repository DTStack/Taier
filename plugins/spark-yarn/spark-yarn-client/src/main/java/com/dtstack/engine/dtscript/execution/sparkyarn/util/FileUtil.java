package com.dtstack.engine.dtscript.execution.sparkyarn.util;

import com.dtstack.engine.common.exception.RdosException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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

    private static final Integer BUFFER_SIZE = 2048;

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);

    private static String fileSP = File.separator;

    public static Map<String, String> downLoadDirFromHdfs(String uriStr, String dstDirName, Configuration hadoopConf) throws IOException, URISyntaxException {
        try (FileSystem fs = FileSystem.get(hadoopConf)) {
            Path path = new Path(uriStr);
            if (!fs.exists(path)) {
                throw new RuntimeException("hdfs not exists" + path);
            }

            if (!fs.isDirectory(path)) {
                throw new RuntimeException("transfer must be directory");
            }

            Map<String, String> downFileInfo = Maps.newHashMap();
            FileStatus[] statusArr = fs.listStatus(path);
            for (FileStatus status : statusArr) {
                String subPath = status.getPath().toString();
                String fileName = status.getPath().getName();
                String localDstFileName = dstDirName + fileSP + fileName;
                downLoadFileFromHdfs(subPath, localDstFileName, hadoopConf);
                downFileInfo.put(fileName, localDstFileName);
            }
            return downFileInfo;
        }
    }


    public static boolean downLoadFileFromHdfs(String uriStr, String dstFileName, Configuration hadoopConf) throws URISyntaxException, IOException {

        Pair<String, String> pair = parseHdfsUri(uriStr);
        if(pair == null){
            throw new RdosException("can't parse hdfs url from given uriStr:" + uriStr);
        }

        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();

        URI uri = new URI(hdfsUri);
        try (FileSystem fs = FileSystem.get(uri, hadoopConf)) {
            Path hdfsFilePath = new Path(hdfsFilePathStr);
            if (!fs.exists(hdfsFilePath)) {
                return false;
            }


            File file = new File(dstFileName);
            if(!file.getParentFile().exists()){
                Files.createParentDirs(file);
            }

            InputStream is=fs.open(hdfsFilePath);//读取文件
            IOUtils.copyBytes(is, new FileOutputStream(file), BUFFER_SIZE, true);//保存到本地
        }
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

    public static void deleteFile(String fileName, Configuration conf) throws Exception{
        deleteFiles(Lists.newArrayList(fileName),conf);
    }

    public static void deleteFiles(List<String> fileNames, Configuration conf) throws Exception{
        if(CollectionUtils.isNotEmpty(fileNames)){
            try (FileSystem fs = FileSystem.get(conf)){
                for (String fileName : fileNames) {
                    Path path = new Path(fileName);
                    if (!fs.exists(path)){
                        continue;
                    }

                    if(!fs.isFile(path)){
                        continue;
                    }

                    fs.delete(path,false);
                }
            }
        }
    }

    public static void deleteFile(String delpath) {
        try {
            File file = new File(delpath);
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        deleteFile(delpath + File.separator + filelist[i]);
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
