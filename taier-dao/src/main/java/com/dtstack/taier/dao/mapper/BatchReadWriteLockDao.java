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

import com.dtstack.taier.dao.domain.BatchReadWriteLock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchReadWriteLockDao {

    BatchReadWriteLock getOne(@Param("id") long id);

    Integer insert(BatchReadWriteLock BatchReadWriteLock);

    Integer updateVersionAndModifyUserId(@Param("id") Long id, @Param("version") Integer version, @Param("modifyUserId")Long modifyUserId);

    BatchReadWriteLock getByTenantIdAndRelationIdAndType(@Param("tenantId") Long tenantId, @Param("relationId") Long relationId, @Param("type") String type);

    List<BatchReadWriteLock> getLocksByIds(@Param("tenantId") Long tenantId, @Param("type") String type, @Param("relationIds") List<Long> relationIds);

    Integer updateVersionAndModifyUserIdDefinitized(@Param("id") Long id, @Param("userId") Long userId);

}
