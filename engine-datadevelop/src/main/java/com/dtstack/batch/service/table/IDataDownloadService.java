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


import com.dtstack.batch.engine.rdbms.common.IDownload;

import java.util.List;

/**
 * 下载相关逻辑
 * Date: 2019/5/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IDataDownloadService {
    /**
     * 下载sql查询的数据
     * @param jobId
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param needMask
     * @return
     * @throws Exception
     */
    IDownload downloadSqlExeResult(String jobId, Long tenantId, Long projectId, Long dtuicTenantId, boolean needMask);


    /**
     * 数据查询
     * 比如用在:
     *    数据预览
     *    临时表查询数据
     * @param dtuicTenantId
     * @param tableName
     * @param db
     * @param num
     * @param fieldNameList
     * @param permissionStyle
     * @param needMask
     * @return
     * @throws Exception
     */
    List<Object> queryDataFromTable(Long dtuicTenantId, Long projectId, String tableName, String db, Integer num,
                                    List<String> fieldNameList, Boolean permissionStyle, boolean needMask) throws Exception;

    IDownload buildIDownLoad(String jobId, Integer taskType, Long dtuicTenantId, Integer limitNum);

    IDownload typeLogDownloader(Long dtuicTenantId, String jobId, Integer limitNum, String logType);

}
