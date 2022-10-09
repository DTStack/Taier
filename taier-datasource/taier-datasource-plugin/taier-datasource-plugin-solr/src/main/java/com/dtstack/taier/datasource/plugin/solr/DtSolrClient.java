package com.dtstack.taier.datasource.plugin.solr;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.solr.pool.SolrManager;
import com.dtstack.taier.datasource.plugin.solr.pool.SolrPool;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SolrSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.Krb5HttpClientBuilder;
import org.apache.solr.client.solrj.impl.SolrHttpClientBuilder;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author ：qianyi
 * date：Created in 下午10:51 2021/04/27
 * company: www.dtstack.com
 */
@Slf4j
public class DtSolrClient extends AbsNoSqlClient {

    public static final ThreadLocal<Boolean> IS_OPEN_POOL = new ThreadLocal<>();

    private static final SolrManager solrManager = SolrManager.getInstance();

    private static final int MAX_NUM = 10000;

    @Override
    public Boolean testCon(ISourceDTO source) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) source;
        if (solrSourceDTO == null || StringUtils.isBlank(solrSourceDTO.getZkHost())) {
            throw new SourceException("zkHost can not be blank");
        }
        CloudSolrClient client = null;
        try {
            client = getClient(solrSourceDTO);
            //fix 开启kerberos client.connect() 判断连接无效
            CollectionAdminRequest.listCollections(client);
            return true;
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            closeResource(client, solrSourceDTO);
        }
    }

    private static CloudSolrClient getClient(SolrSourceDTO solrSourceDTO) {
        //开启kerberos 认证不走连接池
        boolean openPool = solrSourceDTO.getPoolConfig() != null && MapUtils.isEmpty(solrSourceDTO.getKerberosConfig());
        IS_OPEN_POOL.set(openPool);
        if (!openPool) {
            return getClient(solrSourceDTO.getZkHost(), solrSourceDTO.getChroot(), solrSourceDTO.getKerberosConfig());
        }

        SolrPool solrPool = solrManager.getConnection(solrSourceDTO);
        CloudSolrClient solrClient = solrPool.getResource();
        if (Objects.isNull(solrClient)) {
            throw new SourceException("No database connection available");
        }
        return solrClient;
    }


    private static CloudSolrClient getClient(String zkHost, String chroot, Map<String, Object> kerberosConfig) {
        if (StringUtils.isNotBlank(zkHost)) {
            //开启kerberos
            if (MapUtils.isNotEmpty(kerberosConfig)) {
                SolrUtils.initKerberosConfig(kerberosConfig);
                // 做一步特殊操作，用于刷新 Krb5HttpClientBuilder 类中的静态变量 jaasConfig，不然会有缓存
                Krb5HttpClientBuilder.regenerateJaasConfiguration();
                Krb5HttpClientBuilder krbBuild = new Krb5HttpClientBuilder();
                SolrHttpClientBuilder kb = krbBuild.getBuilder();
                HttpClientUtil.setHttpClientBuilder(kb);
            }
            Optional<String> chrootOptional = StringUtils.isNotBlank(chroot) ? Optional.of(chroot) : Optional.empty();
            return new CloudSolrClient.Builder(Collections.singletonList(zkHost), chrootOptional).build();
        }
        throw new SourceException("zkHost can't null");
    }


    private void closeResource(CloudSolrClient solrClient, SolrSourceDTO solrSourceDTO) {
        try {
            if (BooleanUtils.isFalse(IS_OPEN_POOL.get())) {
                //not open pool
                if (Objects.nonNull(solrClient)) {
                    solrClient.close();
                }
            } else {
                //open pool
                SolrPool solrPool = solrManager.getConnection(solrSourceDTO);
                if (Objects.nonNull(solrClient) && Objects.nonNull(solrPool)) {
                    solrPool.returnResource(solrClient);
                }
            }
            if(MapUtils.isNotEmpty(solrSourceDTO.getKerberosConfig())) {
                SolrUtils.destroyKerberosProperty();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IS_OPEN_POOL.remove();
        }

    }

    /**
     * collection 》》table
     *
     * @param source
     * @param queryDTO
     * @return
     */
    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) source;
        if (solrSourceDTO == null || StringUtils.isBlank(solrSourceDTO.getZkHost())) {
            return new ArrayList<>();
        }
        CloudSolrClient solrClient = getClient(solrSourceDTO);
        try {
            List<String> tableList = CollectionAdminRequest.listCollections(solrClient);
            return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
        } catch (Exception e) {
            throw new SourceException(String.format("get solr collections error, Cause by: %s", e.getMessage()), e);
        } finally {
            closeResource(solrClient, solrSourceDTO);
        }
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) iSource;
        if (solrSourceDTO == null || StringUtils.isBlank(solrSourceDTO.getZkHost())) {
            return new ArrayList<>();
        }
        //solr collection
        String collection = queryDTO.getTableName();
        if (StringUtils.isBlank(collection)) {
            throw new SourceException("The collection of solr is not specified，Data preview failed");
        }
        CloudSolrClient solrClient = getClient(solrSourceDTO);

        //设置条件
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.setStart(0);
        //限制条数，最大10000条
        int previewNum = queryDTO.getPreviewNum() > MAX_NUM ? MAX_NUM : queryDTO.getPreviewNum();
        solrQuery.setRows(previewNum);
        try {
            SolrDocumentList documents = solrClient.query(collection, solrQuery).getResults();
            List<List<Object>> documentList = Lists.newArrayList();
            for (SolrDocument doc : documents) {
                List<Object> document = Lists.newArrayList();
                Map<String, Object> sourceAsMap = doc.getFieldValueMap();
                sourceAsMap.keySet().forEach(key ->
                        document.add(new Pair<>(key, sourceAsMap.get(key))));
                documentList.add(document);
            }
            return documentList;
        } catch (Exception e) {
            throw new SourceException(String.format("Data preview exception, Cause by %s", e.getMessage()), e);
        } finally {
            closeResource(solrClient, solrSourceDTO);
        }
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) iSource;
        List<ColumnMetaDTO> columnMetaList = new ArrayList<>();
        if (solrSourceDTO == null || StringUtils.isBlank(solrSourceDTO.getZkHost())) {
            return new ArrayList<>();
        }
        //solr collection
        String collection = queryDTO.getTableName();
        if (StringUtils.isBlank(collection)) {
            throw new SourceException("The collection of solr is not specified，Data preview failed");
        }
        CloudSolrClient solrClient = getClient(solrSourceDTO);
        SchemaRequest.Fields fields = new SchemaRequest.Fields();
        SchemaResponse.FieldsResponse fieldsResponse = null;
        try {
            fieldsResponse = fields.process(solrClient, collection);
            List<Map<String, Object>> responseFields = fieldsResponse.getFields();
            for (Map<String, Object> map : responseFields) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                columnMetaDTO.setKey(MapUtils.getString(map, "name"));
                columnMetaDTO.setType(MapUtils.getString(map, "type"));
                columnMetaList.add(columnMetaDTO);
            }
            return columnMetaList;
        } catch (Exception e) {
            throw new SourceException(String.format("get solr columnMetaData error, Cause by: %s", e.getMessage()), e);
        } finally {
            closeResource(solrClient, solrSourceDTO);
        }
    }

    /**
     * 自定义查询
     * @param source
     * @param queryDTO
     * @return
     */
    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) source;
        if (solrSourceDTO == null || StringUtils.isBlank(solrSourceDTO.getZkHost())) {
            return new ArrayList<>();
        }
        //solr collection
        String collection = queryDTO.getTableName();
        if (StringUtils.isBlank(collection)) {
            throw new SourceException("The collection of solr is not specified，Data preview failed");
        }
        CloudSolrClient solrClient = getClient(solrSourceDTO);
        List<Map<String, Object>> executeResult = Lists.newArrayList();

        Map<String, String[]> queryParamMap = queryDTO.getSolrQueryDTO().getQueryParamMap();
        ModifiableSolrParams queryParams = new ModifiableSolrParams(queryParamMap);
        final QueryResponse response;
        try {
            response = solrClient.query(collection, queryParams);
            SolrDocumentList documents = response.getResults();
            Map<String, Object> map = new HashMap<>();
            //查询总数
            map.put("numFound", documents.getNumFound());
            map.put("start", documents.getStart());
            executeResult.add(map);
            for (SolrDocument document : documents) {
                Map<String, Object> documentMap = new HashMap<>();
                for (String key : document.keySet()) {
                    documentMap.put(key, document.getFirstValue(key));
                }
                executeResult.add(documentMap);
            }
        } catch (Exception e) {
            throw new SourceException(String.format("executeQuery exception,Cause by:%s", e.getMessage()), e);
        } finally {
            closeResource(solrClient, solrSourceDTO);
        }
        return executeResult;
    }
}
