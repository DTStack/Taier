package com.dtstack.taier.datasource.plugin.dorisrestful.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.dto.source.DorisRestfulSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.datasource.plugin.common.utils.CommonUtil;
import com.dtstack.taier.datasource.plugin.restful.core.http.HttpClient;
import com.dtstack.taier.datasource.plugin.restful.core.http.HttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DorisRestfulClient implements Closeable {

    private static final String DEFAULT_CLUSTER = "default_cluster";

    private static final String COLUMN_META_JSON_PATH = "$.data.%s.schema";

    private static final String RESULT_JSON_PATH = "$.data";

    private static final String DATA_JSON_PATH = "$.data.data";

    private static final String META_JSON_PATH = "$.data.meta";

    private static final Integer PREVIEW_SIZE = 100;

    private static final String PREVIEW_SQL = "{stmt: \"select * from %s limit %s;\"}";

    private static final String EXECUTE_SQL = "{\"stmt\": \"%s;\"}";

    private static final String TABLE_STATUS_SQL = "show table status";

    @Override
    public void close() {
    }

    public Boolean login(DorisRestfulSourceDTO sourceDTO) {
        getAllDatabases(sourceDTO);
        return true;
    }

    public List<ColumnMetaDTO> getColumnMetaData(DorisRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        String schema = StringUtils.isEmpty(sourceDTO.getSchema()) ? sqlQueryDTO.getSchema() : sourceDTO.getSchema();
        String tableName = sqlQueryDTO.getTableName();

        AssertUtils.notBlank(schema, "schema not null");
        AssertUtils.notBlank(tableName, "tableName not null");

        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.COLUMN_METADATA, cluster, schema, tableName));
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            Response result = httpClient.get(null, null, null);
            AssertUtils.isTrue(result, 0);
            JSONArray jsonArray = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), String.format(COLUMN_META_JSON_PATH, tableName));
            List<ColumnMetaDTO> list = new ArrayList<>();
            for (Object object : jsonArray) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                JSONObject obj = (JSONObject) object;
                columnMetaDTO.setKey(MapUtils.getString(obj, "Field"));
                columnMetaDTO.setType(MapUtils.getString(obj, "Type"));
                columnMetaDTO.setComment(MapUtils.getString(obj, "Extra"));
                list.add(columnMetaDTO);
            }
            return list;
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    /**
     * 获取所有的库名
     *
     * @param sourceDTO
     * @return
     */
    public List<String> getAllDatabases(DorisRestfulSourceDTO sourceDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.ALL_DATABASE, cluster));
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            Response result = httpClient.get(null, null, null);
            AssertUtils.isTrue(result, 0);
            JSONArray jsonArray = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), RESULT_JSON_PATH);
            //查询的数据库格式是 cluster:database
            return jsonArray.stream().map(obj -> ((String) obj).substring(((String) obj).lastIndexOf(":") + 1)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }


    public List<String> getTableListBySchema(DorisRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        String schema = StringUtils.isEmpty(sourceDTO.getSchema()) ? sqlQueryDTO.getSchema() : sourceDTO.getSchema();
        AssertUtils.notBlank(schema, "schema not null");

        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.ALL_TABLES, cluster, cluster, schema));
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            Response result = httpClient.get(null, null, null);
            AssertUtils.isTrue(result, 0);
            JSONArray jsonArray = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), RESULT_JSON_PATH);
            return jsonArray.toJavaList(String.class);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }


    public List<List<Object>> getPreview(DorisRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        String schema = StringUtils.isEmpty(sourceDTO.getSchema()) ? sqlQueryDTO.getSchema() : sourceDTO.getSchema();
        String tableName = sqlQueryDTO.getTableName();
        AssertUtils.notBlank(schema, "schema not null");
        AssertUtils.notBlank(tableName, "tableName not null");

        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.QUERY_DATA, cluster, schema));
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            Integer limit = sqlQueryDTO.getLimit() != null ? sqlQueryDTO.getLimit() : PREVIEW_SIZE;
            String body = String.format(PREVIEW_SQL, tableName, limit);
            Response result = httpClient.post(body, null, null);
            AssertUtils.isTrue(result, 0);

            JSONArray data = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), DATA_JSON_PATH);
            JSONArray metaObj = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), META_JSON_PATH);
            List<String> meta = metaObj.stream().map(obj -> ((JSONObject) obj).getString("name")).collect(Collectors.toList());
            List<List<Object>> resultList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                List<Object> line = new ArrayList<>();
                JSONArray jsonObject = data.getJSONArray(i);
                for (int j = 0; j < meta.size(); j++) {
                    line.add(new Pair<String, Object>(meta.get(j), jsonObject.get(j)));
                }
                resultList.add(line);
            }
            return resultList;
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }


    public List<Map<String, Object>> executeQuery(DorisRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        String schema = StringUtils.isEmpty(sourceDTO.getSchema()) ? sqlQueryDTO.getSchema() : sourceDTO.getSchema();
        AssertUtils.notBlank(schema, "schema not null");
        AssertUtils.notBlank(sqlQueryDTO.getSql(), "sql not null");

        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.QUERY_DATA, cluster, schema));
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {

            JSONObject bodyObject = new JSONObject();
            bodyObject.put("stmt", sqlQueryDTO.getSql());
            Response result = httpClient.post(bodyObject.toJSONString(), null, null);
            AssertUtils.isTrue(result, 0);

            JSONArray data = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), DATA_JSON_PATH);
            JSONArray metaObj = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), META_JSON_PATH);
            if (null == metaObj) {
                return new ArrayList<>(0);
            }
            List<String> meta = metaObj.stream().map(obj -> ((JSONObject) obj).getString("name")).collect(Collectors.toList());
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> line = new HashMap<>();
                JSONArray jsonObject = data.getJSONArray(i);
                for (int j = 0; j < meta.size(); j++) {
                    line.put(meta.get(j), jsonObject.get(j));
                }
                resultList.add(line);
            }
            return resultList;
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }


    public Boolean executeSqlWithoutResultSet(DorisRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        String schema = StringUtils.isEmpty(sourceDTO.getSchema()) ? sqlQueryDTO.getSchema() : sourceDTO.getSchema();
        AssertUtils.notBlank(schema, "schema not null");
        AssertUtils.notBlank(sqlQueryDTO.getSql(), "sql not null");

        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.QUERY_DATA, cluster, schema));
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {

            JSONObject bodyObject = new JSONObject();
            bodyObject.put("stmt", sqlQueryDTO.getSql());
            Response result = httpClient.post(bodyObject.toJSONString(), null, null);
            AssertUtils.isTrue(result, 0);
            return true;
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }


    public String getTableMetaComment(DorisRestfulSourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        String schema = StringUtils.isEmpty(sourceDTO.getSchema()) ? sqlQueryDTO.getSchema() : sourceDTO.getSchema();
        String tableName = sqlQueryDTO.getTableName();
        AssertUtils.notBlank(schema, "schema not null");
        AssertUtils.notBlank(tableName, "tableName not null");
        sourceDTO.setUrl(sourceDTO.getUrl() + String.format(HttpAPI.QUERY_DATA, cluster, schema));
        try {
            HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO);
            JSONObject bodyObject = new JSONObject();
            bodyObject.put("stmt", TABLE_STATUS_SQL);
            Response result = httpClient.post(bodyObject.toJSONString(), null, null);
            AssertUtils.isTrue(result, 0);

            JSONArray data = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), DATA_JSON_PATH);
            JSONArray metaObj = (JSONArray) JSONPath.eval(JSONObject.parse(result.getContent()), META_JSON_PATH);
            List<String> meta = metaObj.stream().map(obj -> ((JSONObject) obj).getString("name")).collect(Collectors.toList());

            int tableNamePosition = 0;
            int commentPosition = 0;
            for (int i = 0; i < meta.size(); i++) {
                if ("Comment".equals(meta.get(i))) {
                    commentPosition = i;
                }
                if ("Name".equals(meta.get(i))) {
                    tableNamePosition = i;
                }
            }

            for (int i = 0; i < data.size(); i++) {
                if ((data.getJSONArray(i).get(tableNamePosition)).equals(sqlQueryDTO.getTableName())) {
                    return (String) data.getJSONArray(i).get(commentPosition);
                }
            }
        } catch (Exception e) {
            log.error("get Table Comment error,{}", e.getMessage(), e);
        }
        return "";
    }

    public Boolean isTableExistsInDatabase(DorisRestfulSourceDTO sourceDTO, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName)) {
            throw new SourceException("schema || table not null");
        }

        String cluster = StringUtils.isEmpty(sourceDTO.getCluster()) ? DEFAULT_CLUSTER : sourceDTO.getCluster();
        sourceDTO.setUrl(sourceDTO.getUrl() +
                String.format(HttpAPI.QUERY_TABLE_STRUCTURE, cluster, cluster, dbName, tableName));

        HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO);
        Response response = httpClient.get(null, null, null);

        //code为1表示数据库不存在或表不存在,code为0表示表存在于指定db
        return CommonUtil.codeIsEqual(response, 0);
    }
}
