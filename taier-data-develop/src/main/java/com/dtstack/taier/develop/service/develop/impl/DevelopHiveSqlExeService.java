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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.ISqlExeService;
import org.springframework.stereotype.Service;

@Service
public class DevelopHiveSqlExeService extends DevelopSparkHiveSqlExeService implements ISqlExeService {

    @Override
    public ExecuteResultVO executeSql(ExecuteContent executeContent) {
        return executeSql(executeContent, EScheduleJobType.HIVE_SQL);
    }

    @Override
    public String process(String sqlText, String database) {
        return processSql(sqlText, database);
    }

}
