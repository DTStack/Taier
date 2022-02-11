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

import com.dtstack.taiga.dao.domain.BatchSelectSql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO 修改为非特定hive
 * @author jiangbo
 */
public interface BatchSelectSqlDao {

    /**
     * 根据 jobId 查询SQL记录
     * @param jobId
     * @param tenantId
     * @param isDeleted
     * @return
     */
    BatchSelectSql getByJobId(@Param("jobId") String jobId, @Param("tenantId") Long tenantId, @Param("isDeleted") Integer isDeleted);

    /**
     * 插入 sql执行记录
     * @param selectSql
     * @return
     */
    Integer insert(BatchSelectSql selectSql);

    Integer updateGmtModify(@Param("jobId") String jobId, @Param("tenantId") Long tenantId);

    Integer deleteByJobId(@Param("jobId") String jobId, @Param("tenantId") Long tenantId);

    List<BatchSelectSql> listSelectTypeByGmtModified(@Param("timeDiff")Integer timeDiff);

    Integer deleteByIds(@Param("list") List<Long> list);

    Integer deleteByJobIds(@Param("list") List<String> list);

    List<BatchSelectSql> listBySqlType(@Param("type") Integer type);

    Integer deleteByTenantId(@Param("tenantId") Long tenantId);
}
