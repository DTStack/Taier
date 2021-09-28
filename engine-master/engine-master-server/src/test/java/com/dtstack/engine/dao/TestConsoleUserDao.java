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

import com.dtstack.engine.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2020-07-08
 */
public interface TestConsoleUserDao {

    @Insert({" INSERT INTO console_user (dtuic_user_id,user_name,email,status,phone_number)" +
            "        values(#{user.dtuicUserId},#{user.userName},#{user.email},#{user.status},#{user.phoneNumber})"})
    @Options(useGeneratedKeys=true, keyProperty = "user.id", keyColumn = "id")
    void insert(@Param("user") User user);
}
