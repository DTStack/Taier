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

import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.service.console.ConsoleComponentService;
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
@RequestMapping("/download")
@Api(value = "/download", tags = {"下载接口"})
public class DownloadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    private ConsoleComponentService consoleComponentService;

    @Value("${user.dir}")
    private String path;

    @RequestMapping(value = "/component/downloadFile", method = {RequestMethod.GET})
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "0:kerberos配置文件 1:配置文件 2:模板文件", required = true, dataType = "int")
    })
    public R<Void> handleDownload(@RequestParam(value = "componentId", required = false) Long componentId,
                                  @RequestParam("type") Integer downloadType,
                                  @RequestParam("componentType") Integer componentType,
                                  @RequestParam("versionName") String versionName,
                                  @RequestParam("clusterId") Long clusterId,
                                  @RequestParam(value = "deployType", required = false) Integer deployType,
                                  HttpServletResponse response) {
        response.setHeader("Content-Type", "application/octet-stream;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        File downLoadFile = null;
        try {
            downLoadFile = consoleComponentService.downloadFile(componentId, downloadType, componentType, versionName, clusterId, deployType);
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
            if (null != downLoadFile) {
                downLoadFile.delete();
            }
        }
        return R.empty();
    }

    private static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
        }
        return value;
    }
}
