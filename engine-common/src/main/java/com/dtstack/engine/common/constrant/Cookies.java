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

package com.dtstack.engine.common.constrant;

/**
 * @author yuebai
 * @date 2021-08-03
 */
public interface Cookies {
    String DT_TOKEN = "dt_token";
    String TOKEN = "token";

    String DT_USER_ID = "dt_user_id";
    String DT_USER_NAME = "dt_username";
    String DT_TENANT_ID = "dt_tenant_id";
    String DT_TENANT_NAME = "dt_tenant_name";

    String USER_ID = "userId";
    String CREATE_USER_ID = "createUserId";
    String MODIFY_USER_ID = "modifyUserId";
}
