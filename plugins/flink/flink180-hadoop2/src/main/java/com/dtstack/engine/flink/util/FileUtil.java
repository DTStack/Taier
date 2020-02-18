package com.dtstack.engine.flink.util;

import com.dtstack.engine.common.exception.RdosException;
import com.google.common.io.Files;
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
                throw new RdosException(" get exception download from hdfs, error:" + e.getCause().toString());
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
            //如果父目录不存在则创建
            Files.createParentDirs(outFile);
            outFile.createNewFile();

            FileOutputStream fout = new FileOutputStream(outFile);
            URL url = new URL(urlStr);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.connect();
            BufferedInputStream bfInputStream = new BufferedInputStream(httpUrlConnection.getInputStream());

            byte[] buf = new byte[BUFFER_SIZE];
            int readSize = -1;
            while((readSize = bfInputStream.read(buf)) != -1){
                fout.write(buf, 0, readSize);
            }

            //释放资源
            fout.close();
            bfInputStream.close();
            httpUrlConnection.disconnect();
            logger.info("download from remote url:{} success,dest file name is {}.", urlStr, dstFileName);
        } catch (IOException e) {
            logger.error("download from remote url:" + urlStr +"failure.", e);
            throw new RdosException("download from remote url:" + urlStr +"failure." + e.getMessage());
        }

        return true;
    }

    public static boolean downLoadFileFromHdfs(String uriStr, String dstFileName, Configuration hadoopConf) throws URISyntaxException, IOException {

        Pair<String, String> pair = parseHdfsUri(uriStr);
        if(pair == null){
            throw new RdosException("can't parse hdfs url from given uriStr:" + uriStr);
        }

        String hdfsUri = pair.getLeft();
        String hdfsFilePathStr = pair.getRight();

        URI uri = new URI(hdfsUri);
        FileSystem fs = FileSystem.get(uri, hadoopConf);
        Path hdfsFilePath = new Path(hdfsFilePathStr);
        if(!fs.exists(hdfsFilePath)){
            return false;
        }

        File file = new File(dstFileName);
        if(!file.getParentFile().exists()){
            Files.createParentDirs(file);
        }

        InputStream is=fs.open(hdfsFilePath);//读取文件
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

}
