/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dtstack.sql.flink;

import com.dtstack.google.common.collect.Lists;
import com.dtstack.sql.flink.api.FlinkTableLineage;
import com.dtstack.sql.flink.api.TableMata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Company: www.dtstack.com
 * 血缘解析
 * @author dapeng
 * @date 2020-10-19
 */
public class BloodParser {

    /**
     * 获取sql的血缘
     * @param sql
     * @return
     */
    public static List<FlinkTableLineage> getBlood(String sql){
        try {
            SqlTree sqlTree = SqlParser.parseTable(sql);
            return buildBlood(sqlTree);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static List<FlinkTableLineage> buildBlood(SqlTree sqlTree){
        List<FlinkTableLineage> flinkTableLineages = new ArrayList<>();
        for(InsertSqlParser.SqlParseResult sqlParseResult : sqlTree.getExecSqlList()){
            FlinkTableLineage data = new FlinkTableLineage();
            List<TableMata> sourceTables = Lists.newArrayList();
            List<TableMata> sideTables = Lists.newArrayList();
            parseSource(sqlParseResult.getSourceTableList(), sqlTree.getTmpTableMap(), sourceTables, sideTables, sqlTree.getPreDealTableMap());
            data.setSourceTables(sourceTables);
            data.setSideTables(sideTables);
            data.setSinkTables(getSinkTableMeta(sqlParseResult.getTargetTableList(), sqlTree.getPreDealTableMap()));
            flinkTableLineages.add(data);
        }
        return flinkTableLineages;
    }

    private static List<TableMata> getSinkTableMeta(List<String> tableNames,  Map<String, CreateTableParser.SqlParserResult> preDealTableMap){
        List<TableMata> result = Lists.newArrayList();
        for (String tableName: tableNames){
            CreateTableParser.SqlParserResult sqlParserResult = preDealTableMap.get(tableName);
            TableMata tableMata = new TableMata(tableName, sqlParserResult.getPropMap());
            result.add(tableMata);
        }
        return result;
    }

    private static void parseSource(List<String> tableNames, Map<String, CreateTmpTableParser.SqlParserResult> tmpTableCache, List<TableMata> sourceTables, List<TableMata> sideTables, Map<String, CreateTableParser.SqlParserResult> preDealTableMap){
        for(String tableName : tableNames){
            if(tmpTableCache.containsKey(tableName)){
                CreateTmpTableParser.SqlParserResult sqlParserResult = tmpTableCache.get(tableName);
                parseSource(sqlParserResult.getSourceTableList(), tmpTableCache, sourceTables, sideTables, preDealTableMap);
                continue;
            }
            //判断是否是维表（需要字段信息）
            CreateTableParser.SqlParserResult sqlParserResult = preDealTableMap.get(tableName);
            TableMata tableMata = new TableMata(tableName, sqlParserResult.getPropMap());
            if(AbstractTableInfoParser.checkIsSideTable(sqlParserResult.getFieldsInfoStr())){
                sideTables.add(tableMata);
            } else {
                sourceTables.add(tableMata);
            }
        }
    }

}
