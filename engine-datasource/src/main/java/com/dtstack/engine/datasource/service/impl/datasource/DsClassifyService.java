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

import com.dtstack.engine.datasource.dao.mapper.datasource.DsClassifyMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsClassify;
import com.dtstack.engine.datasource.mapstruct.DsClassStruct;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.vo.datasource.DsClassifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsClassifyService extends BaseService<DsClassifyMapper, DsClassify> {

    @Autowired
    private DsClassStruct dsClassStruct;

    /**
     * 获取数据源分类类目列表
     *
     * @return
     */
    public List<DsClassifyVO> queryDsClassifyList() {
        return  lambdaQuery().orderByDesc(DsClassify::getSorted).list().stream()
                .map(t -> {
                    DsClassifyVO dsClassifyVO = dsClassStruct.toDsClassifyVO(t);
                    dsClassifyVO.setClassifyId(t.getId());
                    return dsClassifyVO;
                }).collect(Collectors.toList());
    }
}
