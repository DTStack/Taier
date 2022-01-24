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

package com.dtstack.taiga.dao.mapper;

import com.dtstack.taiga.dao.domain.BatchCatalogue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface BatchCatalogueDao {

    /**
     * 根据id查询目录
     * @param id
     * @return
     */
    BatchCatalogue getOne(@Param("id") Long id);

    /**
     * 根据名称和父目录Id查询
     * @param tenantId
     * @param nodePid
     * @param name
     * @return
     */
    BatchCatalogue getByPidAndName(@Param("tenantId") Long tenantId, @Param("nodePid") Long nodePid, @Param("name") String name);

    /**
     * 根据租户Id和父目录Id查询
     * @param nodePid
     * @param tenantId
     * @return
     */
    List<BatchCatalogue> listByPidAndTenantId(@Param("nodePid") Long nodePid, @Param("tenantId") Long tenantId);

    /**
     * 根据租户和目录等级查询
     * @param level
     * @param tenantId
     * @return
     */
    List<BatchCatalogue> listByLevelAndTenantId(@Param("level") Integer level, @Param("tenantId") Long tenantId);

    /**
     * 根据租户、目录等级、目录名称 查询
     * @param level
     * @param tenantId
     * @param name
     * @return
     */
    BatchCatalogue getByLevelAndTenantIdAndName(@Param("level") Integer level, @Param("tenantId") Long tenantId, @Param("name") String name);

    /**
     * 根据 名称 模糊查询
     * @param tenantId
     * @param name
     * @return
     */
    List<BatchCatalogue> listByNameFuzzy(@Param("tenantId") Long tenantId, @Param("name") String name);

    /**
     * 根据引擎类型 查询对应系统函数的根目录
     * @param nodePid
     * @return
     */
    BatchCatalogue getSystemFunctionCatalogueOne(@Param("nodePid") int nodePid);

    /**
     * 插入数据
     * @param batchCatalogue
     * @return
     */
    Integer insert(BatchCatalogue batchCatalogue);

    /**
     * 更新数据
     * @param batchCatalogue
     * @return
     */
    Integer update(BatchCatalogue batchCatalogue);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    Integer deleteById(@Param("id") Long id);

    /**
     * 校验目录下的 直接子目录 + 直接子文件 不超过2000条
     * @param nodePid
     * @param tenantId
     * @return
     */
    Integer getSubAmountsByNodePid(@Param("nodePid") Long nodePid, @Param("tenantId") Long tenantId);

    /**
     * 查询租户的根目录
     * @param tenantId
     * @param catalogueType
     * @return
     */
    BatchCatalogue getTenantRoot(@Param("tenantId") Long tenantId, @Param("catalogueType") Integer catalogueType);

    /**
     * 根据租户、名称、父目录id 查询
     * @param tenantId
     * @param name
     * @param parentId
     * @return
     */
    BatchCatalogue getBeanByTenantIdAndNameAndParentId(@Param("tenantId")Long tenantId, @Param("name")String name, @Param("parentId")Long parentId);

}
