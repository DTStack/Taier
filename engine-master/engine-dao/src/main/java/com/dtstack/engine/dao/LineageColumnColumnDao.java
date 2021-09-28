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

import com.dtstack.engine.domain.LineageColumnColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author chener
 * @Classname LineageColumnColumnDao
 * @Description TODO
 * @Date 2020/10/22 20:05
 * @Created chener@dtstack.com
 */
public interface LineageColumnColumnDao {

    Integer batchInsertColumnColumn(List<LineageColumnColumn> columnColumns);

//    Integer deleteByUniqueKey(@Param("uniqueKey")String uniqueKey);

    List<LineageColumnColumn> queryColumnInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    List<LineageColumnColumn> queryColumnResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId,@Param("columnName")String columnName);

    LineageColumnColumn queryByLineageKey(@Param("appType")Integer appType,@Param("lineageKey")String lineageKey);

    List<LineageColumnColumn> queryByLineageKeys(@Param("appType")Integer appType, @Param("keys") Set<String> columnLineageKeys);

    List<String> queryTableLineageInputColumns(@Param("tableId")Long tableId);

    List<String> queryTableLineageResultColumns(@Param("tableId")Long tableId);

    /**
     * 根据taskId和appType查询字段血缘
     * @param taskId
     * @param appType
     * @return
     */
    List<LineageColumnColumn> queryColumnLineageByTaskIdAndAppType(@Param("taskId") Long taskId,@Param("appType") Integer appType);
}
