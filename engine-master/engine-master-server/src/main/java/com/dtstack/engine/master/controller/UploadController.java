package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.router.vertx.ResourceVerticle;
import io.swagger.annotations.Api;
import io.vertx.ext.web.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/node/upload")
@Api(value = "/node/upload", tags = {"上传接口"})
public class UploadController {
    @Autowired
    private ComponentService componentService;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private static String uploadsDir = System.getProperty("user.dir") + File.separator + "file-uploads";

    @RequestMapping(value="/component/config", method = {RequestMethod.POST})
    @ResponseBody
    public List<Object> upload(@RequestParam("fileName") List<MultipartFile> files, @RequestParam("componentType") Integer componentType, @RequestParam(value = "autoDelete", required = false) Boolean autoDelete) {
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
                logger.error("" + e);
                throw new RdosDefineException("存储文件发生错误");
            }
            resources.add(new Resource(fileOriginalName, path, (int) file.getSize(), file.getContentType(), file.getName()));
        }
        return componentService.config(resources, componentType, autoDelete);
    }
}
