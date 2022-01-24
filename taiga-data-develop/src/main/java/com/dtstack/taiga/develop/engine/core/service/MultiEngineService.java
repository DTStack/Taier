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

package com.dtstack.taiga.develop.engine.core.service;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.taiga.develop.service.datasource.impl.IMultiEngineService;
import com.dtstack.taiga.scheduler.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 和console交互获取多集群的配置信息
 * Date: 2019/4/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class MultiEngineService implements IMultiEngineService {

    @Autowired
    public ComponentService componentService;

    /**
     * 从console获取Hadoop的meta数据源
     * @param tenantId
     * @return
     */
    @Override
    public DataSourceType getTenantSupportHadoopMetaDataSource(Long tenantId) {
        Integer metaComponent = Engine2DTOService.getMetaComponent(tenantId);
        if (EComponentType.SPARK_THRIFT.getTypeCode().equals(metaComponent)){
            return DataSourceType.SparkThrift2_1;
        }
        throw new RdosDefineException("not find 'Hadoop' meta DataSource!");
    }

}
