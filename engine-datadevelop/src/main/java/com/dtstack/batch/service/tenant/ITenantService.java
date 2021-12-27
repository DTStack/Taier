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

package com.dtstack.batch.service.tenant;

import java.util.List;

/**
 * 项目相关接口
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ITenantService {

    Integer NORMAL_TABLE = 0;

    /**
     * 获取已经存在的database
     * @return
     */
    List<String> getRetainDB(Long tenantId,Long userId) throws Exception;

    List<String> getDBTableList(Long tenantId, Long userId, String dbName) throws Exception;
}
