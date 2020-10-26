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
package com.dtstack.engine.sql.flink;

import com.dtstack.engine.sql.flink.api.FlinkTableLineage;
import org.apache.commons.compress.utils.Lists;

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
            List<String> sourceTables = Lists.newArrayList();
            List<String> sideTables = Lists.newArrayList();
            parseSource(sqlParseResult.getSourceTableList(), sqlTree.getTmpTableMap(), sourceTables, sideTables, sqlTree.getPreDealTableMap());
            FlinkTableLineage flinkTableLineage = new FlinkTableLineage();
            flinkTableLineage.setSideTables(sideTables);
            flinkTableLineage.setSinkTables(sqlParseResult.getTargetTableList());
            flinkTableLineage.setSourceTables(sourceTables);
            flinkTableLineages.add(flinkTableLineage);
        }
        return flinkTableLineages;
    }

    private static void parseSource(List<String> tableNames, Map<String, CreateTmpTableParser.SqlParserResult> tmpTableCache, List<String> sourceTables, List<String> sideTables, Map<String, CreateTableParser.SqlParserResult> preDealTableMap){
        for(String tableName : tableNames){
            if(tmpTableCache.containsKey(tableName)){
                CreateTmpTableParser.SqlParserResult sqlParserResult = tmpTableCache.get(tableName);
                parseSource(sqlParserResult.getSourceTableList(), tmpTableCache, sourceTables, sideTables, preDealTableMap);
                continue;
            }
            //判断是否是维表（需要字段信息）
            CreateTableParser.SqlParserResult sqlParserResult = preDealTableMap.get(tableName);
            if(AbstractTableInfoParser.checkIsSideTable(sqlParserResult.getFieldsInfoStr())){
                sideTables.add(tableName);
            } else {
                sourceTables.add(tableName);
            }
        }
    }

    public static void main(String[] args) {
        String sql = "-- name 21763_side_join_start_join_side\n" +
                "-- type FlinkSQL\n" +
                "-- author admin@dtstack.com\n" +
                "-- create time 2020-02-25 17:45:37\n" +
                "-- desc \n" +
                "\n" +
                "CREATE TABLE MyTable(\n" +
                "    id INT,\n" +
                "    channel VARCHAR,\n" +
                "    pv varchar,\n" +
                "    xctime varchar,\n" +
                "    name varchar\n" +
                " )WITH(\n" +
                "    type ='kafka10',\n" +
                "    bootstrapServers ='172.16.8.107:9092',\n" +
                "    zookeeperQuorum ='172.16.8.107:2181/kafka',\n" +
                "    offsetReset ='latest',\n" +
                "    topic ='feiyutest',\n" +
                "    timezone='Asia/Shanghai',\n" +
                "    topicIsPattern ='false',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                "CREATE TABLE sideTableA(\n" +
                "    id INT,\n" +
                "    channel varchar,\n" +
                "    time_info varchar,\n" +
                "    name varchar,\n" +
                "    price double,\n" +
                "    PRIMARY KEY(channel),\n" +
                "    PERIOD FOR SYSTEM_TIME\n" +
                " )WITH(\n" +
                "    type ='mysql',\n" +
                "    url ='jdbc:mysql://172.16.10.134:3306/yeluo_test',\n" +
                "    userName ='dtstack',\n" +
                "    password ='abc123',\n" +
                "    tableName ='sideTableA',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                " CREATE TABLE MyTableB(\n" +
                "    id INT,\n" +
                "    channel varchar,\n" +
                "    address varchar,\n" +
                "    name varchar\n" +
                " )WITH(\n" +
                "    type ='kafka10',\n" +
                "    bootstrapServers ='172.16.8.107:9092',\n" +
                "    zookeeperQuorum ='172.16.8.107:2181/kafka',\n" +
                "    offsetReset ='latest',\n" +
                "    topic ='est_test',\n" +
                "    timezone='Asia/Shanghai',\n" +
                "    topicIsPattern ='false',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                "  CREATE TABLE sideTableC(\n" +
                "    channel varchar,\n" +
                "    address varchar,\n" +
                "    name varchar,\n" +
                "    PRIMARY KEY(channel),\n" +
                "    PERIOD FOR SYSTEM_TIME\n" +
                " )WITH(\n" +
                "    type ='mysql',\n" +
                "    url ='jdbc:mysql://172.16.10.134:3306/yeluo_test',\n" +
                "    userName ='dtstack',\n" +
                "    password ='abc123',\n" +
                "    tableName ='sideTableC',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                " CREATE TABLE MyTableD(\n" +
                "    id INT,\n" +
                "    channel VARCHAR,\n" +
                "    pv varchar,\n" +
                "    xctime varchar,\n" +
                "    name varchar\n" +
                " )WITH(\n" +
                "    type ='kafka10',\n" +
                "    bootstrapServers ='172.16.8.107:9092',\n" +
                "    zookeeperQuorum ='172.16.8.107:2181/kafka',\n" +
                "    offsetReset ='latest',\n" +
                "    topic ='feiyutest',\n" +
                "    timezone='Asia/Shanghai',\n" +
                "    topicIsPattern ='false',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                " CREATE TABLE MyTableF(\n" +
                "    id INT,\n" +
                "    channel VARCHAR,\n" +
                "    pv varchar,\n" +
                "    xctime varchar,\n" +
                "    name varchar\n" +
                " )WITH(\n" +
                "    type ='kafka10',\n" +
                "    bootstrapServers ='172.16.8.107:9092',\n" +
                "    zookeeperQuorum ='172.16.8.107:2181/kafka',\n" +
                "    offsetReset ='latest',\n" +
                "    topic ='feiyutest',\n" +
                "    timezone='Asia/Shanghai',\n" +
                "    topicIsPattern ='false',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                "CREATE TABLE MyResult(\n" +
                "    id double,\n" +
                "    xctime VARCHAR,\n" +
                "    name VARCHAR,\n" +
                "    name1 VARCHAR,\n" +
                "    name2 VARCHAR,\n" +
                "    name3 varchar,\n" +
                "    time_info VARCHAR,\n" +
                "    address VARCHAR\n" +
                " )WITH(\n" +
                "    type ='mysql',\n" +
                "    url ='jdbc:mysql://172.16.10.134:3306/yeluo_test',\n" +
                "    userName ='dtstack',\n" +
                "    password ='abc123',\n" +
                "    tableName ='MyResult',\n" +
                "    parallelism ='1'\n" +
                " );\n" +
                "\n" +
                "insert \n" +
                "into\n" +
                "    MyResult\n" +
                "    select\n" +
                "        cast(t1.id as double) AS id,\n" +
                "        t1.xctime as xctime,\n" +
                "        t1.name as name,\n" +
                "        t2.name as name1,\n" +
                "        t3.name as name2,\n" +
                "        t4.name as name3,\n" +
                "        t2.time_info as time_info,\n" +
                "        t3.address as address     \n" +
                "    from\n" +
                "    (\n" +
                "    select\n" +
                "    id, \n" +
                "    name,\n" +
                "    channel,\n" +
                "    pv,\n" +
                "    xctime\n" +
                "    from \n" +
                "        MyTable \n" +
                "    ) t1    \n" +
                "    left join\n" +
                "        sideTableA t2                           \n" +
                "        on  t1.channel = t2.channel                                   \n" +
                "    join MyTableB t3\n" +
                "        on t1.channel = t3.channel\n" +
                "    join sideTableC t4\n" +
                "        on t1.channel = t4.channel  \n" +
                "    join MyTableD t5\n" +
                "        on t1.channel = t5.channel\n" +
                "    join MyTableF t6\n" +
                "        on t1.channel = t6.channel          \n" +
                "    where t1.name = 'xc';";
        List<FlinkTableLineage> blood = BloodParser.getBlood(sql);
        System.out.println(blood);
    }

}
