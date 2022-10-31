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

package com.dtstack.taier.datasource.plugin.presto;

import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;

/**
 * presto 数据源连接工厂类
 *
 * @author ：wangchuan
 * date：Created in 上午9:50 2021/3/23
 * company: www.dtstack.com
 */
public class PrestoConnFactory extends ConnFactory {

    public PrestoConnFactory() {
        driverName = DataBaseType.Presto.getDriverClassName();
        this.errorPattern = new PrestoErrorPattern();
        testSql = DataBaseType.Presto.getTestSql();
    }

    @Override
    protected String getCallProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    protected boolean supportTransaction() {
        return false;
    }

    protected boolean supportProcedure(String sql) {
        return false;
    }

    protected String getDropProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }
}
