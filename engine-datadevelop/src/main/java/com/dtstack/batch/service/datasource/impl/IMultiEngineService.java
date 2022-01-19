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

package com.dtstack.batch.service.datasource.impl;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.enums.EJobType;

import java.util.List;

/**
 * Reason:
 * Date: 2019/5/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IMultiEngineService {

    /**
     * 获取支持的引擎信息
     * @param dtuicTenantId
     * @return
     */
    List<Integer> getTenantSupportMultiEngine(Long dtuicTenantId);

    /**
     * 从console获取Hadoop的meta数据源
     * @param tenantId
     * @return
     */
    DataSourceType getTenantSupportHadoopMetaDataSource(Long tenantId);

    List<EJobType> getTenantSupportJobType(Long tenantId);

}
