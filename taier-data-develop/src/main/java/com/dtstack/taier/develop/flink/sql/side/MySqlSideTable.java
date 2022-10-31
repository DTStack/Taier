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

package com.dtstack.taier.develop.flink.sql.side;


import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.side.param.MySqlSideParamEnum;

/**
 * mysql 维表
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class MySqlSideTable extends AbstractSideTable {

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return MySqlSideParamEnum.values();
    }

    @Override
    protected String getTypeBeforeVersion112() {
        return "mysql";
    }

    @Override
    protected String getTypeVersion112() {
        return "mysql-x";
    }
}
