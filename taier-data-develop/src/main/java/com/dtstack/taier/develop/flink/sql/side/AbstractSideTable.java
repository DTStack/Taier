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

import com.dtstack.taier.common.util.AssertUtils;
import com.dtstack.taier.develop.flink.sql.core.AbstractBaseTable;
import com.dtstack.taier.develop.flink.sql.core.SqlConstant;

import java.util.List;
import java.util.Map;

/**
 * flink sql 维表抽象类
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public abstract class AbstractSideTable extends AbstractBaseTable {

    /**
     * 维表标识
     */
    protected static final String PERIOD_FOR_SYSTEM_TIME_KEY = "PERIOD FOR SYSTEM_TIME";

    @Override
    protected void addTableStructureParam(List<String> tableStructure) {
        addPrimaryKey(tableStructure);
    }

    @Override
    protected void addBaseParam(Map<String, Object> tableParam) {
    }

    @Override
    public void checkParam() {
        super.checkParam();
        AssertUtils.notBlank(getAllParam().getString(SqlConstant.SideTable.CACHE), "缓存策略不能为空.");
    }
}
