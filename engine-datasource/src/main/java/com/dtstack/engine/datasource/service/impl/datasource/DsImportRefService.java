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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import com.dtstack.engine.datasource.dao.mapper.datasource.DsImportRefMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsImportRef;
import com.dtstack.engine.datasource.service.impl.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Service
public class DsImportRefService extends BaseService<DsImportRefMapper, DsImportRef> {

    @Resource
    private DsImportRefMapper dsImportRefMapper;

    /**
     * 取消数据源在产品中的引入
     *
     * @param dataInfoIds
     * @param appType
     * @param dtUicTenantId
     * @param projectId
     * @return
     */
    public Boolean cancelImport(List<Long> dataInfoIds, Integer appType,Long dtUicTenantId,Long projectId) {
        Asserts.notEmpty(dataInfoIds, "数据源主键List不能为空!");
        dtUicTenantId = null == dtUicTenantId ? 0 : dtUicTenantId;
        projectId = null == projectId ? -1 : projectId;
        LambdaQueryWrapper<DsImportRef> qw = Wrappers.lambdaQuery();
        qw.in(DsImportRef::getDataInfoId, dataInfoIds).eq(DsImportRef::getAppType, appType)
        .eq(DsImportRef::getProjectId,projectId).eq(DsImportRef::getDtUicTenantId,dtUicTenantId);
        return this.getBaseMapper().delete(qw) > 0;
    }

    /**
     * 根据产品类型获取对应的数据源
     *
     * @param appType
     * @return
     */
    public List<Long> getDataInfoIdByType(Integer appType) {
        Objects.requireNonNull(appType);
        List<DsImportRef> dsImportRefs = lambdaQuery().select(DsImportRef::getDataInfoId)
                .eq(DsImportRef::getAppType, appType).list();
        return dsImportRefs.stream().map(DsImportRef::getDataInfoId).collect(Collectors.toList());
    }

    /**
     * 根据数据源Id获取对应的编码
     * @param dataInfoId
     * @return
     */
    public List<Integer> getCodeByDsId(Long dataInfoId) {
        Objects.requireNonNull(dataInfoId);
        return lambdaQuery().eq(DsImportRef::getDataInfoId, dataInfoId).list()
                .stream().map(DsImportRef::getAppType).collect(Collectors.toList());
    }




    /**
     * 查询引入表判断是否是迁移的数据源
     * @param dsInfoId
     * @return
     */
    public List<DsImportRef> getImportDsByInfoId(Long dsInfoId){

        return  dsImportRefMapper.getImportDsByInfoId(dsInfoId);
    }

    public List<DsImportRef> getImportDsByInfoIdList(List<Long> dsInfoIdList) {
        if (CollectionUtils.isEmpty(dsInfoIdList)){
            return Collections.emptyList();
        }
        return dsImportRefMapper.getImportDsByInfoIdList(dsInfoIdList);
    }
}
