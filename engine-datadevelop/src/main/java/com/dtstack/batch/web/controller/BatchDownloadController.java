package com.dtstack.batch.web.controller;

import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.enums.DownloadType;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchDownloadService;
import com.dtstack.batch.web.annotation.FileDownload;
import com.dtstack.batch.web.download.vo.BatchDownloadJobLogVO;
import com.dtstack.batch.web.download.vo.BatchDownloadSqlVO;
import com.dtstack.batch.web.filemanager.vo.query.BatchFileMergePartitionGetVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(value = "下载管理", tags = {"下载管理"})
@RestController
@RequestMapping(value = "/api/rdos/download/batch/batchDownload")
public class BatchDownloadController {

    @Autowired
    private BatchDownloadService batchDownloadService;

    @GetMapping(value = "downloadSqlExeResult")
    @ApiOperation("下载sql解析结果")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    @FileDownload
    public void downloadSqlExeResult(BatchDownloadSqlVO vo, HttpServletResponse response) {
        IDownload iDownload = batchDownloadService.downloadSqlExeResult(vo.getJobId(), vo.getTenantId(), vo.getProjectId(), vo.getDtuicTenantId(), vo.getUserId(), vo.getIsRoot());
        batchDownloadService.handleDownload(response, iDownload, DownloadType.TABLE, vo.getJobId(), vo.getDtuicTenantId(), vo.getTenantId(), vo.getUserId(), vo.getIsRoot());
    }

    @GetMapping(value = "downloadJobLog")
    @ApiOperation("下载job日志")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    @FileDownload
    public void downloadJobLog(BatchDownloadJobLogVO vo, HttpServletResponse response) {
        IDownload iDownload = batchDownloadService.downloadJobLog(vo.getJobId(), vo.getTaskType(), vo.getDtuicTenantId());
        batchDownloadService.handleDownload(response, iDownload, DownloadType.LOG, vo.getJobId(), null, null, null, null);
    }

    @GetMapping(value = "downloadFileMergeRecordMessage")
    @ApiOperation("下载文件治理记录日志")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    @FileDownload
    public void downloadFileMergeRecordMessage(BatchFileMergePartitionGetVO recordGetVO, HttpServletResponse response){
        batchDownloadService.downloadFileMergeRecordMessage(recordGetVO.getPartitionId(), response);
    }
}
