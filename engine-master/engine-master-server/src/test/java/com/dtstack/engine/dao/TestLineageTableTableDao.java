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
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestLineageTableTableDao
 * @Description
 * @Date 2020/11/16 15:14
 * @Created chener@dtstack.com
 */
public interface TestLineageTableTableDao {

    @Insert({"INSERT INTO lineage_table_table(dt_uic_tenant_id,app_type,input_table_id,input_table_key,result_table_id,result_table_key,table_lineage_key,lineage_source)VALUES(#{lineageTableTable.dtUicTenantId},#{lineageTableTable.appType},#{lineageTableTable.inputTableId},#{lineageTableTable.inputTableKey},#{lineageTableTable.resultTableId},#{lineageTableTable.resultTableKey},#{lineageTableTable.tableLineageKey},#{lineageTableTable.lineageSource})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageTableTable.id", keyColumn = "id")
    Integer insert(@Param("lineageTableTable")LineageTableTable lineageTableTable);

}
