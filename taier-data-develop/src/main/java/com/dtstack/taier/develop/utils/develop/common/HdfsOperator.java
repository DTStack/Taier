/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.utils.develop.common;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IHdfsFile;
import com.dtstack.dtcenter.loader.dto.FileStatus;
import com.dtstack.dtcenter.loader.dto.HDFSContentSummary;
import com.dtstack.dtcenter.loader.dto.source.HdfsSourceDTO;
import com.dtstack.dtcenter.loader.enums.FileFormat;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author sishu.yss
 */
public class HdfsOperator {

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

   public static class HadoopConf {

	  private  Map<String,Object> conf = new HashMap<>(128);

	  public Map<String,Object> setConf(Properties properties){
	        if(properties!=null){
		        Set<Map.Entry<Object,Object>> entrys = properties.entrySet();
		        for(Map.Entry<Object,Object> entry:entrys){
		        	conf.put(entry.getKey().toString(), entry.getValue().toString());
		        }
	        }
	        return conf;
	  }

	  public Map<String,Object> setConf(String defaultFs,Properties properties) {
	        conf.put("fs.defaultFS", defaultFs);

          String canonicalHostName = null;
          try {
              canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
          } catch (UnknownHostException e) {
              throw new DtCenterDefException(String.format("获取本地地址失败，原因是：%s", e.getMessage()));
          }
          if(properties!=null){
		        Set<Map.Entry<Object,Object>> entrys = properties.entrySet();
		        for(Map.Entry<Object,Object> entry:entrys){
                    String value = entry.getValue().toString();
                    if (value.contains("_HOST")) {
                        value = value.replace("_HOST", canonicalHostName);
                    }
		        	conf.put(entry.getKey().toString(), value);
		        }
	        }
	        return conf;
	  }

   }

    private static HdfsSourceDTO getSourceDTO(Map<String,Object> conf, Map<String,Object> kerberos){
        conf = Objects.isNull(conf) ? Maps.newHashMap() : conf;
        HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO.builder()
                .defaultFS(conf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberos)
                .config(JSONObject.toJSONString(conf)).build();
        return hdfsSourceDTO;
    }

