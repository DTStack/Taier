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

package com.dtstack.engine.datasource.dao.mapper.datasource;

import com.dtstack.engine.datasource.dao.bo.datasource.DsAuthRefBO;
import com.dtstack.engine.datasource.dao.mapper.IMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsAuthRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Mapper
public interface DsAuthRefMapper extends IMapper<DsAuthRef> {

    List<DsAuthRefBO> mapDaIdName(@Param("dataInfoIds") List<Long> dataInfoIds);

    List<Long> getDataIdByAppTypes(@Param("appTypes") List<Integer> appTypes);
}
