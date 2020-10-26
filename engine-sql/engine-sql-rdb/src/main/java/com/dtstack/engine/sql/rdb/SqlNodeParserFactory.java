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


package com.dtstack.engine.sql.rdb;

import com.dtstack.engine.sql.rdb.node.CreateTableSqlNodeParser;
import com.dtstack.engine.sql.rdb.node.InsertSqlNodeParser;
import com.dtstack.engine.sql.rdb.node.OtherSqlNodeParser;
import com.dtstack.engine.sql.rdb.node.SelectSqlNodeParser;

/**
 * SqlNode解析器工厂
 *
 * @author jiangbo
 * @date 2019/5/21
 */
public class SqlNodeParserFactory {

    private static SqlNodeParserFactory factory = new SqlNodeParserFactory();

    private SqlNodeParserFactory() {
    }

    public SqlNodeParserImpl getNodeParser(Class nodeClass) {
        SqlNodeParserImpl nodeParser;
        String className = nodeClass.getName().substring(nodeClass.getName().lastIndexOf(".") + 1);
        switch (className){
            case "SqlCreateTable" : nodeParser = new CreateTableSqlNodeParser(); break;
            case "SqlInsert" : nodeParser = new InsertSqlNodeParser(); break;
            case "SqlSelect" :
            case "SqlWith" :
            case "SqlOrderBy":
            case "SqlBasicCall" : nodeParser = new SelectSqlNodeParser(); break;
            default: nodeParser = new OtherSqlNodeParser();
        }

        return nodeParser;
    }

    public static SqlNodeParserFactory getInstance(){
        return factory;
    }
}
