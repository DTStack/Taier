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

package com.dtstack.batch.controller;


import com.dtstack.batch.service.task.impl.BatchTaskRecordService;
import com.dtstack.batch.web.task.vo.query.BatchTaskRecordQueryRecordsVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskRecordQueryRecordsResultVO;
import com.dtstack.engine.common.lang.coc.APITemplate;
import com.dtstack.engine.common.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "任务操作记录", tags = {"任务操作记录"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchTaskRecord")
public class BatchTaskRecordController {

    @Autowired
    private BatchTaskRecordService recordService;

    @PostMapping(value = "queryRecords")
    @ApiOperation("查询操作记录")
    public R<BatchTaskRecordQueryRecordsResultVO> queryRecords(@RequestBody BatchTaskRecordQueryRecordsVO vo) {
        return new APITemplate<BatchTaskRecordQueryRecordsResultVO>() {
            @Override
            protected BatchTaskRecordQueryRecordsResultVO process() {
                return recordService.queryRecords(vo .getTaskId(), vo.getCurrentPage(),
                        vo.getPageSize());
            }
        }.execute();
    }

}
