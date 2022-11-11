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

package com.dtstack.taier.develop.service.template.bulider.reader;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;

import java.util.Map;

/**
 * Date: 2020/1/8
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public interface DaReaderBuilder {

    String JDBC_USERNAME = "username";

    String JDBC_PASSWORD = "password";

    String RDBMS_DA_TYPE = "rdbmsDaType";
    String HALF_STRUCTURE_DA_TYPE = "halfStructureDaType";

    String JDBC_URL = "jdbcUrl";

    String URL = "url";

    /**
     * 参数预处理
     * @param param
     */
    void setReaderJson(TaskResourceParam param);

    Reader daReaderBuild(TaskResourceParam param) throws Exception;

    /**
     * 基础的参数 例如 sourceId
     * @param sourceMap
     * @return
     */
    Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap);

    DataSourceType getDataSourceType();

}
