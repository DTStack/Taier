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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Account;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2020-02-14
 */
public interface AccountDao {

    Integer insert(Account account);

    Account getByName(@Param("name") String name, @Param("type") Integer type);

    Account getById(@Param("id") Long id);

    Integer update(Account account);

    Account getOne(@Param("tenantId") Long tenantId,@Param("userId") Long userId,
                   @Param("accountType") Integer accountType,@Param("name") String name);


}