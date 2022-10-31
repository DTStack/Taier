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

package com.dtstack.taier.datasource.plugin.es5;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.es5.consistent.ESEndPoint;
import com.dtstack.taier.datasource.plugin.es5.consistent.RequestType;
import com.dtstack.taier.datasource.plugin.es5.pool.ElasticSearchManager;
import com.dtstack.taier.datasource.plugin.es5.pool.ElasticSearchPool;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ESSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * es5 连接客户端
 *
 * @author ：wangchuan
 * date：Created in 下午3:04 2021/12/8
 * company: www.dtstack.com
 */
@Slf4j
public class EsClient extends AbsNoSqlClient {

    private static final int MAX_NUM = 10000;

    private static final String POST = "post";

    private static final String PUT = "put";

    private static final String DELETE = "delete";

    private static final String ENDPOINT_UPDATE_FORMAT = "/%s/_update";

    private static final String ENDPOINT_DELETE_FORMAT = "/%s";

    private static final String ENDPOINT_BULK_FORMAT = "/_bulk";

    private static final String ENDPOINT_UPDATE_QUERY_FORMAT = "/%s/_update_by_query";

    private static final String ENDPOINT_DELETE_QUERY_FORMAT = "/%s/_delete_by_query";

    private static final String RESULT_KEY = "result";

    private static final ElasticSearchManager ELASTIC_SEARCH_MANAGER = ElasticSearchManager.getInstance();

    public static final ThreadLocal<Boolean> IS_OPEN_POOL = new ThreadLocal<>();

