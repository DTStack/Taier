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

package com.dtstack.taier.develop.service.template.bulider.writer;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.bulider.db.MysqlDbBuilder;
import com.dtstack.taier.develop.service.template.mysql.MySQLWriter;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriterParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: MySQL Writer Builder
 * @date 2021-11-03 16:36:39
 */
@Component
public class MySQLWriterBuilder extends AbsRDBWriterBuilder {

    @Autowired
    DsInfoService dataSourceAPIClient;
    @Autowired
    MysqlDbBuilder mysqlDbBuilder;

    @Override
    public DsInfoService getDataSourceAPIClient() {
        return dataSourceAPIClient;
    }

    @Override
    public RDBWriter getRDBWriter() {
        return new MySQLWriter();
    }

    @Override
    public RDBWriterParam getRDBWriterParam(TaskResourceParam param) {
        RDBWriterParam rdbWriterParam = JsonUtils.objectToObject(param.getTargetMap(), RDBWriterParam.class);
        return rdbWriterParam;
    }


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MySQL;
    }

    @Override
    public void preWriterJson(TaskResourceParam param) {
    }
}
