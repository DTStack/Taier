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

package com.dtstack.taiga.develop.controller.console;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.lang.coc.APITemplate;
import com.dtstack.taiga.common.lang.web.R;
import com.dtstack.taiga.dao.dto.Resource;
import com.dtstack.taiga.develop.service.console.ConsoleComponentService;
import com.dtstack.taiga.scheduler.vo.ComponentVO;
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

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/node/upload")
@Api(value = "/node/upload", tags = {"上传接口"})
public class UploadController {
    @Autowired
    private ConsoleComponentService consoleComponentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    private static String uploadsDir = System.getProperty("user.dir") + File.separator + "file-uploads";

    @PostMapping(value="/component/config")
    @ApiOperation(value = "解析zip中xml或者json")
    public R<List<Object>> upload(@RequestParam("fileName") List<MultipartFile> files, @RequestParam("componentType") Integer componentType,
                               @RequestParam(value = "autoDelete", required = false) Boolean autoDelete,@RequestParam(value = "version", required = false) String componentVersion) {
        return R.ok(consoleComponentService.config(getResourcesFromFiles(files), componentType, autoDelete,componentVersion));
    }

    @PostMapping(value="/component/addOrUpdateComponent")
    public R<ComponentVO> addOrUpdateComponent(@RequestParam("resources1") List<MultipartFile> files1, @RequestParam("resources2") List<MultipartFile> files2, @RequestParam("clusterId") Long clusterId,
                                            @RequestParam(value = "componentConfig") String componentConfig, @RequestParam("versionName")@NotNull String versionName,
                                            @RequestParam("kerberosFileName") String kerberosFileName, @RequestParam("componentTemplate") String componentTemplate,
                                            @RequestParam("componentCode") Integer componentCode, @RequestParam("storeType")Integer storeType,
                                            @RequestParam("principals")String principals,@RequestParam("principal")String principal,@RequestParam("isMetadata")boolean isMetadata,
                                            @RequestParam(value = "isDefault",required = false) Boolean isDefault,@RequestParam(value = "deployType",required = false)Integer deployType) {
        List<Resource> resources = getResourcesFromFiles(files1);
        List<Resource> resourcesAdd = getResourcesFromFiles(files2);
        resources.addAll(resourcesAdd);
        return new APITemplate<ComponentVO>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                if (null == componentCode) {
                    throw new RdosDefineException("Component type cannot be empty");
                }
                if (null == clusterId) {
                    throw new RdosDefineException("Cluster Id cannot be empty");
                }
                if (CollectionUtils.isNotEmpty(resources) && resources.size() >= 2 && StringUtils.isBlank(kerberosFileName)) {
                    //上传二份文件 需要kerberosFileName文件名字段
                    throw new RdosDefineException("kerberosFileName不能为空");
                }
                //校验引擎是否添加
                if (EComponentType.deployTypeComponents.contains(componentCode) && null == deployType) {
                    throw new RdosDefineException(ErrorCode.EMPTY_PARAMETERS.getMsg() + ":deployType");
                }
            }

            @Override
            protected ComponentVO process() throws RdosDefineException {
                //校验引擎是否添加
                EComponentType componentType = EComponentType.getByCode(componentCode);
                if (EComponentType.deployTypeComponents.contains(componentType) && null == deployType) {
                    throw new RdosDefineException("deploy type cannot be empty");
                }
                return consoleComponentService.addOrUpdateComponent(clusterId, componentConfig, resources,
                        versionName, kerberosFileName, componentTemplate, componentType, storeType, principals, principal, isMetadata, isDefault, deployType);
            }
        }.execute();


    }

    @PostMapping(value="/component/parseKerberos")
    @ApiOperation(value = "解析kerberos文件中信息")
    public R<List<String>> parseKerberos(@RequestParam("fileName") List<MultipartFile> files) {
        return R.ok(consoleComponentService.parseKerberos(getResourcesFromFiles(files)));
    }

    @PostMapping(value="/component/uploadKerberos")
    public R<String> uploadKerberos(@RequestParam("kerberosFile") List<MultipartFile> files, @RequestParam("clusterId") Long clusterId,
                                 @RequestParam("componentCode") Integer componentCode,@RequestParam("componentVersion") String componentVersion) {
        List<Resource> resources = getResourcesFromFiles(files);
        return R.ok(consoleComponentService.uploadKerberos(resources, clusterId, componentCode,componentVersion));
    }

    private List<Resource> getResourcesFromFiles(List<MultipartFile> files) {
        List<Resource> resources = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            String fileOriginalName = file.getOriginalFilename();
            String path =  uploadsDir + File.separator + fileOriginalName;
            File saveFile = new File(path);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                file.transferTo(saveFile);
            } catch (Exception e) {
                LOGGER.error("" , e);
                throw new RdosDefineException("An error occurred while storing the file");
            }
            resources.add(new Resource(fileOriginalName, path, (int) file.getSize(), file.getContentType(), file.getName()));
        }
        return resources;
    }
}
