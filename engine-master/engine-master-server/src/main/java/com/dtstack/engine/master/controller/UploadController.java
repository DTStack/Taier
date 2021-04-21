package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ComponentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/node/upload")
@Api(value = "/node/upload", tags = {"上传接口"})
public class UploadController {
    @Autowired
    private ComponentService componentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    private static String uploadsDir = System.getProperty("user.dir") + File.separator + "file-uploads";

    @RequestMapping(value="/component/config", method = {RequestMethod.POST})
    @ApiOperation(value = "解析zip中xml或者json")
    public List<Object> upload(@RequestParam("fileName") List<MultipartFile> files, @RequestParam("componentType") Integer componentType,
                               @RequestParam(value = "autoDelete", required = false) Boolean autoDelete,@RequestParam(value = "version", required = false) String componentVersion) {
        return componentService.config(getResourcesFromFiles(files), componentType, autoDelete,componentVersion);
    }

    @RequestMapping(value="/component/addOrUpdateComponent", method = {RequestMethod.POST})
    public ComponentVO addOrUpdateComponent(@RequestParam("resources1") List<MultipartFile> files1, @RequestParam("resources2") List<MultipartFile> files2, @RequestParam("clusterId") Long clusterId,
                                            @RequestParam("componentConfig") String componentConfig, @RequestParam("hadoopVersion") String hadoopVersion,
                                            @RequestParam("kerberosFileName") String kerberosFileName, @RequestParam("componentTemplate") String componentTemplate,
                                            @RequestParam("componentCode") Integer componentCode, @RequestParam("storeType")Integer storeType,
                                            @RequestParam("principals")String principals, @RequestParam("principal")String principal,@RequestParam(value = "isDefault",required = false) Boolean isDefault) {
        List<Resource> resources = getResourcesFromFiles(files1);
        List<Resource> resourcesAdd = getResourcesFromFiles(files2);
        resources.addAll(resourcesAdd);
        return componentService.addOrUpdateComponent(clusterId, componentConfig, resources, hadoopVersion, kerberosFileName, componentTemplate, componentCode,storeType,principals,principal,isDefault);
    }

    @RequestMapping(value="/component/parseKerberos", method = {RequestMethod.POST})
    @ApiOperation(value = "解析kerberos文件中信息")
    public List<String> parseKerberos(@RequestParam("fileName") List<MultipartFile> files) {
        return componentService.parseKerberos(getResourcesFromFiles(files));
    }

    @RequestMapping(value="/component/uploadKerberos", method = {RequestMethod.POST})
    public String uploadKerberos(@RequestParam("kerberosFile") List<MultipartFile> files, @RequestParam("clusterId") Long clusterId,
                                 @RequestParam("componentCode") Integer componentCode,@RequestParam("componentVersion") String componentVersion) {
        List<Resource> resources = getResourcesFromFiles(files);
        return componentService.uploadKerberos(resources, clusterId, componentCode,componentVersion);
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
