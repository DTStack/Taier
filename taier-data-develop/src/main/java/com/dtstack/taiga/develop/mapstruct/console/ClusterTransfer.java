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

package com.dtstack.taiga.develop.mapstruct.console;


import com.dtstack.taiga.dao.domain.Cluster;
import com.dtstack.taiga.develop.dto.devlop.ComponentBindDBDTO;
import com.dtstack.taiga.develop.vo.console.ClusterInfoVO;
import com.dtstack.taiga.develop.vo.console.ComponentBindDBVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClusterTransfer {

    ClusterTransfer INSTANCE = Mappers.getMapper(ClusterTransfer.class);

    @Mapping(source = "id", target = "clusterId")
    ClusterInfoVO toInfoVO(Cluster cluster);


    List<ClusterInfoVO> toInfoVOs(List<Cluster> cluster);

    List<ComponentBindDBDTO> bindDBtoDTOList(List<ComponentBindDBVO> componentBindDBVOList);

}
