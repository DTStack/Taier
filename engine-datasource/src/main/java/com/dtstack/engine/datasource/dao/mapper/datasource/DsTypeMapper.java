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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.datasource.dao.mapper.IMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Mapper
public interface DsTypeMapper extends IMapper<DsType> {
    /**
     * 添加数据源类型的权重值
     * 目前固定增加1
     * @param dataType
     * @param plusWeight
     * @return
     */
    Integer plusDataTypeWeight(@Param("dataType") String dataType, @Param("plusWeight") Integer plusWeight);
}
