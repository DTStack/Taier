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

package com.dtstack.taier.develop.service.datasource.impl;

import com.dtstack.taier.dao.domain.DsClassify;
import com.dtstack.taier.dao.mapper.DsClassifyMapper;
import com.dtstack.taier.develop.mapstruct.datasource.DsClassifyTransfer;
import com.dtstack.taier.develop.vo.datasource.DsClassifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Service
public class DsClassifyService {


    @Autowired
    private DsClassifyMapper dsClassifyMapper;

    /**
     * 获取数据源分类类目列表
     *
     * @return
     */
    public List<DsClassifyVO> queryDsClassifyList() {
        List<DsClassify> dsClassifyList = dsClassifyMapper.queryDsClassifyList();
        return dsClassifyList.stream().map
                (DsClassifyTransfer.INSTANCE::toInfoVO).collect(Collectors.toList());
    }
}
