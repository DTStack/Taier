package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.util.ZipUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
            return CollectionUtils.isEmpty(validXml)? xmlFiles : filterXml(xmlFiles, validXml);
        } catch (Exception e) {
            LOGGER.error("{}", e);
            throw new RdosDefineException("压缩包解压失败");
        }
    }

}
