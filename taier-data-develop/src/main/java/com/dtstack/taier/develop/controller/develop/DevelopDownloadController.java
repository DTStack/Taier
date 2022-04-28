package com.dtstack.taier.develop.controller.develop;

import com.dtstack.taier.common.enums.DownloadType;
import com.dtstack.taier.develop.service.develop.impl.BatchDownloadService;
import com.dtstack.taier.develop.service.develop.impl.FlinkDownloadLogService;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.develop.vo.develop.query.BatchDownloadJobLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletResponse;

@Api(value = "下载管理", tags = {"下载管理"})
@RestController
@RequestMapping(value = "/developDownload")
public class DevelopDownloadController {

    @Autowired
    private BatchDownloadService batchDownloadService;

    @Autowired
    private FlinkDownloadLogService flinkDownloadLogService;

    @GetMapping(value = "downloadJobLog")
    @ApiOperation("下载job日志")
    public void downloadJobLog(BatchDownloadJobLogVO vo, HttpServletResponse response) {
        IDownload iDownload = batchDownloadService.downloadJobLog(vo.getJobId(), vo.getTaskType(), vo.getTenantId());
        batchDownloadService.handleDownload(response, iDownload, DownloadType.DEVELOP_LOG, vo.getJobId());
    }

}
