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

package com.dtstack.taier.develop.flink.sql.sink;


import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.sink.param.ES7SinkParamEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.dtstack.taier.develop.enums.develop.FlinkVersion.FLINK_112;


/**
 * mysql 结果表
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class ES7SinkTable extends AbstractSinkTable {

    @Override
    protected void addSelfParam(Map<String, Object> tableParam) {
        super.addSelfParam(tableParam);
    }

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return ES7SinkParamEnum.values();
    }

    @Override
    protected void addTableStructureParam(List<String> tableStructure) {
        if (FLINK_112.equals(version)) {
            String id = (String) getAllParam().get(ES7SinkParamEnum.id.getFront());
            if (StringUtils.isNotEmpty(id)) {
                tableStructure.add(String.format(PRIMARY_KEY_TEMPLATE, id));
            }
        } else {
            super.addTableStructureParam(tableStructure);
        }
    }

    @Override
    protected String getTypeBeforeVersion112() {
        return "elasticsearch7";
    }

    @Override
    protected String getTypeVersion112() {
        return "elasticsearch7-x";
    }
}
