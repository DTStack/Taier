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

package com.dtstack.taiga.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taiga.dao.domain.ScheduleDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-03-02
 */
public interface DictMapper extends BaseMapper<ScheduleDict> {

    List<ScheduleDict> listDictByType(@Param("type") Integer type);

    ScheduleDict getTypeDefault(@Param("type") Integer type);

    ScheduleDict getByNameValue(@Param("type") Integer type, @Param("dictName") String dictName, @Param("dictValue") String dictValue,@Param("dependName") String dependName);

    List<ScheduleDict> getByDependName(@Param("type") Integer type,@Param("dependName") String dependName);

}
