package com.dtstack.engine.master.controller;

import com.dtstack.engine.alert.enums.AlertGateTypeEnum;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.ComponentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;


@RestController
@RequestMapping("/node/download")
@Api(value = "/node/download", tags = {"下载接口"})
public class DownloadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    private ComponentService componentService;

    @Value("${user.dir}")
    private String path;

    @RequestMapping(value="/component/downloadFile", method = {RequestMethod.GET})
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type",value="0:kerberos配置文件 1:配置文件 2:模板文件",required=true, dataType = "int")
    })
    public void handleDownload(@RequestParam(value = "componentId",required = false) Long componentId,
                               @RequestParam("type") Integer downloadType,
                               @RequestParam("componentType") Integer componentType,
                               @RequestParam("hadoopVersion") String componentVersion,
                               @RequestParam("clusterName") String clusterName, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/octet-stream;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        File downLoadFile = null;
        try {
            downLoadFile = componentService.downloadFile(componentId, downloadType, componentType, componentVersion, clusterName);
            if (null != downLoadFile && downLoadFile.isFile()) {
                response.setHeader("Content-Disposition", "attachment;filename=" + encodeURIComponent(downLoadFile.getName()));
                ServletOutputStream outputStream = response.getOutputStream();
                byte[] bytes = FileUtils.readFileToByteArray(downLoadFile);
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            response.setHeader("Content-Disposition", "attachment;filename=error.log");
            LOGGER.error("", e);
            try {
                response.getWriter().write("下载文件异常:" + e.getMessage());
            } catch (Exception eMsg) {
                LOGGER.error("", eMsg);
            }
        } finally {
            if(null != downLoadFile){
                downLoadFile.delete();
            }
        }
    }

    private static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
        }
        return value;
    }


    @RequestMapping(value="/downloadJar", method = {RequestMethod.GET})
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name="alertGateType",value="MAIL(1), SMS(2), DINGDING(3),CUSTOMIZE(4);",required=true, dataType = "int")
    })
    public void handleDownload(@RequestParam(value = "alertGateType",required = false) Long alertGateType, HttpServletResponse response){
        response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");

        File downLoadFile = null;
        try {
            if (alertGateType == null) {
                throw new RdosDefineException("alertGateType不能为null");
            }
            AlertGateTypeEnum enumByCode = AlertGateTypeEnum.getEnumByCode(alertGateType.intValue());

            if (enumByCode == null) {
                throw new RdosDefineException("非法通道类型");
            }

            downLoadFile = new File(path + "/doc/" + enumByCode.name() + ".zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + encodeURIComponent(downLoadFile.getName()));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = FileUtils.readFileToByteArray(downLoadFile);
            outputStream.write(bytes);
            outputStream.flush();

        } catch (Exception e) {
            response.setHeader("Content-Disposition", "attachment;filename=error.log");
            LOGGER.error("", e);
            try {
                response.getWriter().write("下载文件异常:" + e.getMessage());
            } catch (Exception eMsg) {
                LOGGER.error("", eMsg);
            }
        }
    }



}
