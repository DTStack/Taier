package com.dtstack.taier.datasource.plugin.restful.core.http;

import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.DtClassThreadFactory;
import com.dtstack.taier.datasource.plugin.common.utils.MD5Util;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SSLUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpClientFactory {

    /**
     * http IO 线程数，默认一个，后面如果需要支持池化 再做修改
     */
    private static final Integer IO_THREAD_COUNT = 3;

    /**
     * HTTP连接超时时间，单位：秒
     */
    private static final Integer HTTP_CONNECT_TIMEOUT = 90;

    /**
     * Socket 超时时间，单位：秒
     */
    private static final Integer HTTP_SOCKET_TIMEOUT = 90;

    /**
     * 获取 HTTP 连接超时时间，单位：秒
     */
    private static final Integer HTTP_CONNECTION_REQUEST_TIMEOUT = 90;

    private static final ConcurrentHashMap<String, HttpClient> HTTP_CLIENT_CACHE = new ConcurrentHashMap<>();

    public static HttpClient createHttpClientAndStart(ISourceDTO sourceDTO) {
        RestfulSourceDTO restfulSourceDTO = (RestfulSourceDTO) sourceDTO;
        Boolean useCache = ReflectUtil.getFieldValueNotThrow(Boolean.class, restfulSourceDTO, "useCache", Boolean.FALSE, Boolean.FALSE);
        HttpClient httpClient;
        if (useCache != null && useCache) {
            String key = MD5Util.getMd5String(restfulSourceDTO.getUrl());
            httpClient = HTTP_CLIENT_CACHE.get(key);
            if (httpClient == null) {
                synchronized (HttpClientFactory.class) {
                    httpClient = HTTP_CLIENT_CACHE.get(key);
                    if (httpClient == null) {
                        httpClient = createHttpClient(sourceDTO);
                        httpClient.start();
                        HTTP_CLIENT_CACHE.put(key, httpClient);
                    }
                }
            }
        } else {
            httpClient = createHttpClient(sourceDTO);
            httpClient.start();
        }
        return httpClient;
    }

    public static HttpClient createHttpClient(ISourceDTO sourceDTO) {
        RestfulSourceDTO restfulSourceDTO = (RestfulSourceDTO) sourceDTO;
        // 创建 ConnectingIOReactor
        ConnectingIOReactor ioReactor = initIOReactorConfig();
        SSLConfig sslConfig = restfulSourceDTO.getSslConfig();
        SSLIOSessionStrategy sslIS;
        if (Objects.isNull(sslConfig) || BooleanUtils.isTrue(MapUtils.getBoolean(sslConfig.getOtherConfig(), "skipSsl", false))) {
            // 跳过 ssl 认证
            sslIS = getDefaultSSLConnectionSocketFactory();
        } else {
            SSLContext sslContext = getSSLContext(restfulSourceDTO);
            sslIS = sslContext == null ?
                    SSLIOSessionStrategy.getDefaultStrategy() :
                    new SSLIOSessionStrategy(sslContext, (hostname, session) -> true);
        }

        // 支持 http、https
        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry =
                RegistryBuilder.<SchemeIOSessionStrategy>create()
                        .register("http", NoopIOSessionStrategy.INSTANCE)
                        .register("https", sslIS)
                        .build();
        // 创建链接管理器
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor, sessionStrategyRegistry);

        // 创建HttpAsyncClient
        CloseableHttpAsyncClient httpAsyncClient = createPoolingHttpClient(cm, restfulSourceDTO.getConnectTimeout(), restfulSourceDTO.getSocketTimeout(), restfulSourceDTO.getKerberosConfig());

        // 启动定时调度
        ScheduledExecutorService clearConnService = initFixedCycleCloseConnection(cm);

        // 组合生产HttpClientImpl
        return buildHttpClient(restfulSourceDTO, httpAsyncClient, clearConnService);
    }

    /**
     * 跳过 ssl 认证
     *
     * @return {@link SSLIOSessionStrategy}
     */
    public static SSLIOSessionStrategy getDefaultSSLConnectionSocketFactory() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return new SSLIOSessionStrategy(sc, (hostname, session) -> true);
        } catch (Exception e) {
            throw new SourceException("skip ssl error.", e);
        }
    }

    /**
     * 获取 http sslContext
     *
     * @param sourceDTO 数据源信息
     * @return SSLContext
     */
    public static SSLContext getSSLContext(ISourceDTO sourceDTO) {
        SSLUtil.SSLConfiguration sslConfiguration = SSLUtil.getSSLConfiguration(sourceDTO);
        if (Objects.isNull(sslConfiguration)) {
            return null;
        }
        try {
            InputStream in = new FileInputStream(sslConfiguration.getTrustStorePath());
            KeyStore trustStore = KeyStore.getInstance(StringUtils.isNotBlank(sslConfiguration.getTrustStoreType()) ?
                    sslConfiguration.getTrustStoreType() : KeyStore.getDefaultType());
            try {
                trustStore.load(in, StringUtils.isNotBlank(sslConfiguration.getTrustStorePassword()) ?
                        sslConfiguration.getTrustStorePassword().toCharArray() : null);
            } finally {
                in.close();
            }
            return SSLContexts
                    .custom()
                    .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())  //加载本地信任证书
                    .build();
        } catch (Exception e) {
            log.info("get sslContext failed.", e);
            return null;
        }
    }


    /**
     * 初始化 http 请求配置
     *
     * @param connectTimeout 连接超时时间
     * @param socketTimeout  socket 超时时间
     * @return http 请求配置
     */
    private static RequestConfig initRequestConfig(Integer connectTimeout, Integer socketTimeout) {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        // ConnectTimeout:连接超时.连接建立时间，三次握手完成时间.
        Integer connTimeout = Objects.isNull(connectTimeout) ? HTTP_CONNECT_TIMEOUT : connectTimeout;
        Integer sockTimeout = Objects.isNull(socketTimeout) ? HTTP_SOCKET_TIMEOUT : socketTimeout;
        requestConfigBuilder.setConnectTimeout(connTimeout * 1000);
        // SocketTimeout:Socket请求超时.数据传输过程中数据包之间间隔的最大时间.
        requestConfigBuilder.setSocketTimeout(sockTimeout * 1000);
        // ConnectionRequestTimeout:httpclient使用连接池来管理连接，这个时间就是从连接池获取连接的超时时间，可以想象下数据库连接池
        requestConfigBuilder.setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT * 1000);
        return requestConfigBuilder.build();
    }

    /**
     * 初始化线程数
     *
     * @return ConnectingIOReactor
     */
    private static ConnectingIOReactor initIOReactorConfig() {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(IO_THREAD_COUNT).build();
        ConnectingIOReactor ioReactor;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            return ioReactor;
        } catch (IOReactorException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    /**
     * 初始化周期调度线程池去关闭无用连接
     *
     * @param cm connection 管理器
     * @return 周期调度线程池
     */
    private static ScheduledExecutorService initFixedCycleCloseConnection(final PoolingNHttpClientConnectionManager cm) {
        // 定时关闭所有空闲链接
        ScheduledExecutorService connectionGcService = Executors.newSingleThreadScheduledExecutor(new DtClassThreadFactory("Loader-close-connection"));
        connectionGcService.scheduleAtFixedRate(() -> {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Close idle connections, fixed cycle operation");
                }
                cm.closeIdleConnections(3, TimeUnit.MINUTES);
            } catch (Exception ex) {
                log.error("", ex);
            }
        }, 30, 30, TimeUnit.SECONDS);
        return connectionGcService;
    }

    /**
     * 创建异步 http client
     *
     * @param cm             http connection 管理器
     * @param connectTimeout 连接超时时间
     * @param socketTimeout  socket 超时时间
     * @param kerberosConfig kerberos 配置
     * @return 异步 http client
     */
    private static CloseableHttpAsyncClient createPoolingHttpClient(PoolingNHttpClientConnectionManager cm, Integer connectTimeout, Integer socketTimeout, Map<String, Object> kerberosConfig) {

        if (MapUtils.isNotEmpty(kerberosConfig)) {
            return KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                    (PrivilegedAction<CloseableHttpAsyncClient>) () -> {
                        RequestConfig requestConfig = initRequestConfig(connectTimeout, socketTimeout);
                        HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClients.custom();

                        // 设置连接管理器
                        httpAsyncClientBuilder.setConnectionManager(cm);

                        // 设置RequestConfig
                        if (requestConfig != null) {
                            httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig);
                        }

                        // 设置 kerberos 认证
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY, new KerberosCredentials(null));
                        httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        return httpAsyncClientBuilder.build();
                    });

        }
        RequestConfig requestConfig = initRequestConfig(connectTimeout, socketTimeout);
        HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClients.custom();

        // 设置连接管理器
        httpAsyncClientBuilder.setConnectionManager(cm);

        // 设置RequestConfig
        if (requestConfig != null) {
            httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig);
        }
        return httpAsyncClientBuilder.build();
    }

    /**
     * 有些特殊的数据源拓展用，继承自HttpClient
     *
     * @param restfulSourceDTO
     * @param httpAsyncClient
     * @param clearConnService
     * @return
     */
    private static HttpClient buildHttpClient(RestfulSourceDTO restfulSourceDTO,
                                              CloseableHttpAsyncClient httpAsyncClient,
                                              ScheduledExecutorService clearConnService) {
        // TODO 暂时兼容
        if (restfulSourceDTO.getSourceType().equals(99)) {
            return new HbaseHttpClient(restfulSourceDTO, httpAsyncClient, clearConnService);
        } else {
            return new HttpClient(restfulSourceDTO, httpAsyncClient, clearConnService);
        }
    }
}
