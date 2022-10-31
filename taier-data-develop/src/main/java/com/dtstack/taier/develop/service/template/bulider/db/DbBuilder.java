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
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.vo.datasource.DsPreviewResultVO;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 上午10:37 2020/7/16
 * @Description：
 */
public interface DbBuilder {

    /**
     * 获取对应的client客户端 - 插件化支持
     * @return
     */
    IClient getClient();

    /**
     * 获取数据源类型
     * @return
     */
    DataSourceType getDataSourceType();

    /**
     * 间接轮询数据预览
     * @param tableName
     * @param sourceDTO
     * @return
     */
    JSONObject pollPreview(String tableName, ISourceDTO sourceDTO);

    /**
     * 间接轮询表字段
     * @param sourceDTO
     * @param tableName
     * @return
     */
    List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName);

    /**
     * 获取schema信息
     * @param sourceDTO
     * @param db
     * @return
     */
    List<String> listSchemas(ISourceDTO sourceDTO, String db);

    /**
     * 根据schema获取table
     *
     * @param schema           schema
     * @param tableNamePattern 模糊查询
     * @param db               数据库
     * @return table 集合
     */
    List<String> listTablesBySchema(String schema,  String tableNamePattern, ISourceDTO sourceDTO, String db);

    /**
     * 生成数据源连接信息，数据预览安全审计使用
     *
     * @param dataJson 数据源信息
     * @return 数据源连接信息
     */
    String buildConnMsgForSA(JSONObject dataJson);
}
