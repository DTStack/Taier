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
import com.dtstack.engine.datasource.dao.mapper.datasource.DsFormFieldMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsFormField;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.param.datasource.DsTypeVersionParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsFormFieldService extends BaseService<DsFormFieldMapper, DsFormField> {

    private static String COMMON = "common";

    @Autowired
    private DsTypeFieldRefService typeFieldRefService;

    /**
     * 根据数据源类型和版本获取表单属性列表
     * @param param
     * @return
     */
    public List<DsFormField> findFieldByTypeVersion(DsTypeVersionParam param) {
        String typeVersion = param.getDataType();
        if (StringUtils.isNotBlank(param.getDataVersion())) {
            typeVersion = param.getDataType() + "-" + param.getDataVersion();
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).or().eq("type_version", COMMON));
    }

    /**
     * 根据数据源类型和版本获取具有连接性质的属性列表
     * @param dataType
     * @param dataVersion
     * @return
     */
    public List<DsFormField> findLinkFieldByTypeVersion(String dataType, String dataVersion) {
        String typeVersion = dataType;
        if (StringUtils.isNotBlank(dataVersion)) {
            typeVersion = dataType + "-" + dataVersion;
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).eq("is_link", 1));
    }
}