    @Override
    public Boolean testCon(ISourceDTO iSource) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) iSource;
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return false;
        }
        RestClient client = null;
        try {
            client = getClient(esSourceDTO);
            client.performRequest(RequestType.GET, ESEndPoint.ENDPOINT_HEALTH_CHECK);
            return true;
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            closeResource(client, esSourceDTO);
        }
    }

    /**
     * 获取 es 某一索引下所有 type (也就是表)
     *
     * @param sourceDTO 数据源连接信息
     * @param queryDTO  查询条件
     * @return 所有的 type
     */
    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) sourceDTO;
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return new ArrayList<>();
        }
        RestClient client = getClient(esSourceDTO);
        List<String> typeList = Lists.newArrayList();
        //es索引
        String index = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : queryDTO.getTableName();
        //不指定index抛异常
        if (StringUtils.isBlank(index)) {
            throw new SourceException("The index of es is not specified, and the acquisition fails");
        }
        try {
            JSONObject resultJson = executeAndReturnJson(client, null, RequestType.GET, String.format(ESEndPoint.ENDPOINT_MAPPING_FORMAT, index));
            if (Objects.nonNull(resultJson)) {
                for (String key : resultJson.keySet()) {
                    JSONObject mappings = resultJson.getJSONObject(key).getJSONObject("mappings");
                    if (Objects.isNull(mappings)) {
                        continue;
                    }
                    typeList.addAll(Lists.newArrayList(mappings.keySet()));
                }
            }
        } catch (NullPointerException e) {
            log.error("index not exits", e);
        } catch (Exception e) {
            log.error(String.format("get type exception,%s", e.getMessage()), e);
        } finally {
            closeResource(client, esSourceDTO);
        }
        return SearchUtil.handleSearchAndLimit(typeList, queryDTO);
    }


    /**
     * 获取 es 所有 index (也就是数据库)
     *
     * @param sourceDTO 数据源连接信息
     * @param queryDTO  查询条件
     * @return 所有的 index
     */
    @Override
    public List<String> getAllDatabases(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) sourceDTO;
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return new ArrayList<>();
        }
        RestClient client = getClient(esSourceDTO);
        ArrayList<String> dbs = new ArrayList<>();
        try {
            JSONArray arrayResult = executeAndReturnArray(client, null, RequestType.GET, ESEndPoint.ENDPOINT_INDEX_GET);
            if (CollectionUtils.isNotEmpty(arrayResult)) {
                for (int i = 0; i < arrayResult.size(); i++) {
                    String index = arrayResult.getJSONObject(i).getString("index");
                    if (StringUtils.isNotBlank(index)) {
                        dbs.add(index);
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("Failed to get es index,%s", e.getMessage()), e);
        } finally {
            closeResource(client, esSourceDTO);
        }
        return dbs;
    }

    /**
     * es数据预览，默认 100 条，最大 10000 条
     *
     * @param sourceDTO 数据源连接信息
     * @param queryDTO  查询条件
     * @return 预览的数据
     */
    @Override
    public List<List<Object>> getPreview(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) sourceDTO;
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return new ArrayList<>();
        }
        RestClient client = getClient(esSourceDTO);
        //索引
        String index = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : queryDTO.getTableName();
        if (StringUtils.isBlank(index)) {
            throw new SourceException("The index of es is not specified，Data preview failed");
        }
        //限制条数，最大10000条
        int previewNum = queryDTO.getPreviewNum() > MAX_NUM ? MAX_NUM : queryDTO.getPreviewNum();
        JSONObject limitJson = new JSONObject();
        limitJson.put("size", previewNum);

        List<List<Object>> documentList = Lists.newArrayList();
        try {
            String endPoint;
            if (StringUtils.isNotBlank(queryDTO.getSchema()) && StringUtils.isNotBlank(queryDTO.getTableName())) {
                endPoint = String.format(ESEndPoint.ENDPOINT_SEARCH_TYPE_FORMAT, queryDTO.getSchema(), queryDTO.getTableName());
            } else {
                endPoint = String.format(ESEndPoint.ENDPOINT_SEARCH_FORMAT, index);
            }
            JSONObject jsonResult = executeAndReturnJson(client, buildEntity(limitJson), RequestType.GET, endPoint);
            // 结果集
            JSONArray hits = jsonResult.getJSONObject("hits").getJSONArray("hits");
            for (int i = 0; i < hits.size(); i++) {
                JSONObject hitJson = hits.getJSONObject(i);
                //一行数据
                List<Object> document = Lists.newArrayList();
                hitJson.keySet().forEach(key ->
                        document.add(new Pair<String, Object>(key, hitJson.get(key))));
                documentList.add(document);
            }
        } catch (Exception e) {
            log.error("doc acquisition exception", e);
        } finally {
            closeResource(client, esSourceDTO);
        }
        return documentList;
    }

    /**
     * 获取 es 字段信息
     *
     * @param sourceDTO 数据源连接信息
     * @param queryDTO  查询条件
     * @return es 字段信息
     */
    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) sourceDTO;
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return new ArrayList<>();
        }
        RestClient client = getClient(esSourceDTO);
        //索引
        String index = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : queryDTO.getTableName();
        if (StringUtils.isBlank(index)) {
            throw new SourceException("The index of es is not specified, and the field information fails to be obtained");
        }
        List<ColumnMetaDTO> columnMetaDTOS = new ArrayList<>();
        try {
            String endPoint;
            if (StringUtils.isNotBlank(queryDTO.getSchema()) && StringUtils.isNotBlank(queryDTO.getTableName())) {
                endPoint = String.format(ESEndPoint.ENDPOINT_MAPPING_TYPE_FORMAT, queryDTO.getSchema(), queryDTO.getTableName());
            } else {
                endPoint = String.format(ESEndPoint.ENDPOINT_MAPPING_FORMAT, index);
            }
            JSONObject resultJson = executeAndReturnJson(client, null, RequestType.GET, endPoint);
            if (Objects.nonNull(resultJson)) {
                for (String key : resultJson.keySet()) {
                    JSONObject mappings = resultJson.getJSONObject(key).getJSONObject("mappings");
                    if (Objects.isNull(mappings)) {
                        continue;
                    }
                    for (String type : mappings.keySet()) {
                        JSONObject properties = mappings.getJSONObject(type).getJSONObject("properties");
                        if (Objects.isNull(properties)) {
                            continue;
                        }
                        for (String column : properties.keySet()) {
                            ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                            columnMetaDTO.setKey(column);
                            columnMetaDTO.setType(properties.getJSONObject(column).getString("type"));
                            columnMetaDTOS.add(columnMetaDTO);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("doc acquisition exception", e);
        } finally {
            closeResource(client, esSourceDTO);
        }
        return columnMetaDTOS;
    }

    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) iSource;
        List<Map<String, Object>> list = Lists.newArrayList();
        HashMap<String, Object> map = Maps.newHashMap();
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return new ArrayList<>();
        }
        String dsl = doDealPageSql(queryDTO.getSql());
        //索引
        String index = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : queryDTO.getTableName();
        if (StringUtils.isBlank(index)) {
            throw new SourceException("The index of es is not specified, and the field information fails to be obtained. Please specify tableName as the index in sqlQueryDTO");
        }
        RestClient client = null;
        JSONObject resultJsonObject;
        try {
            client = getClient(esSourceDTO);
            Response response = client.performRequest(RequestType.GET, String.format(ESEndPoint.ENDPOINT_SEARCH_FORMAT, index));
            String result = EntityUtils.toString(response.getEntity());
            resultJsonObject = JSONObject.parseObject(result);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            closeResource(client, esSourceDTO);
        }
        map.put(RESULT_KEY, resultJsonObject);
        list.add(map);
        return list;
    }

    private static RestClient getClient(ESSourceDTO esSourceDTO) {
        boolean check = esSourceDTO.getPoolConfig() != null;
        IS_OPEN_POOL.set(check);
        if (!check) {
            return getClient(esSourceDTO.getUrl(), esSourceDTO.getUsername(), esSourceDTO.getPassword());
        }
        ElasticSearchPool elasticSearchPool = ELASTIC_SEARCH_MANAGER.getConnection(esSourceDTO);
        RestClient restHighLevelClient = elasticSearchPool.getResource();
        if (Objects.isNull(restHighLevelClient)) {
            throw new SourceException("No database connection available");
        }
        return restHighLevelClient;

    }

    /**
     * 根据地址、用户名和密码连接 es RestClient
     *
     * @param address  rest 地址
     * @param username 用户名
     * @param password 密码
     * @return es RestClient
     */
    private static RestClient getClient(String address, String username, String password) {
        log.info("Get ES data source connection, address : {}, userName : {}", address, username);
        List<HttpHost> httpHosts = dealHost(address);
        // 有用户名密码情况
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            return RestClient.builder(httpHosts.toArray(new HttpHost[0]))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();
        }
        // 无用户名密码
        return RestClient.builder(httpHosts.toArray(new HttpHost[0])).build();
    }

    private static List<HttpHost> dealHost(String address) {
        List<HttpHost> httpHostList = new ArrayList<>();
        String[] addr = address.split(",");
        for (String add : addr) {
            String[] pair = add.split(":");
            httpHostList.add(new HttpHost(pair[0], Integer.valueOf(pair[1]), "http"));
        }
        return httpHostList;
    }

    private void closeResource(RestClient restClient, ESSourceDTO esSourceDTO) {
        if (BooleanUtils.isFalse(IS_OPEN_POOL.get())) {
            //未开启线程池
            try {
                if (Objects.nonNull(restClient)) {
                    restClient.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            IS_OPEN_POOL.remove();
        } else {
            //开启连接池
            ElasticSearchPool elasticSearchPool = ELASTIC_SEARCH_MANAGER.getConnection(esSourceDTO);
            try {
                if (Objects.nonNull(restClient) && Objects.nonNull(elasticSearchPool)) {
                    elasticSearchPool.returnResource(restClient);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private String doDealPageSql(String dsl) {
        if (StringUtils.isNotBlank(dsl)) {
            // Feature.OrderedField 是为了保证格式化后 json 中的字段顺序不变
            JSONObject jsonObject = JSONObject.parseObject(dsl, Feature.OrderedField);
            return JSONObject.toJSONString(jsonObject, true);
        }
        return "";
    }

    /**
     * 执行增删改等操作
     *
     * @param sourceDTO 数据源连接信息
     * @param queryDTO  查询条件
     * @return 执行结果
     */
    @Override
    public Boolean executeSqlWithoutResultSet(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) sourceDTO;
        boolean result = false;
        if (esSourceDTO == null || StringUtils.isBlank(esSourceDTO.getUrl())) {
            return false;
        }
        //索引
        String index = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : queryDTO.getTableName();

        RestClient client = null;
        NStringEntity entity = null;
        try {
            client = getClient(esSourceDTO);
            if (queryDTO.getSql() != null) {
                entity = new NStringEntity(queryDTO.getSql(), ContentType.APPLICATION_JSON);
            }
            Integer esCommandType = queryDTO.getEsCommandType();
            String httpMethod = POST;
            String endpoint = index;
            switch (esCommandType) {
                case 0:
                case 1:
                    // PUT /${index}
                    httpMethod = PUT;
                    break;
                case 2:
                    // POST /${index}/_update
                    endpoint = String.format(ENDPOINT_UPDATE_FORMAT, index);
                    break;
                case 3:
                    // DELETE /${index}
                    httpMethod = DELETE;
                    log.info("delete es index, index : {}", index);
                    endpoint = String.format(ENDPOINT_DELETE_FORMAT, index);
                    break;
                case 4:
                    // POST /_bulk
                    endpoint = ENDPOINT_BULK_FORMAT;
                    break;
                case 5:
                    // POST /${index}/_update_by_query
                    endpoint = String.format(ENDPOINT_UPDATE_QUERY_FORMAT, index);
                    break;
                case 6:
                    // POST /${index}/_delete_by_query
                    endpoint = String.format(ENDPOINT_DELETE_QUERY_FORMAT, index);
                    break;
                default:
            }
            Response response = execute(client, entity, httpMethod, endpoint);
            if (response != null && (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)) {
                result = true;
            }
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            closeResource(client, esSourceDTO);
            if (entity != null) {
                entity.close();
            }
        }
        return result;
    }

    private Response execute(RestClient client, HttpEntity entity, String httpMethod, String endpoint) throws IOException {
        return client.performRequest(httpMethod, endpoint, Collections.emptyMap(), entity);
    }

    private JSONObject executeAndReturnJson(RestClient client, HttpEntity entity, String httpMethod, String endpoint) throws IOException {
        Response response = client.performRequest(httpMethod, endpoint, Collections.emptyMap(), entity);
        if (Objects.isNull(response)) {
            return new JSONObject();
        }
        String result = EntityUtils.toString(response.getEntity());
        if (StringUtils.isBlank(result)) {
            return new JSONObject();
        }
        return JSONObject.parseObject(result);
    }

    private JSONArray executeAndReturnArray(RestClient client, HttpEntity entity, String httpMethod, String endpoint) throws IOException {
        Response response = client.performRequest(httpMethod, endpoint, Collections.emptyMap(), entity);
        if (Objects.isNull(response)) {
            return new JSONArray();
        }
        String result = EntityUtils.toString(response.getEntity());
        if (StringUtils.isBlank(result)) {
            return new JSONArray();
        }
        return JSONArray.parseArray(result);
    }

    private HttpEntity buildEntity(JSONObject entityJson) {
        return buildEntity(entityJson.toJSONString());
    }

    private HttpEntity buildEntity(String entity) {
        return new NStringEntity(entity, ContentType.APPLICATION_JSON);
    }
}
