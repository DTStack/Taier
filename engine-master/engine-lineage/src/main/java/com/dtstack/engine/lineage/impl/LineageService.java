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

package com.dtstack.engine.lineage.impl;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.lineage.enums.SourceType2TableType;
import com.dtstack.engine.lineage.util.SqlParserClientOperator;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.domain.ParseResult;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author chener
 * @Classname LineageService
 * @Description 血缘解析、存储service
 * @Date 2020/10/23 14:43
 * @Created chener@dtstack.com
 */
@Service
public class LineageService {
    private static final Logger logger = LoggerFactory.getLogger(LineageService.class);

    @Autowired
    private SqlParserClientOperator sqlParserClientOperator;

    @Autowired
    private EnvironmentContext env;

    @PostConstruct
    public void setUp() {
        System.setProperty("sqlParser.dir", env.getSqlParserDir());
    }


    /**
     * 解析sql基本信息
     *
     * @param sql 单条sql
     * @return
     */
    public ParseResult parseSql(String sql, String defaultDb, Integer dataSourceType) {
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dataSourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + dataSourceType + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
        ParseResult parseResult = null;
        try {
            parseResult = sqlParserClient.parseSql(sql, defaultDb, new HashMap<>(), sourceType2TableType.getTableType());
        } catch (Exception e) {
            logger.error("解析sql异常:", e);
            throw new RdosDefineException(e.getMessage(), ErrorCode.SQLPARSE_ERROR);
        }
        return parseResult;
    }

    public Set<String> parseFunction(String sql) {
        List<String> sqlList = SqlFormatUtil.splitSqlWithoutSemi(sql);
        Set<String> functionList = new HashSet<>();
        for (String s : sqlList) {
            ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
            try {
                Set<String> functions = sqlParserClient.parseFunction(s);
                functionList.addAll(functions);
            } catch (Exception e) {
                logger.error("parseFunction error:{}", e);
            }
        }
        return functionList;
    }


}