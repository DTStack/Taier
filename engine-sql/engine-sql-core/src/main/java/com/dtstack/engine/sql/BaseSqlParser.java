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


package com.dtstack.engine.sql;

import com.dtstack.engine.sql.handler.HiveUglySqlHandler;
import com.dtstack.engine.sql.handler.IUglySqlHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @date 2019/5/21
 */
public abstract class BaseSqlParser implements SqlParserImpl {

    private static final String LIFECYCLE_REGEX = "(?i)lifecycle\\s+(?<lifecycle>[1-9]\\d*)";

    private static final String CATALOGUE_REGEX = "(?i)catalogue\\s+(?<catalogue>[1-9]\\d*)";

    private static Pattern LIFECYCLE_PATTERN = Pattern.compile(LIFECYCLE_REGEX);

    private static Pattern CATALOGUE_PATTERN = Pattern.compile(CATALOGUE_REGEX);

    protected IUglySqlHandler uglySqlHandler = new HiveUglySqlHandler();

    public BaseSqlParser() {
    }

    public BaseSqlParser(IUglySqlHandler uglySqlHandler) {
        this.uglySqlHandler = uglySqlHandler;
    }

    /**
     * 默认生命周期
     */
    private static final int DEFAULT_LIFECYCLE = 9999;

    @Override
    public void parseLifecycleAndCatalogue(ParseResult parseResult) {
        String standardSql = parseResult.getOriginSql();

        Matcher matcher;
        if(parseResult.getMainTable().getLifecycle() == null){
            matcher = LIFECYCLE_PATTERN.matcher(standardSql);
            int lifecycle = DEFAULT_LIFECYCLE;
            if(matcher.find()){
                lifecycle = Integer.parseInt(matcher.group("lifecycle"));
                standardSql = matcher.replaceAll("");
            }
            parseResult.getMainTable().setLifecycle(lifecycle);
            parseResult.getMainTable().setOperate(TableOperateEnum.CREATE);
        }

        if(parseResult.getMainTable().getCatalogueId() == null){
            // 解析类目
            Long catalogueId = null;
            matcher = CATALOGUE_PATTERN.matcher(standardSql);
            if(matcher.find()){
                catalogueId = Long.parseLong(matcher.group("catalogue"));
                standardSql = matcher.replaceAll("");
            }
            parseResult.getMainTable().setCatalogueId(catalogueId);
        }

        parseResult.setStandardSql(standardSql);
    }
}
