package com.dtstack.engine.master.utils;

import com.dtstack.dtcenter.common.hadoop.HdfsOperator;
import com.dtstack.dtcenter.common.sftp.SFTPHandler;
import com.dtstack.dtcenter.common.util.Xml2JsonUtil;
import com.dtstack.dtcenter.common.util.ZipUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/2/25 21:54
 * @Description:
 */
public class XmlFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileUtil.class);
    private final static List<String> BASE_XML = Lists.newArrayList( "core-site.xml", "hdfs-site.xml", "yarn-site.xml");
    private final static String XML_SUFFIX = ".xml";

    public static List<File> filterXml(List<File> xmlFiles, List<String> validXml) {
        if (xmlFiles == null) {
            throw new RdosDefineException("缺少必要配置文件：" + StringUtils.join(BASE_XML, ","));
        }
        if(null == validXml){
            validXml = Collections.emptyList();
        }
        xmlFiles = xmlFiles.stream().filter(f -> {
            return (!f.getName().startsWith(".") && f.getName().endsWith(XML_SUFFIX));
        }).collect(Collectors.toList());
        List<String> checkFiles = new ArrayList<>(BASE_XML);
        for (File file : xmlFiles) {
            String fileName = file.getName();
            if (checkFiles.contains(fileName)) {
                checkFiles.remove(fileName);
            }
            if(validXml.contains(fileName)){
                validXml.remove(fileName);
            }
        }
        if (!checkFiles.isEmpty() || !validXml.isEmpty()) {
            throw new RdosDefineException("缺少必要配置文件：" + StringUtils.join(checkFiles, ",")+StringUtils.join(validXml, ","));
        }
        return xmlFiles;
    }

    public static List<File> getFilesFromZip(String zipLocation, String unzipLocation,List<String> validXml) {
        try {
            List<File> xmlFiles = ZipUtil.upzipFile(zipLocation, unzipLocation);
            xmlFiles = filterXml(xmlFiles, validXml);
            return xmlFiles;
        } catch (Exception e) {
            LOGGER.error("{}", e);
            throw new RdosDefineException("压缩包解压失败");
        }
    }

    /**
     * 解析并读取配置文件内容
     *
     * @param xmlFiles
     * @return
     * @throws Exception
     */
    public static Map<String, Object> parseAndRead(List<File> xmlFiles) throws Exception {
        Map<String, Object> xmlMaps = new HashMap<>();
        for (File file : xmlFiles) {
            Map<String, Object> xmlMap = Xml2JsonUtil.xml2map(file);
            xmlMaps.putAll(xmlMap);
        }
        return xmlMaps;
    }

    public static void uploadConfig2HDFD(String hdfsDir, List<File> xmlFiles, Configuration configuration) {
        if (xmlFiles != null && configuration != null) {
            try {
                //删除hdfs目录下全部文件，重新新建文件夹，并上传文件
                HdfsOperator.checkAndDele(configuration, hdfsDir);
                HdfsOperator.createDir(configuration, hdfsDir);
                for (File xmlFile : xmlFiles) {
                    HdfsOperator.uploadLocalFileToHdfs(configuration, xmlFile.getPath(), hdfsDir);
                }
            } catch (Exception e) {
                LOGGER.error("{}", e);
                throw new RdosDefineException("配置文件上传至HDFS失败，可能原因Hadoop配置不正确");
            } finally {
                HdfsOperator.release();
            }
        }
    }


    public static void uploadConfig2SFTP(String sftpDir, List<File> xmlFiles, Map<String, String> sftpConfig) {
        if (xmlFiles != null) {
            SFTPHandler instance = null;
            try {
                instance = SFTPHandler.getInstance(sftpConfig);
                //删除hdfs目录下全部文件，重新新建文件夹，并上传文件
                instance.deleteDir(sftpDir);
                instance.mkdir(sftpDir);
                for (File xmlFile : xmlFiles) {
                    instance.upload(sftpDir, xmlFile.getPath(), true);
                }
            } catch (Exception e) {
                LOGGER.error("uploadConfig2SFTP error {}", e);
                throw new RdosDefineException("配置文件上传至SFTP失败");
            } finally {
                if (instance != null){
                    instance.close();
                }
            }
        }
    }
}
