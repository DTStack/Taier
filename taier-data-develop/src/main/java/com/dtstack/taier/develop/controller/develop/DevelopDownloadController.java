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
