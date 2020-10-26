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


package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.hive.hive.*;
import org.apache.hadoop.hive.ql.parse.HiveParser;

/**
 * hive sql解析器工厂类
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class AstNodeParserFactory {

    private static final String INSERT_REGEX = "(?i)insert\\s+.*";

    private static AstNodeParserFactory factory = new AstNodeParserFactory();

    public static AstNodeParserFactory getInstance(){
        return factory;
    }

    public ASTNodeParserImpl getHiveSqlParser(int operatorType, String sql){
        ASTNodeParserImpl sqlParser;

        if(sql.matches(INSERT_REGEX)){
            sqlParser = new InsertAstNodeParser();
            return sqlParser;
        }

        switch (operatorType){
            case HiveParser.TOK_CREATETABLE: sqlParser = new CreateTableAstNodeParser(); break;
            case HiveParser.TOK_ALTERTABLE: sqlParser = new AlterAstNodeParser(); break;
            case HiveParser.TOK_QUERY: sqlParser = new SelectAstNodeParser(); break;
            default: sqlParser = new OtherAstNodeParser();
        }

        return sqlParser;
    }

    private AstNodeParserFactory() {
    }
}
