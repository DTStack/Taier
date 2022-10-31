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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriter;
import com.dtstack.taier.develop.service.template.rdbms.RDBWriterParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: gengxin
 * @Date: 2021/10/30 4:43 下午
 */
public abstract class AbsRDBWriterBuilder implements DaWriterBuilder{

    public abstract DsInfoService getDataSourceAPIClient();
    public abstract RDBWriter getRDBWriter();
    public abstract RDBWriterParam getRDBWriterParam(TaskResourceParam param);
    public abstract void preWriterJson(TaskResourceParam param);


    @Override//参数预处理
    public void setWriterJson(TaskResourceParam param) {
        Long sourceId = Long.parseLong(param.getTargetMap().get("sourceId").toString());
        DsInfo data = this.getDataSourceAPIClient().getOneById(sourceId);
        param.getTargetMap().put("source", data);
        //获取到当前任务 目标源的 数据源类型
        param.getTargetMap().put("dataSourceType", data.getDataTypeCode());
        param.getTargetMap().putAll(JSON.parseObject(data.getDataJson()));
        List<Long> sourceIds = new ArrayList<>();
        sourceIds.add(sourceId);
        param.getTargetMap().put("sourceIds", sourceIds);
        preWriterJson(param);
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) throws Exception {
        this.setWriterJson(param);
        RDBWriterParam rdbReaderParam = this.getRDBWriterParam(param);
        JSONObject connection = new JSONObject(true);
        connection.put("jdbcUrl", rdbReaderParam.getJdbcUrl());
        connection.put("table", StringUtils.isNotBlank(rdbReaderParam.getTable()) ? Lists.newArrayList(rdbReaderParam.getTable()) : Lists.newArrayList());
        connection.put("schema", rdbReaderParam.getSchema());

        RDBWriter rdbWriter = this.getRDBWriter();
        rdbWriter.setConnection(Lists.newArrayList(connection));
        rdbWriter.setUsername(rdbReaderParam.getUsername());
        rdbWriter.setPassword(rdbReaderParam.getPassword());
        rdbWriter.setColumn(ColumnUtil.getColumns(rdbReaderParam.getColumn(),null));
        rdbWriter.setPreSql(StringUtils.isNotBlank(rdbReaderParam.getPreSql()) ? Lists.newArrayList(rdbReaderParam.getPreSql().trim().split(";")) : Lists.newArrayList());
        rdbWriter.setPostSql(StringUtils.isNotBlank(rdbReaderParam.getPostSql()) ? Lists.newArrayList(rdbReaderParam.getPostSql().trim().split(";")) : Lists.newArrayList());
        rdbWriter.setWriteMode(rdbReaderParam.getWriteMode());
        rdbWriter.setSourceIds(rdbReaderParam.getSourceIds());
        rdbWriter.setExtralConfig(rdbReaderParam.getExtralConfig());
        return rdbWriter;
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> targetMap) {
        try {
            RDBWriterParam param = JsonUtils.objectToObject(targetMap, RDBWriterParam.class);
            return JsonUtils.objectToMap(param);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("getParserTargetMap error,Caused by: %s", e.getMessage()), e);
        }
    }

}
