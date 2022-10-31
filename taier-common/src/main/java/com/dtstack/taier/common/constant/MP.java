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

package com.dtstack.taier.common.constant;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/15
 */
public interface MP {
    String COLUMN_ID = "id";
    String COLUMN_DELETED = "is_deleted";
    String COLUMN_CREATE_AT = "gmt_create";
    String COLUMN_UPDATE_AT = "gmt_modified";
    String COLUMN_CREATE_BY = "create_user_id";
    String COLUMN_UPDATE_BY = "modify_user_id";
    String COLUMN_TENANT_ID = "tenant_id";
}
