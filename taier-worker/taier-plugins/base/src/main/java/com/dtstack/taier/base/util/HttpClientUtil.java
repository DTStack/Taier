package com.dtstack.taier.base.util;


import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class HttpClientUtil {

    private static Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);


    private static int socketTimeout = 5000;
    private static int connectTimeout = 5000;
    private static int maxTotal = 100;
    private static int maxPerRoute = 20;

    private static int DEFAULT_RETRY_TIMES = 3;
    private static int SLEEP_TIME_MILLI_SECOND = 2000;
    private static long HTTPCLIENT_CACHE_TTL_MILL_SECONDS = 60000;
    private static HttpClient simpleHttpClient = buildHttpClient(new BaseConfig());
    private static Cache<String, HttpClient> httpClientCache = CacheBuilder.newBuilder()
            .removalListener(new HttpClientRemovalListener())
            .expireAfterAccess(HTTPCLIENT_CACHE_TTL_MILL_SECONDS, TimeUnit.MINUTES)
            .build();

    private static Charset charset = Charset.forName("UTF-8");
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static String get(String url) {
        return get(url, new Header[0]);
    }

    public static String get(String url, int retryTimes) {
        return get(url, new Header[0], retryTimes);
    }

    public static String get(String url, Header[] headers) {
        return get(url, new Header[0], DEFAULT_RETRY_TIMES);
    }

    public static String get(String url, Header[] headers, int retryTimes) {
        try {
            return (String) RetryUtil.executeWithRetry(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return executeGet(url, headers);
                }
            }, retryTimes, (long) SLEEP_TIME_MILLI_SECOND, false);
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
    }

    public static String executeGet(String url, Header[] headers) {
        String respBody = null;
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        int statusCode = 0;

        try {
            httpGet = new HttpGet(url);
            if (headers != null && headers.length > 0) {
                httpGet.setHeaders(headers);
            }

            CloseableHttpClient httpClient = (CloseableHttpClient) getHttpClient();
            response = httpClient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();

            Preconditions.checkState(statusCode == 200);

            HttpEntity entity = response.getEntity();
            respBody = EntityUtils.toString(entity, charset.toString());
        } catch (Exception e) {
            String errorMsg = String.format("Request URL: %s, Response Code: %s", url, statusCode);
            throw new PluginDefineException(errorMsg, e);
        } finally {
            if (200 != statusCode && null != httpGet) {
                httpGet.abort();
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    LOG.error("Get Close Response Error:", e);
                }
            }

        }
        return respBody;
    }

    public static String post(String url) {
        return post(url, null);
    }

    public static String post(String url, Map<String, Object> bodyData) {
        return post(url, bodyData, 1);
    }

    public static String post(String url, Map<String, Object> bodyData, int retryTimes) {
        try {
            return (String) RetryUtil.executeWithRetry(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return executePost(url, bodyData, null, Boolean.FALSE);
                }
            }, retryTimes, (long) SLEEP_TIME_MILLI_SECOND, false);
        } catch (Exception e) {
            throw new PluginDefineException(e);
        }
    }

    public static String executePost(String url, Map<String, Object> bodyData, Map<String, Object> cookies, Boolean isRedirect) {
        String responseBody = null;
        int statusCode = 0;
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            CloseableHttpClient httpClient = (CloseableHttpClient) getHttpClient();
            if (cookies != null && cookies.size() > 0) {
                httpPost.addHeader("Cookie", getCookieFormat(cookies));
            }

            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            if (bodyData != null && bodyData.size() > 0) {
                httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(bodyData), charset));
            }

            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity, charset);
            } else {
                Preconditions.checkState(statusCode == 307);

                Header header = response.getFirstHeader("location");
                String newuri = header.getValue();
                HttpPost newHttpPost = new HttpPost(newuri);
                newHttpPost.setHeader("Content-type", "application/json;charset=UTF-8");
                if (bodyData != null && bodyData.size() > 0) {
                    newHttpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(bodyData), charset));
                }

                response = httpClient.execute(newHttpPost);
                statusCode = response.getStatusLine().getStatusCode();

                Preconditions.checkState(statusCode == 200);
                responseBody = EntityUtils.toString(response.getEntity(), charset);
            }

        } catch (Exception e) {
            String errorMsg = String.format("Request URL: %s, Response Code: %s", url, statusCode);
            throw new PluginDefineException(errorMsg, e);
        } finally {
            if (200 != statusCode && null != httpPost) {
                httpPost.abort();
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Exception var19) {
                    LOG.error("", var19);
                }
            }
        }

        return responseBody;
    }


    private static RequestConfig getRequestConfig() {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .build();

        return requestConfig;
    }


    private static PoolingHttpClientConnectionManager getConnectionManager() {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        return cm;
    }


    private static HttpClient buildHttpClient(BaseConfig config) {
        HttpClientBuilder custom = HttpClients.custom();

        RequestConfig requestConfig = getRequestConfig();
        custom.setDefaultRequestConfig(requestConfig);


        PoolingHttpClientConnectionManager cm = getConnectionManager();
        custom.setConnectionManager(cm);

        if (config.isOpenKerberos()) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new KerberosCredentials(null));
            custom.setDefaultCredentialsProvider(credsProvider);
        }

        return custom.build();
    }

    private static String getCookieFormat(Map<String, Object> cookies) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Object>> sets = cookies.entrySet();
        Iterator var3 = sets.iterator();

        while (var3.hasNext()) {
            Map.Entry<String, Object> s = (Map.Entry) var3.next();
            sb.append((String) s.getKey()).append("=").append(s.getValue().toString()).append(";");
        }

        return sb.toString();
    }


    private static HttpClient getHttpClient() {
        String cacheKey = getClientCacheKey();
        HttpClient httpClient = httpClientCache.getIfPresent(cacheKey);
        if (httpClient == null) {
            httpClient = simpleHttpClient;
        }
        return httpClient;
    }

    private static String getClientCacheKey() {
        return Thread.currentThread().getId() + "_" + Thread.currentThread().getName();
    }


    public static HttpClientHelperBuilder builder() {

        return new HttpClientHelperBuilder();
    }


    public static class HttpClientHelper {

        public HttpClientHelper() {
        }

        public String get(String url) {
            return get(url, DEFAULT_RETRY_TIMES);
        }

        public String get(String url, int retryTimes) {
            return get(url, new Header[0], retryTimes);
        }

        public String get(String url, Header[] headers) {
            return get(url, headers, DEFAULT_RETRY_TIMES);
        }

        public String get(String url, Header[] headers, int retryTimes) {
            return HttpClientUtil.get(url, headers, retryTimes);
        }

        public String post(String url) {
            return post(url, null);
        }

        public String post(String url, Map<String, Object> bodyData) {
            return post(url, bodyData, 1);
        }

        public String post(String url, Map<String, Object> bodyData, int retryTimes) {
            return HttpClientUtil.post(url, bodyData, retryTimes);
        }
    }

    public static class HttpClientHelperBuilder {

        private BaseConfig config;

        public HttpClientHelperBuilder() {
        }

        public HttpClientHelperBuilder setBaseConfig(BaseConfig config) {
            this.config = config;
            return this;
        }

        public HttpClientHelper build() {

            String cacheKey = getClientCacheKey();
            synchronized (cacheKey.intern()) {
                try {
                    httpClientCache.get(cacheKey, () -> {
                        return buildHttpClient(config);
                    });
                } catch (Exception e) {
                    throw new PluginDefineException(e);
                }
            }
            return new HttpClientHelper();
        }
    }

    private static class HttpClientRemovalListener implements RemovalListener<String, HttpClient> {

        @Override
        public void onRemoval(RemovalNotification<String, HttpClient> notification) {
            if (notification.getValue() != null) {
                try {
                    CloseableHttpClient httpClient = (CloseableHttpClient) notification.getValue();

                    String cacheKey = getClientCacheKey();

                    // 根据cacheKey 判断改线程是否存在
                    if (threadExists(cacheKey)) {
                        httpClientCache.put(cacheKey, httpClient);
                    } else {
                        httpClient.close();
                    }

                } catch (Exception ex) {
                    LOG.info("HttpClient Close Rrror: ", ex);
                }
            }
        }

        private boolean threadExists(String cacheKey) {
            return (Thread.currentThread().getId() + "_" + Thread.currentThread().getName()) == cacheKey;
        }
    }


    public static void main(String[] args) {


        // 1. 普通访问方式
        HttpClientUtil.get("http://baidu.coms");
        HttpClientUtil.post("http://baidu.com");


        // 2. 如果目标url有开启ssl、kerberos或者其它安全认证的可能
        HttpClientUtil.builder()
                .setBaseConfig(null)
                .build()
                .get("http://baidu.com");
    }
}



