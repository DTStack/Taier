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
import com.dtstack.taier.dao.domain.TaskVersion;
import com.dtstack.taier.dao.dto.DevelopTaskVersionDetailDTO;
import com.dtstack.taier.dao.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface DevelopTaskVersionMapper extends BaseMapper<TaskVersion> {

    List<DevelopTaskVersionDetailDTO> listByTaskId(@Param("taskId") Long taskId, @Param("pageQuery") PageQuery pageQuery);

    DevelopTaskVersionDetailDTO getByVersionId(@Param("versionId") Long versionId);

    List<DevelopTaskVersionDetailDTO> getByVersionIds(@Param("versionIds") List<Integer> versionId);

    List<DevelopTaskVersionDetailDTO> getByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<DevelopTaskVersionDetailDTO> getWithoutSqlByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<DevelopTaskVersionDetailDTO> getLatestTaskVersionByTaskIds(@Param("taskIds") List<Long> taskIds);

    Integer getMaxVersionId(@Param("taskId") Long taskId);

    DevelopTaskVersionDetailDTO getBytaskIdAndVersionId(@Param("taskId") Long taskId, @Param("versionId") Long versionId);

}
