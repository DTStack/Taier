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

package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.dto.BatchTaskDTO;
import com.dtstack.taier.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
@Mapper
public interface DevelopTaskMapper extends BaseMapper<Task> {

    List<Task> catalogueListBatchTaskByNodePid(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid);

}
