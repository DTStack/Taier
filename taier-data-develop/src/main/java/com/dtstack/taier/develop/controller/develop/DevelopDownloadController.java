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

package com.dtstack.taier.develop.controller.develop;

import com.dtstack.taier.common.enums.DownloadType;
import com.dtstack.taier.develop.service.develop.impl.DevelopDownloadService;
import com.dtstack.taier.develop.vo.develop.query.DevelopDownloadJobLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(value = "下载管理", tags = {"下载管理"})
@RestController
@RequestMapping(value = "/developDownload")
public class DevelopDownloadController {

    @Autowired
    private DevelopDownloadService developDownloadService;

    @GetMapping(value = "downloadJobLog")
    @ApiOperation("下载job日志")
    public void downloadJobLog(DevelopDownloadJobLogVO vo, HttpServletResponse response) {
        developDownloadService.handleDownload(response, DownloadType.DEVELOP_LOG, vo.getJobId(),
                vo.getTenantId(), vo.getTaskType());
    }

}
