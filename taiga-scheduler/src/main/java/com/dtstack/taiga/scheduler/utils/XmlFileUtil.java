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

package com.dtstack.taiga.scheduler.utils;

import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.util.ZipUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            throw new RdosDefineException("The necessary configuration file is missing：" + StringUtils.join(BASE_XML, ","));
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
            throw new RdosDefineException("The necessary profile is missing：" + StringUtils.join(checkFiles, ",")+StringUtils.join(validXml, ","));
        }
        return xmlFiles;
    }

    public static List<File> getFilesFromZip(String zipLocation, String unzipLocation,List<String> validXml) {
        try {
            List<File> xmlFiles = ZipUtil.upzipFile(zipLocation, unzipLocation);
            return CollectionUtils.isEmpty(validXml)? xmlFiles : filterXml(xmlFiles, validXml);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RdosDefineException("Failed to decompress the compressed package");
        }
    }

}
