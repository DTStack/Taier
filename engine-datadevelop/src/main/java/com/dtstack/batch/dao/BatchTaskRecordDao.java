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

package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskRecord;
import com.dtstack.batch.dto.BatchTaskRecordDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchTaskRecordDao {

    Integer insert(BatchTaskRecord record);

    Integer insertAll(@Param("records") List<BatchTaskRecord> records);

    BatchTaskRecord getOne(@Param("id") Long id);

    Integer deleteById(@Param("taskId") Long id, @Param("projectId") Long projectId, @Param("operatorId") Long userId);

    List<BatchTaskRecord> generalQuery(PageQuery<BatchTaskRecordDTO> pageQuery);

    Integer generalCount(PageQuery<BatchTaskRecordDTO> pageQuery);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
