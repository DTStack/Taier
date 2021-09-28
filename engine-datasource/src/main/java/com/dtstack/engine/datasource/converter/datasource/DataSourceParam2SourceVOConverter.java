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

package com.dtstack.engine.datasource.converter.datasource;

import com.dtstack.engine.common.lang.convert.Converter;
import com.dtstack.engine.datasource.common.utils.DataSourceUtils;
import com.dtstack.engine.datasource.param.datasource.AddDataSourceParam;
import com.dtstack.engine.datasource.vo.datasource.DataSourceVO;
import org.apache.commons.lang3.StringUtils;

/**
 * AddDataSourceParam 转 DataSourceVO
 * @description:
 * @author: liuxx
 * @date: 2021/3/24
 */
public class DataSourceParam2SourceVOConverter extends Converter<AddDataSourceParam, DataSourceVO> {

    @Override
    protected DataSourceVO doConvert(AddDataSourceParam source) {
        DataSourceVO returnVo = new DataSourceVO();
        returnVo.setId(source.getId());
        returnVo.setUserId(source.getUserId());
        returnVo.setDtuicTenantId(source.getTenantId());
        returnVo.setTenantId(source.getTenantId());
        returnVo.setDataName(source.getDataName());
        returnVo.setDataDesc(source.getDataDesc());
        returnVo.setDataType(source.getDataType());
        returnVo.setDataVersion(source.getDataVersion());
        returnVo.setDataJsonString(source.getDataJsonString());
        if (StringUtils.isNotBlank(source.getDataJsonString())) {
            returnVo.setDataJson(DataSourceUtils.getDataSourceJson(source.getDataJsonString()));
        } else {
            returnVo.setDataJson(source.getDataJson());
        }
        returnVo.setAppTypeList(source.getAppTypeList());
        return returnVo;
    }
}
