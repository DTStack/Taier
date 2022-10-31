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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.DsFormField;
import com.dtstack.taier.dao.mapper.DsFormFieldMapper;
import com.dtstack.taier.develop.bo.datasource.DsTypeVersionParam;
import com.dtstack.taier.develop.vo.datasource.DsFormFieldVo;
import com.dtstack.taier.develop.vo.datasource.DsFormTemplateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/12
 */
@Service
public class DsFormFieldService extends ServiceImpl<DsFormFieldMapper, DsFormField> {

    private static String COMMON = "common";


    /**
     * 根据数据库类型和版本查找表单模版
     * @param param
     * @return
     */
    public DsFormTemplateVo findTemplateByTypeVersion(DsTypeVersionParam param) {
        DsFormTemplateVo returnVo = new DsFormTemplateVo();
        String typeVersion = param.getDataType();
        if (Strings.isNotBlank(param.getDataVersion())) {
            typeVersion = param.getDataType() + "-" + param.getDataVersion();
        }
        List<DsFormField> formFieldList = this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).
                or().eq("type_version", COMMON));
        List<DsFormFieldVo> formFieldVos = new ArrayList<>();
        for (DsFormField dsFormField : formFieldList) {
            DsFormFieldVo dsFormFieldVo = new DsFormFieldVo();
            BeanUtils.copyProperties(dsFormField,dsFormFieldVo);
            if(StringUtils.isNotBlank(dsFormField.getOptions())){
                List<Map> optionList = JSON.parseArray(dsFormField.getOptions(), Map.class);
                dsFormFieldVo.setOptions(optionList);
            }
            formFieldVos.add(dsFormFieldVo);
        }
        returnVo.setDataType(param.getDataType());
        returnVo.setDataVersion(param.getDataVersion());
        returnVo.setFromFieldVoList(formFieldVos);
        return returnVo;
    }

    /**
     * 根据数据源类型和版本获取具有连接性质的属性列表
     * @param dataType
     * @param dataVersion
     * @return
     */
    public List<DsFormField> findLinkFieldByTypeVersion(String dataType, String dataVersion) {
        String typeVersion = dataType;
        if (Strings.isNotBlank(dataVersion)) {
            typeVersion = dataType + "-" + dataVersion;
        }
        return this.list(Wrappers.<DsFormField>query().eq("type_version", typeVersion).eq("is_link", 1));
    }
}
