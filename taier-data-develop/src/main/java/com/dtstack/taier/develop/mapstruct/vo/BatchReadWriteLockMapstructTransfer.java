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

package com.dtstack.taier.develop.mapstruct.vo;

import com.dtstack.taier.dao.domain.BatchReadWriteLock;
import com.dtstack.taier.develop.dto.devlop.ReadWriteLockVO;
import com.dtstack.taier.develop.vo.develop.result.ReadWriteLockGetLockResultVO;
import com.dtstack.taier.develop.vo.develop.result.ReadWriteLockResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchReadWriteLockMapstructTransfer {

    BatchReadWriteLockMapstructTransfer INSTANCE = Mappers.getMapper(BatchReadWriteLockMapstructTransfer.class);

    /**
     * ReadWriteLock -> ReadWriteLockResultVO
     * @param readWriteLock
     * @return
     */
    ReadWriteLockResultVO ReadWriteLockToResultVO(BatchReadWriteLock readWriteLock);

    /**
     * ReadWriteLockVO -> ReadWriteLockGetLockResultVO
     * @param readWriteLockVO
     * @return
     */
    @Mapping(source = "getLock", target = "getLock")
    ReadWriteLockGetLockResultVO ReadWriteLockVOToReadWriteLockGetLockResultVO(ReadWriteLockVO readWriteLockVO);

}
