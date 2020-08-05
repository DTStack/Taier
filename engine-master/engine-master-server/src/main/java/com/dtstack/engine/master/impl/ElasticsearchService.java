package com.dtstack.engine.master.impl;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 2020/7/25
 * Company: www.dtstack.com
 * @author maqi
 */
@Component
public class ElasticsearchService implements InitializingBean, DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchService.class);


    private static final String ES_LOG_TIMESTAMP_KEY = "timestamp";
    private static final String ES_LOG_COMPONENT_KEY = "component";
    private static final String ES_LOG_MESSAGE_KEY = "logInfo";

    private List<HttpHost> httpHosts;
    private String username;
    private String password;
    private String index;
    private Integer fetchSize;
    private Long keepAlive;

    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EnvironmentContext environmentContext;

    @Override
    public void afterPropertiesSet() {
        LOG.info("Initializing " + this.getClass().getName());

        this.httpHosts = parseHostsString(environmentContext.getElasticsearchAddress());
        this.username = environmentContext.getElasticsearchUsername();
        this.password = environmentContext.getElasticsearchPassword();
        this.index = environmentContext.getElasticsearchIndex();
        this.fetchSize = Integer.valueOf(environmentContext.getElasticsearchFetchSize());
        this.keepAlive = Long.valueOf(environmentContext.getElasticsearchKeepAlive());

        if (httpHosts.size() > 0) {
            RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()]));

            if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                builder = RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()]))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }
            restHighLevelClient = new RestHighLevelClient(builder);
        }
    }

    /**
     *  TODO  batchReader
     * @param fileName
     * @param jobId
     * @return
     */
    public String searchWithJobId(String fileName, String jobId) {
        Map<String, StringBuilder> completeLogs = Maps.newTreeMap();
        StringBuilder strBuilder = new StringBuilder();
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termsQuery(fileName, jobId));

            SearchRequest searchRequest = new SearchRequest(index);
            Scroll scroll = new Scroll(TimeValue.timeValueMinutes(keepAlive));
            searchRequest.scroll(scroll);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.sort(ES_LOG_TIMESTAMP_KEY, SortOrder.ASC);
            searchSourceBuilder.size(fetchSize);
            searchSourceBuilder.query(boolQueryBuilder);

            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            // first request data
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            buildLogContent(completeLogs, searchHits);


            String scrollId = searchResponse.getScrollId();
            while (searchHits != null && searchHits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.searchScroll(scrollRequest);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();

                buildLogContent(completeLogs, searchHits);
            }

            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest);
            boolean succeeded = clearScrollResponse.isSucceeded();


            for (Map.Entry<String, StringBuilder> entry : completeLogs.entrySet()) {
                strBuilder.append("###############" + entry.getKey() + "###############\n");
                strBuilder.append(entry.getValue());
                strBuilder.append("###################################################\n");
            }

            LOG.info("clearScrollResponse succeeded :{},scrollId:{}", succeeded, scrollId);
        } catch (Exception e) {
            throw new RdosDefineException("searchRequest error !" + ExceptionUtils.getStackTrace(e));
        }

        return strBuilder.toString();
    }

    private void buildLogContent(Map<String, StringBuilder> completeLogs, SearchHit[] searchHits) {
        for (SearchHit hit : searchHits) {
            Pair<String, String> fileNameAndContent = parseContent(hit.getSourceAsString());
            String name = fileNameAndContent.getLeft();
            String content = fileNameAndContent.getRight();
            StringBuilder stringBuilder = completeLogs.computeIfAbsent(name, (n) -> new StringBuilder());
            stringBuilder.append(content);
        }
    }

    /**
     * 文件内容及所属文件名称
     * @param sourceString
     * @return
     */
    public Pair<String, String> parseContent(String sourceString) {
        JSONObject jsonObject = JSONObject.parseObject(sourceString);
        String component = (String) jsonObject.get(ES_LOG_COMPONENT_KEY);
        String messages = (String) jsonObject.get(ES_LOG_MESSAGE_KEY);
        Pair<String, String> pair = new MutablePair<>(component, messages);
        return pair;
    }


    /**
     * Parse Hosts String to list. format:  http://host_name:9092;http://host_name:9093
     */
    public static List<HttpHost> parseHostsString(String hostsStr) {
        final List<HttpHost> hostList = new ArrayList<>();
        final String validationExceptionMessage = "'elasticsearch address format should " +
                "follow the format 'http://host_name:port', but is '" + hostsStr + "'.";

        if (StringUtils.isEmpty(hostsStr)) {
            LOG.warn("No set elasticsearch host!");
            return hostList;
        }

        final String[] hosts = hostsStr.split(";");
        for (String host : hosts) {
            try {
                final URL url = new URL(host);
                final String protocol = url.getProtocol();
                final String hostName = url.getHost();
                final int hostPort = url.getPort();

                if (StringUtils.isBlank(protocol) || StringUtils.isBlank(hostName) || -1 == hostPort) {
                    LOG.error(validationExceptionMessage);
                }

                hostList.add(new HttpHost(hostName, hostPort, protocol));
            } catch (MalformedURLException e) {
                LOG.error(validationExceptionMessage, e);
            }
        }
        return hostList;
    }


    @Override
    public void destroy() {
        try {
            LOG.info("Closing elasticSearch client");
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (Exception e) {
            LOG.error("Error closing ElasticSearch client: ", e);
        }
    }
}
