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

package com.dtstack.taier.develop.controller.datasource;


import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.bo.datasource.DsTypeVersionParam;
import com.dtstack.taier.develop.service.datasource.impl.DsFormFieldService;
import com.dtstack.taier.develop.utils.Asserts;
import com.dtstack.taier.develop.vo.datasource.DsFormTemplateVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@Api(tags = {"数据源中心-数据源表单模版化"})
@RestController
@RequestMapping(value = "/dataSource/dsForm")
public class DatasourceFormController {

    @Autowired
    private DsFormFieldService dsFormFieldService;

    @ApiOperation("根据数据库类型和版本查找表单模版")
    @PostMapping("/findFormByTypeVersion")
    public R<DsFormTemplateVo> findTemplateByTypeVersion(@RequestBody DsTypeVersionParam param) {
        return new APITemplate<DsFormTemplateVo>() {
            @Override
            protected void checkParams() throws IllegalArgumentException {
                Asserts.hasText(param.getDataType(), "数据源类型不能为空!");
            }
            @Override
            protected DsFormTemplateVo process() throws TaierDefineException {
                return dsFormFieldService.findTemplateByTypeVersion(param);
            }
        }.execute();
    }


}
