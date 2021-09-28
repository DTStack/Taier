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

import com.dtstack.engine.datasource.common.enums.datasource.AppTypeEnum;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsAppListMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsAppList;
import com.dtstack.engine.datasource.mapstruct.DsAppListStruct;
import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import com.dtstack.engine.datasource.service.impl.BaseService;
import com.dtstack.engine.datasource.vo.datasource.ProductListVO;
import com.google.common.collect.Lists;
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
public class DsAppListService extends BaseService<DsAppListMapper, DsAppList> {


    @Autowired
    private DsAppListStruct dsAppListStruct;

    /**
     * 获取产品种类下拉列表  todo quanyue 实际只是本租户已开通的产品，不是全部产品，此信息需要从UIC获取, 目前为临时方法
     *
     * @return
     */
    public List<ProductListVO> getAppList(PubSvcBaseParam baseParam) {
        List<AppTypeEnum> uicAppTypeList = Lists.newArrayList(AppTypeEnum.RDOS);

        List<DsAppList> list = this.lambdaQuery().eq(DsAppList::getInvisible, 0).orderByDesc(DsAppList::getSorted).list();
        List<DsAppList> intersection = list.stream().filter(e -> AppTypeEnum.containAppType(uicAppTypeList, e.getAppType())).collect(Collectors.toList());

        return dsAppListStruct.toProductListVOs(intersection);
    }
}
