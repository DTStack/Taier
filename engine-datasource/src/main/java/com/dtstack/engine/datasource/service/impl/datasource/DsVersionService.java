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

import com.dtstack.engine.datasource.dao.mapper.datasource.DsVersionMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsVersion;
import com.dtstack.engine.datasource.mapstruct.DsVersionStruct;
import com.dtstack.engine.datasource.param.datasource.DsVersionSearchParam;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.vo.datasource.DsVersionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsVersionService extends BaseService<DsVersionMapper, DsVersion> {

    @Autowired
    private DsVersionStruct dsVersionStruct;

    /**
     * 根据数据源类型获取版本列表
     *
     * @param searchParam
     * @return
     */
    public List<DsVersionVO> queryDsVersionByType(DsVersionSearchParam searchParam) {
        List<DsVersion> dsVersions = lambdaQuery().eq(DsVersion::getDataType, searchParam.getDataType())
                .orderByDesc(DsVersion::getSorted).list();

        return dsVersionStruct.toDsVersionVOs(dsVersions);
    }
}
