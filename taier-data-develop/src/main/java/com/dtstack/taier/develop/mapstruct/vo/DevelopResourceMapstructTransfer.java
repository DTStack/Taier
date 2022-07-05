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

import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.develop.dto.devlop.DevelopResourceAddDTO;
import com.dtstack.taier.develop.dto.devlop.DevelopResourceVO;
import com.dtstack.taier.develop.vo.develop.query.DevelopResourceAddVO;
import com.dtstack.taier.develop.vo.develop.result.BatchGetResourceByIdResultVO;
import com.dtstack.taier.develop.vo.develop.result.BatchGetResourcesResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DevelopResourceMapstructTransfer {

    DevelopResourceMapstructTransfer INSTANCE = Mappers.getMapper(DevelopResourceMapstructTransfer.class);


    /**
     * DevelopResourceAddVO --> DevelopResourceAddDTO
     *
     * @param DevelopResourceAddVO
     * @return DevelopResourceAddDTO
     */
    DevelopResourceAddDTO resourceVOToResourceAddDTO(DevelopResourceAddVO DevelopResourceAddVO);


    /**
     * DevelopResourceVO --> BatchGetResourceByIdResultVO
     *
     * @param DevelopResourceVO
     * @return
     */
    BatchGetResourceByIdResultVO DevelopResourceVOToBatchGetResourceByIdResultVO(DevelopResourceVO DevelopResourceVO);

    /**
     * DevelopResource --> BatchGetResourcesResultVO
     *
     * @param DevelopResource
     * @return
     */
    BatchGetResourcesResultVO DevelopResourceToBatchGetResourcesResultVO(DevelopResource DevelopResource);

}
