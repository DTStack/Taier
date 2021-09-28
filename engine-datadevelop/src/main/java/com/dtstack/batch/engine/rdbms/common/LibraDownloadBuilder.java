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

package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;

/**
 * @author yuebai
 * @date 2019-06-10
 */
public class LibraDownloadBuilder {

    public IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        LibraDownload libraDownload = null;
        try {
            libraDownload = new LibraDownload(sql, dtuicTenantId, schema);
            libraDownload.configure();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return libraDownload;
    }

}
