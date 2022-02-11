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

package com.dtstack.taiga.develop.service.develop;

import com.dtstack.taiga.develop.bo.ExecuteContent;
import com.dtstack.taiga.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taiga.develop.dto.devlop.ExecuteSqlParseVO;

/**
 * Reason:
 * Date: 2019/5/13
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ISqlExeService {

    /**
     * 执行sql,插件内部逻辑，需要根据sql类型做处理
     */
    ExecuteResultVO executeSql(ExecuteContent content) throws Exception;

    /**
     * 批量执行sparkSql；解决高级运行不允许sparkSql直连spark ThirftServer问题
     * @param content
     */
    ExecuteSqlParseVO batchExecuteSql(ExecuteContent content) throws Exception;

    void checkSingleSqlSyntax(Long tenantId, String sql, String db, String taskParam);

    /**
     * sql 语句整理
     * 去掉注释
     * 拼接上对应的schema
     * @param sqlText
     * @param database
     * @return
     */
    String process(String sqlText, String database);

}
