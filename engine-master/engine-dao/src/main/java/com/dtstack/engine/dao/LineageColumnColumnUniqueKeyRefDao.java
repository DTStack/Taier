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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.LineageColumnColumnUniqueKeyRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageColumnColumnAppRefDao
 * @Description
 * @Date 2020/10/28 18:02
 * @Created chener@dtstack.com
 */
public interface LineageColumnColumnUniqueKeyRefDao {

    Integer batchInsert(List<LineageColumnColumnUniqueKeyRef> columnColumns);

    Integer deleteByUniqueKey(@Param("uniqueKey")String uniqueKey);

    Integer deleteByUniqueKeyAndVersionId(@Param("uniqueKey")String uniqueKey,@Param("versionId")Integer versionId);

    Integer deleteByLineageIdAndUniqueKey(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey, @Param("columnLineageId")Long columnLineageId);

    /**
     * 根据字段血缘id列表逻辑删除字段血缘关联关系
     * @param columnColumnIdList
     * @param appType
     */
    void deleteByLineageColumnIdList(@Param("columnColumnIdList") List<Long> columnColumnIdList,@Param("appType") Integer appType);

    Integer deleteByUniqueKeyAndAppType(@Param("uniqueKey")String uniqueKey,@Param("appType")Integer appType);

}
