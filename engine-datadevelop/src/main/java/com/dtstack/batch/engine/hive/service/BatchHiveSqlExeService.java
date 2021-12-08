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

package com.dtstack.batch.engine.hive.service;

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.engine.hdfs.service.BatchSparkHiveSqlExeService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.EJobType;
import org.springframework.stereotype.Service;

/**
 * @author sanyue
 * @date 2019/10/30
 */
@Service
public class BatchHiveSqlExeService extends BatchSparkHiveSqlExeService implements ISqlExeService {

//    @Override
//    public ExecuteResultVO executeSql(ExecuteContent executeContent) {
//        return executeSql(executeContent, EJobType.HIVE_SQL);
//    }

    //todo  待删除
    @Override
    public ExecuteResultVO executeSql(ExecuteContent content) throws Exception {
        return null;
    }

    @Override
    public ExecuteSqlParseVO batchExecuteSql(ExecuteContent content) {
        return null;
    }

    @Override
    public void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam) {
        checkSingleSqlSyntax(projectId, dtuicTenantId, sql, db, taskParam, EJobType.HIVE_SQL);
    }

    @Override
    public String process(String sqlText, String database) {
        return processSql(sqlText, database);
    }

}