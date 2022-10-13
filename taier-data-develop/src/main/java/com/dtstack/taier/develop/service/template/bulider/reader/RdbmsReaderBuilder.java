package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.ColumnDTO;
import com.dtstack.taier.develop.dto.devlop.ConnectionDTO;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReader;
import com.dtstack.taier.develop.service.template.rdbms.RdbmsPollReaderParam;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RdbmsReaderBuilder implements DaReaderBuilder {
    @Override
    public void setReaderJson(TaskResourceParam param) {
        Map<String, Object> sourceMap = param.getSourceMap();
//        List<Long> sourceIds = new ArrayList<>();
//
//        List<Object> sourceList = (List<Object>) sourceMap.get("sourceList");
//        JSONArray connections = new JSONArray();
//        for (Object dataSource : sourceList) {
//            Map<String, Object> source = (Map<String, Object>) dataSource;
//            Long sourceId = Long.parseLong(source.get("sourceId").toString());
////            DsServiceInfoDTO batchDataSource = getOne(sourceId);
//            DsServiceInfoDTO batchDataSource = null;
//
//            JSONObject json = JSON.parseObject(batchDataSource.getDataJson());
//            JSONObject conn = new JSONObject();
//
//            conn.put("jdbcUrl", Collections.singletonList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)));
//
//            if (source.get("tables") instanceof String) {
//                conn.put("table", Collections.singletonList(source.get("tables")));
//            } else {
//                conn.put("table", source.get("tables"));
//            }
//
//            conn.put("type", batchDataSource.getType());
//            conn.put("sourceId", sourceId);
//
//            connections.add(conn);
//            sourceIds.add(sourceId);
//
//            source.put("name", batchDataSource.getDataName());
//            if (sourceMap.get("source") == null) {
//                sourceMap.put("source", batchDataSource);
//            }
//            if (sourceMap.get("datasourceType") == null) {
//                sourceMap.put("dataSourceType", batchDataSource.getType());
//            }
//        }
//
//        Map<String, Object> source = (Map<String, Object>) sourceList.get(0);
//        DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(Integer.valueOf(sourceMap.get("type").toString()));
//        sourceMap.put("sourceId", source.get("sourceId"));
//        sourceMap.put("name", source.get("name"));
//        sourceMap.put("type", dataBaseType);
//        sourceMap.put("connections", connections);
//        sourceMap.put("sourceIds", sourceIds);


        //for hive writer
        String tableName = MapUtils.getString(sourceMap, "tableName");
        List<String> table = Lists.newArrayList(tableName);
        sourceMap.put("table", table);
    }

    public Reader daReaderBuild(TaskResourceParam param, RdbmsPollReader rdbmsPollReader, IClient client, RdbmsSourceDTO sourceDTO) throws Exception {
        Map<String, Object> sourceMap = param.getSourceMap();
        DsInfo dataSource = (DsInfo) sourceMap.get("source");
        JSONObject json = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());

        RdbmsPollReaderParam readerParam = JsonUtils.objectToObject(sourceMap, RdbmsPollReaderParam.class);

        rdbmsPollReader.setPollingInterval(readerParam.getPollingInterval());
        rdbmsPollReader.setStartLocation(readerParam.getStartLocation());
        String schema = MapUtils.getString(param.getSourceMap(), "schema");
        // 设置 schema
        sourceDTO.setSchema(schema);
        List<ConnectionDTO> connectionDTOList = new ArrayList<>();
        // 有 schema 信息拼接 schema
        String tableName = StringUtils.isNotBlank(schema) ? schema + "." + readerParam.getTableName() : readerParam.getTableName();

        //设置链接信息
        ConnectionDTO connectionDTO = new ConnectionDTO();
        connectionDTO.setJdbcUrl(Lists.newArrayList(DataSourceUtils.getJdbcUrl(json)));
        connectionDTO.setTable((Lists.newArrayList(tableName)));
        connectionDTOList.add(connectionDTO);

        rdbmsPollReader.setConnection(connectionDTOList);
        rdbmsPollReader.setUsername(DataSourceUtils.getJdbcUsername(json));
        rdbmsPollReader.setPassword(DataSourceUtils.getJdbcPassword(json));

        //获取表对应的字段
        List<ColumnDTO> columns = new ArrayList<>();
        if (StringUtils.isNotBlank(readerParam.getTableName())) {
            try {
                SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(readerParam.getTableName()).build();
                List<ColumnMetaDTO> columnMetaDTOList = client.getColumnMetaData(sourceDTO, sqlQueryDTO);
                List<JSONObject> list = new ArrayList<>();
                List<String> fields = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(columnMetaDTOList)) {
                    for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                        if (sourceMap.get("tableFields") == null) {
                            list.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                            fields.add(columnMetaDTO.getKey());
                        } else {
                            List<String> tableFields = (List<String>) sourceMap.get("tableFields");
                            if (tableFields.contains(columnMetaDTO.getKey())) {
                                list.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
                                fields.add(columnMetaDTO.getKey());
                            }
                        }
                    }
                }
                sourceMap.put("tableFields", fields);
                String arrayJson = JSON.toJSONString(list);
                columns = JSON.parseArray(arrayJson, ColumnDTO.class);
            } catch (Exception e) {
                throw new RdosDefineException(String.format("获取%s字段信息异常,Caused by: %s", tableName, e.getMessage()), e);
            }
        }
        String increColumn = readerParam.getIncreColumn();
        int index = -1;
        for (int i = 0; i < columns.size(); i++) {
            ColumnDTO column = columns.get(i);
            column.setName(column.getKey());
            if (increColumn.equals(column.getKey())) {
                index = i;
            }
        }
        rdbmsPollReader.setIncreColumn(increColumn);

        //回写settingMap
        param.getSettingMap().put("restoreColumnIndex", String.valueOf(index));
        param.getSettingMap().put("restoreColumnName", increColumn);
        rdbmsPollReader.setColumn(columns);
        return rdbmsPollReader;
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        try {
            RdbmsPollReaderParam param = JsonUtils.objectToObject(sourceMap, RdbmsPollReaderParam.class);
            return JsonUtils.objectToMap(param);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("getParserSourceMap error,Caused by: %s", e.getMessage()), e);
        }
    }

}
