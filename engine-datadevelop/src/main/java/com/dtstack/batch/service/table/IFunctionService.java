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

package com.dtstack.batch.service.table;

import com.dtstack.batch.domain.BatchFunction;

/**
 * 函数操作相关
 * Date: 2019/6/17
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IFunctionService {

    /**
     * 添加函数
     * @param dtuicTenantId
     * @param dbName
     * @param funcName
     * @param className
     * @param resource
     * @throws Exception
     */
    void addFunction(Long dtuicTenantId, String dbName, String funcName, String className, String resource, Long projectId) throws Exception;

    /**
     * 删除函数
     * @param dtuicTenantId
     * @param dbName
     * @param functionName
     */
    void deleteFunction(Long dtuicTenantId, String dbName, String functionName, Long projectId) throws Exception;

    /**
     * 新增存储过程
     * @param dtuicTenantId
     * @param dbName
     * @param batchFunction
     */
    void addProcedure(Long dtuicTenantId, String dbName, BatchFunction batchFunction);
}
