package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.develop.vo.datasource.DsPreviewResultVO;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.regex.Pattern;

public abstract class AbsRdbmsDbBuilder implements DbBuilder {
    protected static final Integer LIMIT_COUNT = 100;

    private static final String CONN_MSG_FORMAT = "url:%s,username:%s";

    @Override
    public IClient getClient() {
        return ClientCache.getClient(getDataSourceType().getVal());
    }

    @Override
    public JSONObject pollPreview(String tableName, ISourceDTO sourceDTO) {
        List<String> columnList = new ArrayList<>();
        List<List<Object>> dataList = getClient().getPreview(sourceDTO, SqlQueryDTO.builder().tableName(tableName).previewNum(5).build());
        JSONObject preview = new JSONObject(2);
        preview.put("columnList", columnList);
        if (CollectionUtils.isNotEmpty(dataList)) {
            preview.put("columnList", dataList.get(0));
            if (dataList.size() > 1) {
                List<List<Object>> subList = dataList.subList(1, dataList.size());
                preview.put("dataList", transformData(subList));
            }
        }
        return preview;
    }

    private List<List<String>> transformData(List<List<Object>> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        List<List<String>> result = new ArrayList<>();
        dataList.forEach(data -> {
            List<String> list = new ArrayList<>();
            for (Object obj : data) {
                list.add(Objects.isNull(obj) ? null : obj.toString());
            }
            result.add(list);
        });
        return result;
    }

    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        return getClient().getAllDatabases(sourceDTO, SqlQueryDTO.builder().build());
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = new ArrayList<>();
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tableName).build();
        List<ColumnMetaDTO> columnMetaDTOList = getClient().getColumnMetaData(sourceDTO, sqlQueryDTO);
        if (CollectionUtils.isNotEmpty(columnMetaDTOList)) {
            for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                columns.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
            }
        }
        return columns;
    }

    public List<JSONObject> getByColumn(List<JSONObject> columns, Pattern pollColumn) {
        List<JSONObject> res = new ArrayList<>();
        //指定类型返回
        for (JSONObject column : columns) {
            String type = column.getString("type");
            if (pollColumn.matcher(type).matches()) {
                res.add(column);
            }
        }
        return res;
    }

    @NotNull
    public List<String> getSchemaList(List<String> allDatabases, Pattern pattern) {
        Set<String> result = new HashSet<>();
        if (CollectionUtils.isNotEmpty(allDatabases)) {
            allDatabases.stream().filter(schema -> !pattern.matcher(schema).find()).sorted().forEach(schema -> {
                result.add(schema);
            });
        }
        List<String> resultList = new ArrayList<>(result);
        resultList.sort(String::compareTo);
        return resultList;
    }

    public List<String> listTablesBySchema(String schema, String tableNamePattern,ISourceDTO sourceDTO, String db) {
        return getClient().getTableListBySchema(sourceDTO, SqlQueryDTO.builder().schema(schema).tableNamePattern(tableNamePattern).limit(LIMIT_COUNT).build());
    }

    @Override
    public String buildConnMsgForSA(JSONObject dataJson) {
        return String.format(CONN_MSG_FORMAT, dataJson.getString("jdbcUrl"), dataJson.getString("username"));
    }
}
