package com.dtstack.engine.master.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.lang.base.Strings;
import com.dtstack.engine.common.util.Xml2JsonUtil;
import com.dtstack.engine.common.util.ZipUtil;
import com.dtstack.engine.dto.Resource;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.*;

public class ResourceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtil.class);

    public static List<Object> parseKubernetesData(List<Resource> resources) {
        // 单个 zip 包
        Resource resource = resources.get(0);
        //解压缩获得配置文件
        String xmlZipLocation = resource.getUploadedFileName();
        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        //解析zip 带换行符号
        List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
        if (CollectionUtils.isEmpty(xmlFiles)) {
            return Collections.emptyList();
        }
        String content = Strings.EMPTY_STRING;
        try {
            content = FileUtil.getContentFromFile(xmlFiles.get(0).getPath());
        } catch (Exception e) {
            LOGGER.error("parse Kubernetes resource error {} ", resource.getUploadedFileName(), e);
        }
        return StringUtils.isEmpty(content) ? Collections.emptyList() : Lists.newArrayList(content);
    }

    public static List<Object> parseJsonFile(List<Resource> resources) {
        List<Object> data = new ArrayList<>();
        // 当作json来解析
        for (Resource resource : resources) {
            try {
                String fileInfo = FileUtils.readFileToString(new File(resource.getUploadedFileName()));
                data.add(PublicUtil.strToMap(fileInfo));
            } catch (Exception e) {
                throw new RdosDefineException("JSON file format error, file name:" + resource.getUploadedFileName(), e);
            }
        }
        return data;
    }

    /**
     * @param resources 单个 zip 包
     * @param mustHasXmlNames zip 包中必须包含的文件名
     * @return
     */
    public static List<Object> parseXmlFileConfig(List<Resource> resources, List<String> mustHasXmlNames) {
        // resources 是单个 zip 包
        Map<String, Map<String,Object>> xmlConfigMap = parseUploadFileToMap(resources);
        boolean isLostXmlFile = xmlConfigMap.keySet().containsAll(mustHasXmlNames);
        if(!isLostXmlFile){
            throw new RdosDefineException("Missing necessary configuration file, maybe the Zip file corrupt, please retry zip files.");
        }
        //多个配置文件合并为一个 map
        Map<String,Object> data = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : xmlConfigMap.entrySet()) {
            data.putAll(entry.getValue());
        }
        return Lists.newArrayList(data);
    }

    public static Map<String, Map<String, Object>> parseUploadFileToMap(List<Resource> resources) {
        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("The uploaded file cannot be empty");
        }
        // 获取到 zip 包
        Resource resource = resources.get(0);
        if (!resource.getFileName().endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException("The compressed package format only supports ZIP format");
        }

        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        Map<String, Map<String, Object>> confMap = new HashMap<>();
        String xmlZipLocation = resource.getUploadedFileName();
        try {
            //解压缩获得配置文件
            List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
            if (CollectionUtils.isEmpty(xmlFiles)) {
                throw new RdosDefineException("The configuration file cannot be empty");
            }
            for (File file : xmlFiles) {
                Map<String, Object> fileMap = null;
                if (file.getName().startsWith(DOT)) {
                    //.开头过滤
                    continue;
                }
                if (file.getName().endsWith(XML_SUFFIX)) {
                    //xml文件
                    fileMap = Xml2JsonUtil.xml2map(file);
                } else if (file.getName().endsWith(JSON_SUFFIX)) {
                    //json文件
                    String jsonStr = Xml2JsonUtil.readFile(file);
                    if (StringUtils.isBlank(jsonStr)) {
                        continue;
                    }
                    fileMap = (Map<String, Object>) JSONObject.parseObject(jsonStr, Map.class);
                }
                if (null != fileMap) {
                    confMap.put(file.getName(), fileMap);
                }
            }
            return confMap;
        } catch (Exception e) {
            FileUtil.LOGGER.error("parseAndUploadXmlFile file error ", e);
            throw new RdosDefineException(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (StringUtils.isNotBlank(upzipLocation)) {
                ZipUtil.deletefile(upzipLocation);
            }
        }
    }

    /**
     * 解析对应的kerberos的zip中principle
     *
     * @param resourcesFromFiles
     * @return
     */
    public static List<String> parseKerberos(List<Resource> resourcesFromFiles) {
        if (CollectionUtils.isEmpty(resourcesFromFiles)) {
            return Collections.emptyList();
        }
        Resource resource = resourcesFromFiles.get(0);
        String unzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            //解压到本地
            List<File> files = ZipUtil.upzipFile(resource.getUploadedFileName(), unzipLocation);
            if (CollectionUtils.isEmpty(files)) {
                throw new RdosDefineException("Hadoop-Kerberos file decompression error");
            }

            File fileKeyTab = files.stream()
                    .filter(f -> f.getName().endsWith(KEYTAB_SUFFIX))
                    .findFirst()
                    .orElse(null);
            if (fileKeyTab == null) {
                throw new RdosDefineException("There must be a keytab file in the zip file of the uploaded Hadoop-Kerberos file, please add the keytab file");
            }
            //获取principal
            List<PrincipalName> principal = FileUtil.getPrincipal(fileKeyTab);
            return principal.stream()
                    .map(PrincipalName::getName)
                    .collect(Collectors.toList());
        } finally {
            try {
                FileUtils.deleteDirectory(new File(unzipLocation));
            } catch (IOException e) {
                LOGGER.error("delete update file {} error", unzipLocation);
            }
        }
    }

}
