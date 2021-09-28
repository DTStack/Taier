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

package com.dtstack.engine.datasource.dao;

import com.google.common.base.CaseFormat;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
public interface BaseMapperField {
    String COLUMN_ID = "id";
    String COLUMN_DELETED = "is_deleted";
    String COLUMN_CREATE_AT = "gmt_create";
    String COLUMN_UPDATE_AT = "gmt_modified";
    String COLUMN_CREATE_BY = "create_user_id";
    String COLUMN_UPDATE_BY = "modify_user_id";
    String COLUMN_TENANT_ID = "tenant_id";
    String COLUMN_DTUIC_TENANT_ID = "dtuic_tenant_id";
    String COLUMN_PROJECT_ID = "project_id";
    String FIELD_CREATE_AT = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_CREATE_AT);
    String FIELD_CREATE_BY = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_CREATE_BY);
    String FIELD_UPDATE_AT = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_UPDATE_AT);
    String FIELD_UPDATE_BY = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_UPDATE_BY);
    String FIELD_TENANT_ID = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_TENANT_ID);
    String FIELD_DTUIC_TENANT_ID = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_DTUIC_TENANT_ID);
    String FIELD_PROJECT_ID = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, COLUMN_PROJECT_ID);
}
