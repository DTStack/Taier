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


package com.dtstack.engine.sql.parse;

import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.enums.ETableType;
import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import com.dtstack.engine.sql.handler.LibraUglySqlHandler;
import com.dtstack.engine.sql.hive.AstNodeParser;
import com.dtstack.engine.sql.rdb.CalciteNodeParser;


/**
 * @author jiangbo
 * @date 2019/5/18
 */
public class SqlParserFactory {

    private static SqlParserFactory factory = new SqlParserFactory();

    private SqlParserFactory() {
    }

    public static SqlParserFactory getInstance() {
        return factory;
    }

    /**
     * 获取sql解析器
     *
     * @param tableType 数据库类型
     * @return
     */
    public SqlParserImpl getSqlParser(ETableType tableType) {
        SqlParserImpl sqlParser;
        switch (tableType) {
            case HIVE:
                sqlParser = new AstNodeParser(new HiveUglySqlHandler());
                break;
            case LIBRA:
            case ORACLE:
            case GREENPLUM:
            case TIDB:
                sqlParser = new CalciteNodeParser(new LibraUglySqlHandler());
                break;
            case IMPALA:
                sqlParser = new AstNodeParser(new ImpalaUglySqlHandler());
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type:" + tableType.name());
        }

        return sqlParser;
    }
}
