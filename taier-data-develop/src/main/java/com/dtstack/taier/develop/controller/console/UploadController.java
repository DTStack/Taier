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

package com.dtstack.taier.develop.controller.console;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.dto.Resource;
import com.dtstack.taier.develop.service.console.ConsoleComponentService;
import com.dtstack.taier.scheduler.vo.ComponentVO;
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
@RequestMapping("/upload")
@Api(value = "/upload", tags = {"上传接口"})
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
                                               @RequestParam("kerberosFileName") String kerberosFileName,
                                               @RequestParam("componentCode") Integer componentCode,
                                               @RequestParam("principals")String principals, @RequestParam("principal")String principal, @RequestParam(value = "isMetadata",defaultValue = "false")Boolean isMetadata,
                                               @RequestParam(value = "isDefault",defaultValue = "false") Boolean isDefault, @RequestParam(value = "deployType")Integer deployType) {
        List<Resource> resources = getResourcesFromFiles(files1);
        List<Resource> resourcesAdd = getResourcesFromFiles(files2);
        resources.addAll(resourcesAdd);
        if (StringUtils.isBlank(componentConfig)) {
            componentConfig = new JSONObject().toJSONString();
        }
        String finalComponentConfig = componentConfig;
        return new APITemplate<ComponentVO>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                if (null == componentCode) {
                    throw new TaierDefineException("Component type cannot be empty");
                }
                if (null == clusterId) {
                    throw new TaierDefineException("Cluster Id cannot be empty");
                }
                if (CollectionUtils.isNotEmpty(resources) && resources.size() >= 2 && StringUtils.isBlank(kerberosFileName)) {
                    //上传二份文件 需要kerberosFileName文件名字段
                    throw new TaierDefineException("kerberosFileName不能为空");
                }
            }

            @Override
            protected ComponentVO process() throws TaierDefineException {
                //校验引擎是否添加
                String finalVersionName = versionName;
                EComponentType componentType = EComponentType.getByCode(componentCode);
                if (EComponentType.HDFS == componentType) {
                    //hdfs的组件和yarn组件的版本保持强一致
                    Component yarnComponent = consoleComponentService.getByClusterIdAndComponentType(clusterId, EComponentType.YARN.getTypeCode());
                    if (null != yarnComponent) {
                        finalVersionName = yarnComponent.getVersionName();
                    }
                }
                //存储只能配置hdfs

                //todo 参数处理下，第一次保存 deployType 赋值 0
                Integer deployTypeCode = deployType;
                if ("1.12-standalone".equals(versionName)) {
                    deployTypeCode = 0;
                }
                return consoleComponentService.addOrUpdateComponent(clusterId, finalComponentConfig, resources,
                        finalVersionName, kerberosFileName, componentType, EComponentType.HDFS.getTypeCode(), principals, principal, isMetadata, isDefault, deployTypeCode);
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
                                 @RequestParam("componentCode") Integer componentCode,@RequestParam("versionName") String versionName) {
        List<Resource> resources = getResourcesFromFiles(files);
        return R.ok(consoleComponentService.uploadKerberos(resources, clusterId, componentCode,versionName));
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
                throw new TaierDefineException("An error occurred while storing the file");
            }
            resources.add(new Resource(fileOriginalName, path, (int) file.getSize(), file.getContentType(), file.getName()));
        }
        return resources;
    }
}
