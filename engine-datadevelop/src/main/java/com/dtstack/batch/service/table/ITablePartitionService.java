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

/**
 * 表分区相关
 * Date: 2019/5/14
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ITablePartitionService {

    /**
     * 判断分区是否存在
     *
     * @param tenantId
     * @param partitionVal
     * @param tableName
     * @param db
     * @param tableType
     * @return
     */
    Boolean isPartitionExist(Long tenantId, String partitionVal, String tableName, String db, Integer tableType);
}
