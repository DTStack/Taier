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

package com.dtstack.engine.datasource.service.impl.datasource;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsAppMappingMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsAppMapping;
import com.dtstack.engine.datasource.service.impl.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsAppMappingService extends BaseService<DsAppMappingMapper, DsAppMapping> {

    /**
     * 通过产品类型获取数据源类型列表(去重)
     * @param appType
     * @return
     */
    public List<DsAppMapping> groupListByAppType(Integer appType) {
        return list(Wrappers.<DsAppMapping>query()
                .select("data_type").eq(Objects.nonNull(appType), "app_type", appType).last("group by data_type order by id asc"));
    }

}
