package com.dtstack.batch.web.controller;


import com.dtstack.batch.mapstruct.vo.BatchReadWriteLockMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.task.impl.ReadWriteLockService;
import com.dtstack.batch.web.task.vo.query.BatchReadWriteLockGetLockVO;
import com.dtstack.batch.web.task.vo.query.BatchReadWriteLockGetReadWriteLockVO;
import com.dtstack.batch.web.task.vo.result.ReadWriteLockGetLockResultVO;
import com.dtstack.batch.web.task.vo.result.ReadWriteLockResultVO;
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

@Api(value = "读写锁", tags = {"读写锁"})
@RestController
@RequestMapping(value = "/api/rdos/common/readWriteLock")
public class ReadWriteLockController {

    @Autowired
    private ReadWriteLockService lockService;

    @PostMapping(value = "getLock")
    @ApiOperation("获取锁")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_EDIT)
    public R<ReadWriteLockGetLockResultVO> getLock(@RequestBody BatchReadWriteLockGetLockVO lockVO) {
        return new APITemplate<ReadWriteLockGetLockResultVO>() {
            @Override
            protected ReadWriteLockGetLockResultVO process() {
                return BatchReadWriteLockMapstructTransfer.INSTANCE.ReadWriteLockVOToReadWriteLockGetLockResultVO(lockService.getLock(lockVO.getTenantId(), lockVO.getUserId(),
                        lockVO.getType(), lockVO.getFileId(), lockVO.getProjectId(), lockVO.getLockVersion(), lockVO.getSubFileIds()));
            }
        }.execute();
    }

    @PostMapping(value = "getReadWriteLock")
    @ApiOperation("获取读写锁")
    public R<ReadWriteLockResultVO> getReadWriteLock(@RequestBody BatchReadWriteLockGetReadWriteLockVO lockVO) {
        return new APITemplate<ReadWriteLockResultVO>() {
            @Override
            protected ReadWriteLockResultVO process() {
                return BatchReadWriteLockMapstructTransfer.INSTANCE.ReadWriteLockToResultVO(lockService.getReadWriteLock(lockVO.getUserId(),
                        lockVO.getRelationId(), lockVO.getType()));
            }
        }.execute();
    }

}
