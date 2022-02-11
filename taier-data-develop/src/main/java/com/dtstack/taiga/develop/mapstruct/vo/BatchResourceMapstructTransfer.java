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

import com.dtstack.taiga.dao.domain.BatchResource;
import com.dtstack.taiga.develop.dto.devlop.BatchResourceAddDTO;
import com.dtstack.taiga.develop.dto.devlop.BatchResourceVO;
import com.dtstack.taiga.develop.web.develop.query.BatchResourceAddVO;
import com.dtstack.taiga.develop.web.develop.result.BatchGetResourceByIdResultVO;
import com.dtstack.taiga.develop.web.develop.result.BatchGetResourcesResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchResourceMapstructTransfer {

    BatchResourceMapstructTransfer INSTANCE = Mappers.getMapper(BatchResourceMapstructTransfer.class);


    /**
     * BatchResourceAddVO --> BatchResourceAddDTO
     *
     * @param batchResourceAddVO
     * @return batchResourceAddDTO
     */
    BatchResourceAddDTO resourceVOToResourceAddDTO(BatchResourceAddVO batchResourceAddVO);


    /**
     * BatchResourceVO --> BatchGetResourceByIdResultVO
     *
     * @param batchResourceVO
     * @return
     */
    BatchGetResourceByIdResultVO batchResourceVOToBatchGetResourceByIdResultVO(BatchResourceVO batchResourceVO);

    /**
     * BatchResource --> BatchGetResourcesResultVO
     *
     * @param batchResource
     * @return
     */
    BatchGetResourcesResultVO batchResourceToBatchGetResourcesResultVO(BatchResource batchResource);

}
