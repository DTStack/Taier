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

package com.dtstack.taier.datasource.plugin.db2;

import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.source.DataBaseType;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:09 2020/1/7
 * @Description：Db2 工厂类
 */
public class Db2ConnFactory extends ConnFactory {
    public Db2ConnFactory() {
        this.driverName = DataBaseType.DB2.getDriverClassName();
        this.errorPattern = new Db2ErrorPattern();
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("CREATE PROCEDURE %s() \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("call %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE %s", procName);
    }
}
