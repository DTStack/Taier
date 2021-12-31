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

import com.dtstack.batch.domain.BatchTaskVersion;
import com.dtstack.batch.domain.BatchTaskVersionDetail;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author: toutian
 */
public interface BatchTaskVersionDao {

    List<BatchTaskVersionDetail> listByTaskId(@Param("taskId") Long taskId, @Param("pageQuery") PageQuery pageQuery);

    Integer insert(BatchTaskVersion batchTaskVersion);

    BatchTaskVersionDetail getByVersionId(@Param("versionId") Long versionId);

    List<BatchTaskVersionDetail> getByVersionIds(@Param("versionIds") List<Integer> versionId);

    List<BatchTaskVersionDetail> getByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<BatchTaskVersionDetail> getWithoutSqlByTaskIds(@Param("taskIds") List<Long> taskIds);

    List<BatchTaskVersionDetail> getLatestTaskVersionByTaskIds(@Param("taskIds") List<Long> taskIds);

    Integer getMaxVersionId(@Param("taskId") Long taskId);

    BatchTaskVersionDetail getBytaskIdAndVersionId(@Param("taskId") Long taskId, @Param("versionId") Long versionId);

    BatchTaskVersion getByTaskIdAndVersion(@Param("taskId") Long taskId, @Param("version") Integer version);

}
