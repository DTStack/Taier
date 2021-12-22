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

package com.dtstack.batch.controller.console;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.utils.FileUtils;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.dto.Resource;
import com.dtstack.engine.master.impl.ComponentFileService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.utils.ResourceUtil;
import com.dtstack.engine.master.vo.ComponentVO;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.ZIP_SUFFIX;

@RestController
@RequestMapping("/node/upload")
@Api(value = "/node/upload", tags = {"上传接口"})
public class UploadController {
    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentFileService componentFileService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @PostMapping(value = "/component/config")
    @ApiOperation(value = "解析zip中xml或者json")
    public List<Object> upload(@RequestParam("fileName") List<MultipartFile> files, @RequestParam("componentType") Integer componentType,
                               @RequestParam(value = "autoDelete", required = false) Boolean autoDelete, @RequestParam(value = "version", required = false) String componentVersion) {
        return componentFileService.config(getResourcesFromFiles(files), componentType, autoDelete, componentVersion);
    }

    @PostMapping(value = "/component/addOrUpdateComponent")
    public ComponentVO addOrUpdateComponent(@RequestParam("resources1") List<MultipartFile> files1, @RequestParam("resources2") List<MultipartFile> files2, @RequestParam("clusterId") Long clusterId,
                                            @RequestParam("componentConfig") String componentConfig, @RequestParam("hadoopVersion") String hadoopVersion,
                                            @RequestParam("kerberosFileName") String kerberosFileName, @RequestParam("componentTemplate") String componentTemplate,
                                            @RequestParam("componentCode") Integer componentCode, @RequestParam("storeType") Integer storeType,
                                            @RequestParam("principals") String principals, @RequestParam("principal") String principal, @RequestParam("isMetadata") boolean isMetadata,
                                            @RequestParam(value = "isDefault", required = false) Boolean isDefault, @RequestParam(value = "deployType", required = false) Integer deployType) {
        if (null == componentCode) {
            throw new RdosDefineException("Component type cannot be empty");
        }
        if (null == clusterId) {
            throw new RdosDefineException("Cluster Id cannot be empty");
        }

        List<Resource> resources = getResourcesFromFiles(files1);
        List<Resource> resourcesAdd = getResourcesFromFiles(files2);
        resources.addAll(resourcesAdd);
        if (CollectionUtils.isNotEmpty(resources) && resources.size() >= 2 && StringUtils.isBlank(kerberosFileName)) {
            //上传二份文件 需要kerberosFileName文件名字段
            throw new RdosDefineException("kerberosFileName不能为空");
        }
        if (null == componentConfig) {
            componentConfig = new JSONObject().toJSONString();
        }
        //校验引擎是否添加
        EComponentType componentType = EComponentType.getByCode(componentCode);
        if (EComponentType.deployTypeComponents.contains(componentType) && null == deployType) {
            throw new RdosDefineException("deploy type cannot be empty");
        }
        return componentService.addOrUpdateComponent(clusterId, componentConfig, resources, hadoopVersion, kerberosFileName, componentTemplate, componentType, storeType, principals, principal, isMetadata, isDefault, deployType);
    }

    @PostMapping(value = "/component/parseKerberos")
    @ApiOperation(value = "解析kerberos文件中信息")
    public List<String> parseKerberos(@RequestParam("fileName") List<MultipartFile> files) {
        return ResourceUtil.parseKerberos(getResourcesFromFiles(files));
    }

    @PostMapping(value = "/component/uploadKerberos")
    public String uploadKerberos(@RequestParam("kerberosFile") List<MultipartFile> files, @RequestParam("clusterId") Long clusterId,
                                 @RequestParam("componentCode") Integer componentCode, @RequestParam("componentVersion") String componentVersion) {
        List<Resource> resources = getResourcesFromFiles(files);

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("Please upload a kerberos file!");
        }
        Resource resource = resources.get(0);
        String kerberosFileName = resource.getFileName();
        if (!kerberosFileName.endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException("Kerberos upload files are not in zip format");
        }
        String sftpComponent = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(),
                false, String.class,null);
        return componentFileService.uploadKerberos(resource, clusterId, componentCode, componentVersion, sftpComponent);
    }

    private List<Resource> getResourcesFromFiles(List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }

        FileUtils.upload(files, FileUtils.uploadsDir);

        return files.stream().map(f -> {
            String uploadedFileName = f.getOriginalFilename();
            String fileFullPath = FileUtils.uploadsDir + File.separator + uploadedFileName;
            return new Resource(uploadedFileName, fileFullPath, (int) f.getSize(), f.getContentType(), f.getName());
        }).collect(Collectors.toList());
    }
}
