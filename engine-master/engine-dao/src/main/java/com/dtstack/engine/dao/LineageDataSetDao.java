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

import com.dtstack.engine.domain.LineageDataSetInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableInfo
 * @Description dataSet：数据集，一般为表；也可能是文件或者kafka数据流等形式
 * @Date 2020/10/22 20:04
 * @Created chener@dtstack.com
 */
public interface LineageDataSetDao {

    Integer insertTableInfo(LineageDataSetInfo lineageDataSetInfo);

    LineageDataSetInfo getTableInfo(@Param("sourceId")Long sourceId, @Param("db")String db, @Param("tableName")String tableName);

    Integer deleteTableInfo(@Param("id")Long id);

    LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(@Param("sourceId") Long sourceId, @Param("dbName") String dbName,
                                                             @Param("tableName") String tableName,@Param("schemaName") String schemaName,@Param("appType")Integer appType);

    /**
     * @author zyd
     * @Description 根据id查询表信息
     * @Date 2020/11/11 5:12 下午
     * @param id:
     * @return: com.dtstack.engine.domain.LineageDataSetInfo
     **/
    LineageDataSetInfo getOneById(Long id);

    /**
     * @author zyd
     * @Description 根据ids批量查询表信息
     * @Date 2020/11/11 5:15 下午
     * @param ids:
     * @return: com.dtstack.engine.domain.LineageDataSetInfo
     **/
    List<LineageDataSetInfo> getDataSetListByIds(@Param("ids") List<Long> ids);

    /**
     * 根据oldTableKey修改表名和tableKey
     * @param newTableName
     * @param oldTableKey
     * @param newTableKey
     */
    void updateTableNameByTableNameAndSourceId(@Param("newTableName") String newTableName,@Param("oldTableKey") String oldTableKey,@Param("newTableKey") String newTableKey);

    List<LineageDataSetInfo> getListByParams(@Param("sourceId") Long sourceId, @Param("dbName") String dbName,
                                             @Param("tableName") String tableName,@Param("schemaName") String schemaName,@Param("appType")Integer appType);
}