    /**
     * 从HDFS上下载文件或文件夹到本地
     *
     * @throws IOException
     **/
    public static void downloadFileFromHDFS(Map<String,Object> conf,Map<String,Object> kerberos,String remotePath, String localDir) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getPluginName());
        hdfsClient.downloadFileFromHdfs(sourceDTO,remotePath,localDir);
    }

    public static boolean uploadInputStreamToHdfs(Map<String,Object> conf,Map<String,Object> kerberos,byte[] bytes, String hdfsPath) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.uploadInputStreamToHdfs(sourceDTO, bytes, hdfsPath);
    }

    public static void uploadLocalFileToHdfs(Map<String,Object> conf,Map<String,Object> kerberos,String localFilePath, String hdfsDir) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        hdfsClient.uploadLocalFileToHdfs(sourceDTO,localFilePath,hdfsDir);
    }

    public static boolean createDir(Map<String,Object> conf,Map<String,Object> kerberos, String dir) {
        dir = uri(dir);
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.createDir(sourceDTO,dir,null);
    }


    public static boolean createDir(Map<String,Object> conf,Map<String,Object> kerberos,String dir,short permission) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        dir = uri(dir);
        return hdfsClient.createDir(sourceDTO,dir,permission);
    }


    public static FileStatus getFileStatus(Map<String,Object> conf, Map<String,Object> kerberos, String dir) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        if (isFileExist(conf, kerberos,dir)) {
           return hdfsClient.getStatus(sourceDTO,uri(dir));
        } else {
            throw new DtCenterDefException(dir + " 文件不存在");
        }
    }

    public static boolean isFileExist(Map<String,Object> conf,Map<String,Object> kerberos, String dir) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        dir = uri(dir);
        return hdfsClient.isFileExist(sourceDTO,dir);
    }

    /**
     * 文件检测并删除,递归删除
     *
     * @return
     */
    public static boolean checkAndDele(Map<String,Object> conf,Map<String,Object> kerberos,String fileUri) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.checkAndDelete(sourceDTO, fileUri);
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

    public static boolean checkConnection(Map<String,Object> conf,Map<String,Object> kerberos,String defaultFs) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        if (StringUtils.isNotEmpty(defaultFs)){
            sourceDTO.setDefaultFS(defaultFs);
        }
        IClient hdfsClient = ClientCache.getClient(DataSourceType.HDFS.getVal());
        return hdfsClient.testCon(sourceDTO);
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

    public static long getDirSize(Map<String,Object> conf,Map<String,Object> kerberos,String dir) {
        long size = 0;
        try {
            HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
            IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
            size = hdfsClient.getDirSize(sourceDTO, dir);
        } catch (Exception e) {
        	logger.error("get dir size error:",e);
        }
        return size;
    }

    /**
     * 获取hdfs上指定路径的ContentSummary
     *
     * @param conf
     * @param kerberos
     * @param targetPath
     * @return
     */
    public static HDFSContentSummary getContentSummary(Map<String,Object> conf, Map<String,Object> kerberos, String targetPath) {
        try {
            HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
            IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
            return hdfsClient.getContentSummary(sourceDTO, targetPath);
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("获取hdfs文件内容摘要异常!原因是：%s", e.getMessage()));
        }
    }

    /**
     * 获取hdfs上指定路径集合的ContentSummary
     *
     * @param conf
     * @param kerberos
     * @param targetPaths
     * @return
     */
    public static List<HDFSContentSummary> getContentSummary(Map<String,Object> conf, Map<String,Object> kerberos, List<String> targetPaths) {
        try {
            HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
            IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
            return hdfsClient.getContentSummary(sourceDTO, targetPaths);
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("获取hdfs文件内容摘要异常!原因是：%s", e.getMessage()));
        }
    }

    /**
     * 获取hdfs上指定路径集合的ContentSummary
     *
     * @param conf hadoop配置
     * @param kerberos kerberos配置
     * @param src 原路径
     * @param dist 复制路径
     */
    public static void copyDirector(Map<String,Object> conf, Map<String,Object> kerberos, String src, String dist) {
        try {
            HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
            IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
            hdfsClient.copyDirector(sourceDTO, src, dist);
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("hdfs内复制文件夹异常!原因是：%s", e.getMessage()));
        }
    }

    /**
     * 获取hdfs上指定路径集合的ContentSummary
     *
     * @param conf hadoop配置
     * @param kerberos kerberos配置
     * @param src 原路径
     * @param mergePath 合并临时目录
     * @param fileFormat 文件存储格式
     * @param needCombineFileSizeLimit 需要合并的文件大小阈值
     * @param maxCombinedFileSize 合并后的最大大小
     */
    public static void fileMerge(Map<String,Object> conf, Map<String,Object> kerberos, String src, String mergePath, FileFormat fileFormat, Long maxCombinedFileSize, Long needCombineFileSizeLimit) {
        try {
            HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
            IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
            hdfsClient.fileMerge(sourceDTO, src, mergePath, fileFormat, maxCombinedFileSize, needCombineFileSizeLimit);
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("文件合并异常!原因是：%s", e.getMessage()));
        }
    }

    public static void deleteFile(Map<String,Object> conf,Map<String,Object> kerberos,String fileName) {
        deleteFiles(conf,kerberos,Arrays.asList(fileName));
    }

    /**
     * 直接删除hdfs上文件
     * @param conf hadoop文件
     * @param kerberos kerberos文件
     * @param remotePath hdfs路径
     * @param recursive 是否递归删除
     * @return 删除结果
     * @throws Exception 异常
     */
    public static boolean delete(Map<String,Object> conf,Map<String,Object> kerberos, String remotePath, boolean recursive) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.delete(sourceDTO, remotePath, recursive);
    }

    public static boolean deleteFiles(Map<String,Object> conf,Map<String,Object> kerberos,List<String> fileNames) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.deleteFiles(sourceDTO,fileNames);
    }

    @SuppressWarnings("resource")
	public static boolean isDirExist(Map<String,Object> conf,Map<String,Object> kerberos,String dir){
        dir = uri(dir);
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.isDirExist(sourceDTO,dir);
    }


    public static void setPermission(Map<String,Object> conf,Map<String,Object> kerberos,String path,String mode) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        hdfsClient.setPermission(sourceDTO,path,mode);
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



    public static boolean rename(Map<String,Object> conf,Map<String,Object> kerberos,String src,String dist) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.rename(sourceDTO,src,dist);
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

    public static void copyFile(Map<String,Object> conf,Map<String,Object> kerberos,String src,String dist,boolean overwrite) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        hdfsClient.copyFile(sourceDTO,src,dist,overwrite);
    }

    public static List<String> listAllFilePath(Map<String,Object> conf,Map<String,Object> kerberos, String parentSrc) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.listAllFilePath(sourceDTO,parentSrc);
    }

    /**
     * 获取 HDFS 文件或者文件夹状态
     *
     * @param conf
     * @param kerberos
     * @param parentSrc
     * @return
     * @throws Exception
     */
    public static List<FileStatus> listStatus(Map<String, Object> conf, Map<String, Object> kerberos, String parentSrc) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getPluginName());
        return hdfsClient.listStatus(sourceDTO, parentSrc);
    }

    public static List<FileStatus> listFiles(Map<String,Object> conf, Map<String,Object> kerberos, String parentSrc) {
        return listFiles(conf,kerberos,parentSrc, false);
    }

    public static List<FileStatus> listFiles(Map<String,Object> conf, Map<String,Object> kerberos, String parentSrc, boolean isIterate) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.listAllFiles(sourceDTO,parentSrc,isIterate);
    }


    public static void copyToLocal(Map<String,Object> conf,Map<String,Object> kerberos, String srcPath, String dstPath) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        hdfsClient.copyToLocal(sourceDTO,srcPath,dstPath);
    }

    public static void copyFromLocal(Map<String,Object> conf,Map<String,Object> kerberos, String srcPath, String dstPath, boolean overwrite) {
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        hdfsClient.copyFromLocal(sourceDTO,srcPath,dstPath,overwrite);
    }

    public static void copyInDFSs(Map<String,Object> srcConf, Map<String,Object> kerberos,Map<String,Object> dstConf, Map<String,Object> dstKerberos,String srcPath, String dstPath, String dstFileName, String localPath, boolean overwrite) {
        copyToLocal(srcConf,kerberos,srcPath,localPath);
        copyFromLocal(dstConf,dstKerberos, String.format("%s%s%s", localPath, File.separator, dstFileName) ,dstPath,overwrite);
    }

    public static IHdfsFile getHdfsFileClient(Map<String,Object> conf, Map<String,Object> kerberos){
        HdfsSourceDTO sourceDTO = getSourceDTO(conf, kerberos);
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient;
    }

}
