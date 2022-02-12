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

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.mapstruct.vo.BatchSqlMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.BatchSelectSqlService;
import com.dtstack.taier.develop.web.develop.query.BatchSelectSqlVO;
import com.dtstack.taier.develop.web.develop.result.BatchExecuteDataResultVO;
import com.dtstack.taier.develop.web.develop.result.BatchExecuteRunLogResultVO;
import com.dtstack.taier.develop.web.develop.result.BatchExecuteStatusResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "执行选中的sql或者脚本", tags = {"执行选中的sql或者脚本"})
@RestController
@RequestMapping(value = "/batchSelectSql")
public class BatchSelectSqlController {

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @PostMapping(value = "selectData")
    @ApiOperation("获取执行结果")
    public R<BatchExecuteDataResultVO> selectData(@RequestBody BatchSelectSqlVO sqlVO) {
        return new APITemplate<BatchExecuteDataResultVO>() {
            @Override
            protected BatchExecuteDataResultVO process() {
                try {
                    return BatchSqlMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteDataResultVO(batchSelectSqlService.selectData(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectStatus")
    @ApiOperation("获取执行状态")
    public R<BatchExecuteStatusResultVO> selectStatus(@RequestBody BatchSelectSqlVO sqlVO) {
        return new APITemplate<BatchExecuteStatusResultVO>() {
            @Override
            protected BatchExecuteStatusResultVO process() {
                try {
                    return BatchSqlMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteStatusResultVO(batchSelectSqlService.selectStatus(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectRunLog")
    @ApiOperation("获取执行日志")
    public R<BatchExecuteRunLogResultVO> selectRunLog(@RequestBody BatchSelectSqlVO sqlVO) {
        return new APITemplate<BatchExecuteRunLogResultVO>() {
            @Override
            protected BatchExecuteRunLogResultVO process() {
                try {
                    return BatchSqlMapstructTransfer.INSTANCE.executeResultVOToBatchExecuteRunLogResultVO(batchSelectSqlService.selectRunLog(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }
}
