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

package com.dtstack.engine.lineage.util;

import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.SqlParserClientCache;
import com.dtstack.sqlparser.common.client.exception.ClientAccessException;

/**
 *类名称:SqlParserClientOperator
 *类描述:TODO
 *创建人:newman
 *创建时间:2021/4/17 11:54 上午
 *Version 1.0
 */

public class SqlParserClientOperator {

    private static class SingletonHolder {
        private static SqlParserClientOperator singleton = new SqlParserClientOperator();
    }

    public static SqlParserClientOperator getInstance() {
        return SqlParserClientOperator.SingletonHolder.singleton;
    }

    public ISqlParserClient getClient(String name){
        ISqlParserClient sqlParserClient = null;
        try {
            sqlParserClient = SqlParserClientCache.getInstance().getClient(name);
        } catch (ClientAccessException e) {
            throw new RdosDefineException("get sqlParserClient error");
        }
        if(null == sqlParserClient){
            throw new RdosDefineException("get sqlParserClient error");
        }
        return sqlParserClient;
    }

    private SqlParserClientOperator() {
    }
}


