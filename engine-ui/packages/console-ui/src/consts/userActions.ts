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

import mc from 'mirror-creator';

const userActions = mc([
    'GET_USER', // 获取当前用户信息
    'UPDATE_USER', // 更新当前用户信息
    'GET_NOT_PROJECT_USERS', // 非项目用户
    'GET_PROJECT_USERS', // 项目用户列表
    'GET_USER_LIST'
], { prefix: 'user/' })

export default userActions;
