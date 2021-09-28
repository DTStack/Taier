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

import com.dtstack.engine.domain.LineageTableTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableDao
 * @Description 表级血缘表
 * @Date 2020/10/22 20:05
 * @Created chener@dtstack.com
 */
public interface LineageTableTableDao {

    Integer batchInsertTableTable(@Param("list") List<LineageTableTable> lineageTableTable);

    List<LineageTableTable> queryTableInputList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

    List<LineageTableTable> queryTableResultList(@Param("appType")Integer appType,@Param("tableId")Long tableId);

    LineageTableTable queryBTableLineageKey(@Param("appType")Integer appType,@Param("tableLineageKey")String tableLineageKey);

    List<LineageTableTable> queryByTableLineageKeys(@Param("appType")Integer appType,@Param("list") List<String> keys);

    /**
     * 查询表血缘直接上游数量
     * @param appType
     * @param tableId
     * @return
     */
    Integer queryTableResultCount(@Param("appType") Integer appType,@Param("tableId") Long tableId);

    /**
     * 查询表血缘直接下游数量
     * @param appType
     * @param tableId
     * @return
     */
    Integer queryTableInputCount(@Param("appType") Integer appType,@Param("tableId") Long tableId);

    /**
     * 根据taskId和appType查询表血缘
     * @param taskId
     * @param appType
     * @return
     */
    List<LineageTableTable> queryTableLineageByTaskIdAndAppType(@Param("taskId") Long taskId, @Param("appType") Integer appType);
}
