package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author sishu.yss
 */
public class HdfsOperator {

	private static ThreadLocal<FileSystem> tfs= new ThreadLocal<FileSystem>();

    private static final Logger logger = LoggerFactory.getLogger(HdfsOperator.class);

    private static final String HDFS_PATTERN = "(hdfs://[^/]+)(.*)";

    private static Pattern pattern = Pattern.compile(HDFS_PATTERN);

    public static final BigDecimal BASE_VALUE = new BigDecimal("1024");
    public static final BigDecimal KB_VALUE = BASE_VALUE.pow(1);
    public static final BigDecimal MB_VALUE = BASE_VALUE.pow(2);
    public static final BigDecimal GB_VALUE = BASE_VALUE.pow(3);
    public static final BigDecimal TB_VALUE = BASE_VALUE.pow(4);
    public static final BigDecimal PB_VALUE = BASE_VALUE.pow(5);
    public static final BigDecimal EB_VALUE = BASE_VALUE.pow(6);

    /**
     * 从HDFS上下载文件或文件夹到本地
     *
     * @throws IOException
     **/
    public static void downloadFileFromHDFS(Configuration conf,String remotePath, String localDir) throws IOException {
        FileSystem fs = getFileSystem(conf);
        Path p1 = new Path(remotePath);
        Path p2 = new Path(localDir);
        fs.copyToLocalFile(p1, p2);
    }

