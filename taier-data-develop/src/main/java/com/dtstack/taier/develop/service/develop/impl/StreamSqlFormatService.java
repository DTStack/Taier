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

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.develop.sql.utils.SqlFormatUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class StreamSqlFormatService {

    private static final String ADD_JAR_FORMAT = "ADD JAR WITH %s;";

    private static final String ADD_JAR_WITH_MAIN_FORMAT = "ADD JAR WITH %s AS %s;";

    private static final String ADD_FILE_FORMAT = "ADD FILE WITH %s;";

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\s*([0-9a-zA-Z-_]+)\\s*\\(");

    private static final String ADD_FILE_RENAME_FORMAT = "ADD FILE WITH %s RENAME %s;";

    @Autowired
    private DevelopResourceService DevelopResourceService;

    /**
     * 获取sql中包含的方法名称
     *
     * @param sql
     * @return
     */
    public Set<String> getFuncName(String sql) {

        if (Strings.isNullOrEmpty(sql)) {
            return Sets.newHashSet();
        }
        sql = SqlFormatUtil.formatSql(sql);

        Set<String> funcSet = Sets.newHashSet();
        Matcher matcher = FUNCTION_PATTERN.matcher(sql);
        while (matcher.find()) {
            String funcName = matcher.group(1);
            funcSet.add(funcName.toUpperCase());
        }

        return funcSet;
    }


    public String generateAddJarSQL(Long resourceId, String mainClass) {
        DevelopResource resource = DevelopResourceService.getResource(resourceId);
        if (resource == null || StringUtils.isBlank(resource.getUrl())) {
            throw new DtCenterDefException("任务资源地址为空");
        }
        return generateAddJarSQL(resource.getUrl(), mainClass);
    }


    public String generateAddJarSQL(String url, String mainClass) {
        if (Strings.isNullOrEmpty(mainClass)) {
            return String.format(ADD_JAR_FORMAT, url.replaceAll("//*", "/"));
        } else {
            return String.format(ADD_JAR_WITH_MAIN_FORMAT, url.replaceAll("//*", "/"), mainClass);
        }
    }

    public String generateAddFileSQL(String path) {
        return String.format(ADD_FILE_FORMAT, path.replaceAll("//*", "/"));
    }

    /**
     * 生成添加文件的 SQL 并进行文件重命名
     *
     * @param path       文件路径
     * @param targetName 目标文件名
     * @return 生成的 SQL
     */
    public String generateAddFileRenameSQL(String path, String targetName) {
        return String.format(ADD_FILE_RENAME_FORMAT, path.replaceAll("//*", "/"), targetName);
    }
}
