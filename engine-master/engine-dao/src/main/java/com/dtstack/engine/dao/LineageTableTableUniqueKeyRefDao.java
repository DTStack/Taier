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

import com.dtstack.engine.domain.LineageTableTableUniqueKeyRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableAppRefDao
 * @Description
 * @Date 2020/10/28 18:01
 * @Created chener@dtstack.com
 */
public interface LineageTableTableUniqueKeyRefDao {
    Integer deleteByUniqueKey(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey);

    Integer deleteByUniqueKeyAndVersionId(@Param("appType") Integer appType, @Param("uniqueKey")String uniqueKey,@Param("versionId")Integer versionId);

    Integer deleteByLineageTableIdAndUniqueKey(@Param("appType")Integer appType, @Param("uniqueKey")String uniqueKey, @Param("lineageTableId")Long lineageTableId);

    Integer batchInsert(List<LineageTableTableUniqueKeyRef> resList);

    /**
     * 根据表血缘关系id集合逻辑删除表血缘关联关系
     * @param idList
     * @param appType
     */
    void deleteByLineageTableIdList(@Param("idList")List<Long> idList, @Param("appType") Integer appType);
}