    public static boolean uploadInputStreamToHdfs(Configuration conf,byte[] bytes, String hdfsPath) throws URISyntaxException, IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        FileSystem fs = getFileSystem(conf);
        Path destP = new Path(hdfsPath);
        FSDataOutputStream os = fs.create(destP);
        IOUtils.copyBytes(is, os, 4096, true);
        if(logger.isDebugEnabled()){
            logger.debug("submit file {} to hdfs success.", hdfsPath);
        }
        return true;
    }

    public static void uploadLocalFileToHdfs(Configuration conf,String localFilePath, String hdfsDir) throws Exception {
        FileSystem fs =getFileSystem(conf);
        Path resP = new Path(localFilePath);
        Path destP = new Path(hdfsDir);
        //hdfs路径不存在则创建
        String dir = hdfsDir.substring(0,hdfsDir.lastIndexOf("/")+1);
        if(!isDirExist(conf,dir)){
            fs.mkdirs(new Path(dir));
        }
        fs.copyFromLocalFile(resP, destP);
        if(logger.isDebugEnabled()){
            logger.info("submit file {} to hdfs {} success.", localFilePath, hdfsDir);
        }
    }

    public static boolean createDir(Configuration conf, String dir) throws Exception {
        dir = uri(dir);
        FileSystem fs = getFileSystem(conf);
        return fs.mkdirs(new Path(dir));
    }


    public static boolean createDir(Configuration conf,String dir,short permission) throws Exception{
        dir = uri(dir);
        FileSystem fs = getFileSystem(conf);
        return fs.mkdirs(new Path(dir),new FsPermission(permission));
    }


    public static FileStatus getFileStatus(Configuration conf, String dir) throws Exception {
        if (isFileExist(conf, dir)) {
            FileSystem fs = getFileSystem(conf);
            Path path = new Path(dir);
            return fs.getFileStatus(path);
        } else {
            throw new RdosDefineException(dir + " 文件不存在");
        }
    }

    public static boolean isFileExist(Configuration conf, String dir) throws Exception {
        dir = uri(dir);
        FileSystem fs = getFileSystem(conf);
        Path path = new Path(dir);
        return fs.exists(path) && fs.isFile(path);
    }

    /**
     * 文件检测并删除
     *
     * @return
     */
    public static boolean checkAndDele(Configuration conf,String fileUri) throws IOException, URISyntaxException {
        FileSystem fs = getFileSystem(conf);
        fileUri  = uri(fileUri);
        Path deletePath = new Path(fileUri);
        boolean result = false;
        if (fs.exists(deletePath)) {
            result = fs.delete(deletePath, true);
            if(logger.isInfoEnabled()){
                logger.info("delete file {} on hdfs success.", fileUri);
            }
        } else {
        	if(logger.isInfoEnabled()){
                logger.info("hdfs don't have file of path {}", fileUri);
        	}
            result = true;
        }
        return result;
    }

    public static String uri(String path){
        Pair<String, String> pair = parseHdfsUri(path);
        path = pair==null?path:pair.getRight();
        return path;
    }

    public static Pair<String, String> parseHdfsUri(String path) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.find() && matcher.groupCount() == 2) {
            String hdfsUri = matcher.group(1);
            String hdfsPath = matcher.group(2);
            return new MutablePair<>(hdfsUri, hdfsPath);
        }
        return null;
    }

    public static boolean checkConnection(Configuration conf) {

        FileSystem fs = null;
        boolean check = false;
        try {
            fs = getFileSystem(conf);
            fs.getStatus(new Path("/"));
            check = true;
        } catch (Exception e) {
        	logger.error("{}", e);
        }
        return check;
    }

    /**
     * @param size
     * @return
     */
    public static String unitConverter(long size) {
        BigDecimal sizeBig = BigDecimal.valueOf(size);
        double convertSize = 0;
        String unit = null;
        if (size == 0) {
            return "0";
        } else if (sizeBig.compareTo(MB_VALUE) == -1) {
            convertSize = sizeBig.divide(KB_VALUE).doubleValue();
            unit = "KB";
        } else if (sizeBig.compareTo(GB_VALUE) == -1) {
            convertSize = sizeBig.divide(MB_VALUE).doubleValue();
            unit = "MB";
        } else if (sizeBig.compareTo(TB_VALUE) == -1) {
            convertSize = sizeBig.divide(GB_VALUE).doubleValue();
            unit = "GB";
        } else if (sizeBig.compareTo(PB_VALUE) == -1) {
            convertSize = sizeBig.divide(TB_VALUE).doubleValue();
            unit = "TB";
        } else if (sizeBig.compareTo(EB_VALUE) == -1) {
            convertSize = sizeBig.divide(PB_VALUE).doubleValue();
            unit = "PB";
        } else {
            convertSize = sizeBig.divide(EB_VALUE).doubleValue();
            unit = "EB";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        if(df.format(convertSize).equals("0.00")){
            return "0";
        }else{
            return df.format(convertSize) + unit;
        }
    }

    public static long getDirSize(Configuration conf,String dir) {
        long size = 0;
        try {
        	FileSystem fs = getFileSystem(conf);
        	if(isDirExist(conf,dir)){
            	dir = uri(dir);
                size = fs.getContentSummary(new Path(dir)).getLength();
                logger.info(String.format("get dir size:%s",size));
        	}
        } catch (Exception e) {
        	logger.error("get dir size error:",e);
        }
        return size;
    }

    public static FileSystem getFileSystem(Configuration conf) throws IOException{
        com.dtstack.engine.master.utils.HadoopConf.setDefaultConf(conf);
        if(DtKerberosUtils.needLoginKerberos(conf)){
            try {
                DtKerberosUtils.loginKerberosHdfs(conf);
            } catch (Exception e){
                logger.error("kerberos认证失败:{}",e);
            }
        }

		return FileSystem.get(conf);
    }

    public static void deleteFile(Configuration conf,String fileName) throws Exception{
        deleteFiles(conf,Arrays.asList(fileName));
    }

    public static void deleteFiles(Configuration conf,List<String> fileNames) throws Exception{
        if(CollectionUtils.isNotEmpty(fileNames)){
            FileSystem fs = getFileSystem(conf);
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

    @SuppressWarnings("resource")
	public static boolean isDirExist(Configuration conf,String dir) throws Exception{
        dir = uri(dir);
        FileSystem fs = getFileSystem(conf);
        Path path = new Path(dir);
        return fs.exists(path) && fs.isDirectory(path);
    }


    public static void setPermission(Configuration conf,String path,String mode) throws Exception{
            FileSystem fs = getFileSystem(conf);
            fs.setPermission(new Path(path), new FsPermission(mode));
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }



    public static boolean rename(Configuration conf,String src,String dist) throws Exception {
        FileSystem fs = getFileSystem(conf);
        return fs.rename(new Path(src),new Path(dist));
    }

    public static void release(){
        try{
            if(tfs.get()!=null){
                tfs.get().close();
            }
            tfs.remove();
        }catch (Exception e){
            logger.error("",e);
        }
    }

    public static List<String> parsePartitionDataFromUrl(String path,List<String> partitionColumns){

        Map<String,String> partColDataMap = new HashMap<>();
        for (String part : path.split("/")) {
            if(part.contains("=")){
                String[] parts = part.split("=");
                partColDataMap.put(parts[0],parts[1]);
            }
        }

        List<String> data = new ArrayList<>();
        for (String partitionColumn : partitionColumns) {
            data.add(partColDataMap.get(partitionColumn));
        }

        return data;
    }

    public static void copyFile(Configuration conf,String src,String dist,boolean overwrite) throws Exception{
        FileSystem fs = getFileSystem(conf);
        Path srcPath = new Path(src);
        Path distPath = new Path(dist);

        if (fs.isDirectory(srcPath)){
            throw new RdosDefineException("不能复制目录");
        }

        if (fs.isDirectory(distPath)){
            throw new RdosDefineException("复制目标不能是目录");
        }

        if (overwrite){
            FSDataInputStream in = fs.open(srcPath);
            FSDataOutputStream os = fs.create(new Path(dist));
            IOUtils.copyBytes(in, os, 4096, true);
            if(logger.isDebugEnabled()){
                logger.debug("copy file {} to hdfs success.", src);
            }
        } else {
            if(fs.exists(distPath)){
                throw new RdosDefineException("文件：" + dist + " 已存在");
            }
        }
    }

    public static List<String> listAllFilePath(Configuration conf, String parentSrc) throws IOException {
        FileSystem fs = getFileSystem(conf);
        Path parentPath = new Path(parentSrc);
        if (!fs.isDirectory(parentPath)) {
            return new ArrayList<>();
        }
        List<String> allPathList = new ArrayList<>();
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fs.listFiles(parentPath, true);
        while(locatedFileStatusRemoteIterator.hasNext()) {
            LocatedFileStatus fileStatus = locatedFileStatusRemoteIterator.next();
            allPathList.add(fileStatus.getPath().toString());
        }
        return allPathList;
    }

    public static List<FileStatus> listFiles(Configuration conf, String parentSrc) throws IOException {
        return listFiles(conf, parentSrc, false);
    }

    public static List<FileStatus> listFiles(Configuration conf, String parentSrc, boolean isIterate) throws IOException {
        FileSystem fs = getFileSystem(conf);
        Path parentPath = new Path(parentSrc);
        if (!fs.isDirectory(parentPath)) {
            return new ArrayList<>();
        }
        List<FileStatus> allPathList = new ArrayList<>();
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fs.listFiles(parentPath, isIterate);
        while (locatedFileStatusRemoteIterator.hasNext()) {
            LocatedFileStatus fileStatus = locatedFileStatusRemoteIterator.next();
            allPathList.add(fileStatus);
        }
        return allPathList;
    }


    public static void copyToLocal(Configuration conf, String srcPath, String dstPath) throws IOException {
        FileSystem fs = getFileSystem(conf);
        fs.copyToLocalFile(false, new Path(srcPath), new Path(dstPath));
    }

    public static void copyFromLocal(Configuration conf, String srcPath, String dstPath, boolean overwrite) throws IOException {
        FileSystem fs = getFileSystem(conf);
        fs.copyFromLocalFile(true, overwrite, new Path(srcPath), new Path(dstPath));
    }

    public static void copyInDFSs(Configuration srcConf, Configuration dstConf, String srcPath, String dstPath, String localPath, boolean overwrite) throws IOException {
        copyToLocal(srcConf, srcPath, localPath);
        copyFromLocal(dstConf, localPath, dstPath, overwrite);
    }

}
