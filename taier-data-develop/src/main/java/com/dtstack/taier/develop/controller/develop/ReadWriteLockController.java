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


import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.mapstruct.vo.BatchReadWriteLockMapstructTransfer;
import com.dtstack.taier.develop.service.develop.impl.ReadWriteLockService;
import com.dtstack.taier.develop.web.develop.query.BatchReadWriteLockGetLockVO;
import com.dtstack.taier.develop.web.develop.query.BatchReadWriteLockGetReadWriteLockVO;
import com.dtstack.taier.develop.web.develop.result.ReadWriteLockGetLockResultVO;
import com.dtstack.taier.develop.web.develop.result.ReadWriteLockResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "读写锁", tags = {"读写锁"})
@RestController
@RequestMapping(value = "/readWriteLock")
public class ReadWriteLockController {

    @Autowired
    private ReadWriteLockService lockService;

    @PostMapping(value = "getLock")
    @ApiOperation("获取锁")
    public R<ReadWriteLockGetLockResultVO> getLock(@RequestBody BatchReadWriteLockGetLockVO lockVO) {
        return new APITemplate<ReadWriteLockGetLockResultVO>() {
            @Override
            protected ReadWriteLockGetLockResultVO process() {
                return BatchReadWriteLockMapstructTransfer.INSTANCE.ReadWriteLockVOToReadWriteLockGetLockResultVO(lockService.getLock(lockVO.getTenantId(), lockVO.getUserId(),
                        lockVO.getType(), lockVO.getFileId(), lockVO.getSubFileIds()));
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
