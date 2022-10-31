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

package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


@Component
public class MysqlDbBuilder extends AbsRdbmsDbBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MysqlDbBuilder.class);

    private static Pattern pollColumn = Pattern.compile("^VARCHAR.*|^DATE.*|^DATETIME.*|^INT.*|^BIGINT.*|^TIMESTAMP.*");
    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MySQL;
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = super.listPollTableColumn(sourceDTO, tableName);
        return getByColumn(columns, pollColumn);
    }

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        return Collections.singletonList(getClient().getCurrentDatabase(sourceDTO));
    }
//
//    public Long getTableSize(Long sourceId, String tableName) {
//        Mysql5SourceDTO sourceDTO = (Mysql5SourceDTO)dataSourceCenterService.getSourceDTO(sourceId);
//        ITable table = ClientCache.getTable(sourceDTO.getSourceType());
//        try {
//           return table.getTableSize(sourceDTO, MysqlUtil.getDB(sourceDTO.getUrl()), tableName);
//        }catch (Exception e){
//            logger.error("获取表大小出错",e);
//            return 0L;
//        }
//    }
}
