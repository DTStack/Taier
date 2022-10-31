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

package com.dtstack.taier.develop.mapstruct.datasource;

import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.po.DsListBO;
import com.dtstack.taier.dao.domain.po.DsListQuery;
import com.dtstack.taier.develop.bo.datasource.DsListParam;
import com.dtstack.taier.develop.vo.datasource.DsInfoVO;
import com.dtstack.taier.develop.vo.datasource.DsListVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DsListTransfer {

    DsListTransfer INSTANCE = Mappers.getMapper(DsListTransfer.class);


    DsListQuery toInfoQuery(DsListParam dsListParam);


    DsListVO toInfoVO(DsListBO dsListBO);

    DsInfoVO toDsInfoVO(DsListBO dsListBO);

    @Mapping(source = "id",target = "dataInfoId")
    DsInfoVO toDsInfoVO(DsInfo dsInfo);

    List<DsInfoVO> toDsInfoVOS(List<DsInfo> dsInfoList);



}
