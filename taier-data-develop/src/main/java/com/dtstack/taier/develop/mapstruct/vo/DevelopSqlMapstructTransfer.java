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

import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteDataResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteRunLogResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopExecuteStatusResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DevelopSqlMapstructTransfer {

    DevelopSqlMapstructTransfer INSTANCE = Mappers.getMapper(DevelopSqlMapstructTransfer.class);

    /**
     * ExecuteResultVO -> DevelopExecuteDataResultVO
     * @param executeResultVO
     * @return
     */
    DevelopExecuteDataResultVO executeResultVOToDevelopExecuteDataResultVO(ExecuteResultVO executeResultVO);

    /**
     * ExecuteResultVO -> DevelopExecuteRunLogResultVO
     * @param executeResultVO
     * @return
     */
    DevelopExecuteRunLogResultVO executeResultVOToDevelopExecuteRunLogResultVO(ExecuteResultVO executeResultVO);

    /**
     * ExecuteResultVO -> DevelopExecuteStatusResultVO
     * @param executeResultVO
     * @return
     */
    DevelopExecuteStatusResultVO executeResultVOToDevelopExecuteStatusResultVO(ExecuteResultVO executeResultVO);

}
