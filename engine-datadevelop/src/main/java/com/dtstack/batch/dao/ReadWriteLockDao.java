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

import com.dtstack.batch.domain.ReadWriteLock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReadWriteLockDao {
    ReadWriteLock getOne(@Param("id") long id);

    ReadWriteLock getByLockName(@Param("lockName") String lockName);

    Integer insert(ReadWriteLock readWriteLock);

    Integer updateVersionAndModifyUserId(@Param("id") Long id, @Param("version") Integer version, @Param("modifyUserId")Long modifyUserId);

    ReadWriteLock getByProjectIdAndRelationIdAndType(@Param("projectId") long projectId, @Param("relationId") long relationId, @Param("type") String type);

    List<ReadWriteLock> getLocksByIds(@Param("projectId") long projectId, @Param("type") String type, @Param("relationIds") List<Long> relationIds);

    Integer updateVersionAndModifyUserIdDefinitized(@Param("id") Long id, @Param("userId") Long userId);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
