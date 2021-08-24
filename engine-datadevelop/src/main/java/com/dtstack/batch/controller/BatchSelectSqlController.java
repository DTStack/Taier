package com.dtstack.batch.controller;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.mapstruct.vo.BatchSqlMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.web.table.vo.query.BatchSelectSqlVO;
import com.dtstack.batch.web.table.vo.result.BatchExecuteDataResultVO;
import com.dtstack.batch.web.table.vo.result.BatchExecuteRunLogResultVO;
import com.dtstack.batch.web.table.vo.result.BatchExecuteStatusResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "执行选中的sql或者脚本", tags = {"执行选中的sql或者脚本"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchSelectSql")
public class BatchSelectSqlController {

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @PostMapping(value = "selectData")
    @ApiOperation("获取执行结果")
    @Security(code = AuthCode.DATAMANAGER_HANDLERECORD)
    public R<BatchExecuteDataResultVO> selectData(@RequestBody BatchSelectSqlVO sqlVO) {
        return new APITemplate<BatchExecuteDataResultVO>() {
            @Override
            protected BatchExecuteDataResultVO process() {
                try {
                    return BatchSqlMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteDataResultVO(batchSelectSqlService.selectData(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getProjectId(), sqlVO.getDtuicTenantId(), sqlVO.getUserId(),
                            sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage(), e);
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectStatus")
    @ApiOperation("获取执行状态")
    @Security(code = AuthCode.DATAMANAGER_HANDLERECORD)
    public R<BatchExecuteStatusResultVO> selectStatus(@RequestBody BatchSelectSqlVO sqlVO) {
        return new APITemplate<BatchExecuteStatusResultVO>() {
            @Override
            protected BatchExecuteStatusResultVO process() {
                try {
                    return BatchSqlMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteStatusResultVO(batchSelectSqlService.selectStatus(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getProjectId(), sqlVO.getDtuicTenantId(), sqlVO.getUserId(),
                            sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage(), e);
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectRunLog")
    @ApiOperation("获取执行日志")
    @Security(code = AuthCode.DATAMANAGER_HANDLERECORD)
    public R<BatchExecuteRunLogResultVO> selectRunLog(@RequestBody BatchSelectSqlVO sqlVO) {
        return new APITemplate<BatchExecuteRunLogResultVO>() {
            @Override
            protected BatchExecuteRunLogResultVO process() {
                try {
                    return BatchSqlMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteRunLogResultVO(batchSelectSqlService.selectRunLog(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getProjectId(), sqlVO.getDtuicTenantId(), sqlVO.getUserId(),
                            sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage(), e);
                }
            }
        }.execute();
    }
}
