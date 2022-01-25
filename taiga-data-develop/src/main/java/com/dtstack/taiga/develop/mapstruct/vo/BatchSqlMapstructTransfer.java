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

package com.dtstack.taiga.develop.mapstruct.vo;

import com.dtstack.taiga.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taiga.develop.web.develop.result.BatchExecuteDataResultVO;
import com.dtstack.taiga.develop.web.develop.result.BatchExecuteRunLogResultVO;
import com.dtstack.taiga.develop.web.develop.result.BatchExecuteStatusResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchSqlMapstructTransfer {

    BatchSqlMapstructTransfer INSTANCE = Mappers.getMapper(BatchSqlMapstructTransfer.class);

    /**
     * ExecuteResultVO -> BatchExecuteDataResultVO
     * @param executeResultVO
     * @return
     */
    BatchExecuteDataResultVO executeResultVOToBatchExecuteDataResultVO(ExecuteResultVO executeResultVO);

    /**
     * ExecuteResultVO -> BatchExecuteRunLogResultVO
     * @param executeResultVO
     * @return
     */
    BatchExecuteRunLogResultVO executeResultVOToBatchExecuteRunLogResultVO(ExecuteResultVO executeResultVO);

    /**
     * ExecuteResultVO -> BatchExecuteStatusResultVO
     * @param executeResultVO
     * @return
     */
    BatchExecuteStatusResultVO executeResultVOToBatchExecuteStatusResultVO(ExecuteResultVO executeResultVO);

}
