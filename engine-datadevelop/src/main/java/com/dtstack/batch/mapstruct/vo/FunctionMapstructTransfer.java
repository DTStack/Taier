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

package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.dto.BatchFunctionDTO;
import com.dtstack.batch.vo.BatchFunctionVO;
import com.dtstack.batch.vo.TaskCatalogueVO;
import com.dtstack.batch.web.function.vo.query.BatchFunctionAddVO;
import com.dtstack.batch.web.function.vo.query.BatchFunctionQueryVO;
import com.dtstack.batch.web.function.vo.result.BatchFunctionAddResultVO;
import com.dtstack.batch.web.function.vo.result.BatchFunctionQueryResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FunctionMapstructTransfer {
    FunctionMapstructTransfer INSTANCE = Mappers.getMapper(FunctionMapstructTransfer.class);

    /**
     * BatchFunctionVO  ->  BatchFunction
     *
     * @param vo
     * @return
     */
    BatchFunction newFunctionVoToFunctionVo(BatchFunctionVO vo);

    /**
     * BatchFunctionAddVO  ->  BatchFunction
     *
     * @param vo
     * @return
     */
    BatchFunction newFunctionAddVoToFunctionVo(BatchFunctionAddVO vo);

    /**
     * BatchFunctionQueryVO  ->  BatchFunctionDTO
     *
     * @param vo
     * @return
     */
    BatchFunctionDTO newFunctionQueryVoToDTO(BatchFunctionQueryVO vo);

    /**
     * com.dtstack.batch.vo.BatchFunctionVO  ->  BatchFunctionQueryResultVO
     *
     * @param vo
     * @return
     */
    BatchFunctionQueryResultVO newFunctionToFunctionResultVo(BatchFunctionVO vo);

    /**
     * TaskCatalogueVO  ->  BatchFunctionAddResultVO
     *
     * @param vo
     * @return
     */
    BatchFunctionAddResultVO newTaskCatalogueVoToFunctionAddResultVo(TaskCatalogueVO vo);

    /**
     * PageResult<List<com.dtstack.batch.vo.BatchFunctionVO>>  ->  PageResult<List<BatchFunctionQueryResultVO>>
     *
     * @param result
     * @return
     */
    PageResult<List<BatchFunctionQueryResultVO>> newFunctionVoToFunctionQueryResultVo(PageResult<List<BatchFunctionVO>> result);
}
