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
import com.dtstack.taier.develop.mapstruct.vo.DevelopSqlMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.DevelopSelectSqlService;
import com.dtstack.taier.develop.vo.develop.query.DevelopSelectSqlVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteDataResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteRunLogResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteStatusResultVO;
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
public class DevelopSelectSqlController {

    @Autowired
    private DevelopSelectSqlService batchSelectSqlService;

    @PostMapping(value = "selectData")
    @ApiOperation("获取执行结果")
    public R<DevelopExecuteDataResultVO> selectData(@RequestBody DevelopSelectSqlVO sqlVO) {
        return new APITemplate<DevelopExecuteDataResultVO>() {
            @Override
            protected DevelopExecuteDataResultVO process() {
                try {
                    return DevelopSqlMapstructTransfer.INSTANCE.executeResultVOToDevelopExecuteDataResultVO(batchSelectSqlService.selectData(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectStatus")
    @ApiOperation("获取执行状态")
    public R<DevelopExecuteStatusResultVO> selectStatus(@RequestBody DevelopSelectSqlVO sqlVO) {
        return new APITemplate<DevelopExecuteStatusResultVO>() {
            @Override
            protected DevelopExecuteStatusResultVO process() {
                try {
                    return DevelopSqlMapstructTransfer.INSTANCE.executeResultVOToDevelopExecuteStatusResultVO(batchSelectSqlService.selectStatus(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }

    @PostMapping(value = "selectRunLog")
    @ApiOperation("获取执行日志")
    public R<DevelopExecuteRunLogResultVO> selectRunLog(@RequestBody DevelopSelectSqlVO sqlVO) {
        return new APITemplate<DevelopExecuteRunLogResultVO>() {
            @Override
            protected DevelopExecuteRunLogResultVO process() {
                try {
                    return DevelopSqlMapstructTransfer.INSTANCE.executeResultVOToDevelopExecuteRunLogResultVO(batchSelectSqlService.selectRunLog(sqlVO.getJobId(),
                            sqlVO.getTaskId(), sqlVO.getTenantId(), sqlVO.getUserId(), sqlVO.getIsRoot(), sqlVO.getType(), sqlVO.getSqlId()));
                } catch (Exception e) {
                    throw new RdosDefineException(e.getMessage());
                }
            }
        }.execute();
    }
}
