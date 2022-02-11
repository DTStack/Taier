package com.dtstack.taier.common.http;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.pluginapi.http.RdosHttpRequestRetryHandler;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-16 00:07
 **/
public class PoolHttpClient {
    private static final Logger logger = LoggerFactory
            .getLogger(PoolHttpClient.class);

    private static int SocketTimeout = 30000;// 10秒

    private static int ConnectTimeout = 30000;// 5秒

    // 将最大连接数增加到100
    private static int maxTotal = 100;

    // 将每个路由基础的连接增加到20
    private static int maxPerRoute = 20;

    private static Boolean SetTimeOut = true;

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static CloseableHttpClient httpClient = null;

    private static String code = "UTF-8";

    static {
        try{
            httpClient = getHttpClient();
        }catch (Exception e){
            logger.error("",e);
        }
    }

    private static CloseableHttpClient getHttpClient() throws Exception {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
//		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
//				.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", new SSLConnectionSocketFactory(VerifySSLContext.createIgnoreVerifySSL())).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        return HttpClients.custom()
                .setConnectionManager(cm).setRetryHandler(new RdosHttpRequestRetryHandler()).build();
    }

    public static String post(String url, String bodyData, Map<String,Object> cookies) {
        return post(url, bodyData, cookies, null);
    }

    public static String postWithTimeout(String url, String bodyData,Map<String,Object> cookies,int socketTimeout,int connectTimeout){
        return postWithTimeout(url, bodyData, cookies, null,socketTimeout,connectTimeout);
    }

    public static String postWithFile(String url, Map<String,Object> data, Map<String,Object> cookies, Map<String, File> files) {
        return postForm(url, data, cookies, null,files);
    }

    public static String postForm(String url, Map<String,Object> params, Map<String, Object> cookies, Map<String, Object> headers,Map<String,File> files) {
        HttpPost httpPost = new HttpPost(url);
        if (cookies != null && cookies.size() > 0) {
            httpPost.addHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 上传的文件
        for (Map.Entry<String, File> file : files.entrySet()) {
            builder.addBinaryBody(file.getKey(), file.getValue(), ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"),file.getValue().getName());
        }
        // 设置其他参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()), ContentType.MULTIPART_FORM_DATA.withCharset("UTF-8"));
        }
        httpPost.setEntity(builder.build());
        setConfig(httpPost);
        return execute(httpPost);
    }

    public static String post(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers) {
        return post(url, bodyData, cookies, headers, true);
    }

    /**
     *
     * @param url
     * @param bodyData
     * @param cookies
     * @param headers
     * @param dealWithRespBody true :deal with RespBody,false: Don't deal with RespBody
     * @return
     */
    public static String post(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers,Boolean dealWithRespBody) {
        HttpPost httpPost = new HttpPost(url);
        if (cookies != null && cookies.size() > 0) {
            httpPost.addHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        if (StringUtils.isNotBlank(bodyData)) {
            StringEntity stringEntity = new StringEntity(bodyData, code);
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        }
        setConfig(httpPost);
        //true :默认逻辑处理 false:直接返回返回结果 不对response进行任何处理 直接返回
        if (BooleanUtils.isTrue(dealWithRespBody)) {
            return execute(httpPost);
        } else {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, code);
            } catch (Exception e) {
                logger.error("url:" + httpPost.getURI().toString() + "--->http request error:", e);
            }
        }
        return null;
    }

    public static String postWithTimeout(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers,int socketTimeout,int connectTimeout) {
        HttpPost httpPost = new HttpPost(url);
        if (cookies != null && cookies.size() > 0) {
            httpPost.addHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        if (StringUtils.isNotBlank(bodyData)) {
            StringEntity stringEntity = new StringEntity(bodyData, code);
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        }
        socketTimeout = socketTimeout <= 0?300000:socketTimeout;
        connectTimeout = connectTimeout <= 0?2000:connectTimeout;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();// 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        return execute(httpPost);
    }

    private static String execute(HttpPost httpPost) {
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = null;
            // 请求数据
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            // FIXME 暂时不从header读取
            String result = EntityUtils.toString(entity, code);
            if (status == HttpStatus.SC_OK) {
                responseBody = result;
            } else if (status == HttpStatus.SC_UNAUTHORIZED){
                throw new DtCenterDefException("登陆状态失效");
            } else {
                logger.error("request url:{} fail:{}", httpPost.getURI().toString(), result);
                return null;
            }
            return responseBody;
        } catch (Exception e) {
            logger.error("url:" + httpPost.getURI().toString() + "--->http request error:", e);
        }
        return null;
    }

    public static String post(String url, Map<String, Object> paramMap,Map<String,Object> cookies) throws IOException {
        String bodyData = "";
        if (paramMap != null && paramMap.size() > 0) {
            bodyData = objectMapper.writeValueAsString(paramMap);
        }
        return post(url, bodyData,cookies);
    }

    private static void setConfig(HttpRequestBase httpRequest){
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();// 设置请求和传输超时时间
            httpRequest.setConfig(requestConfig);
        }
    }

    public static String get(String url,Map<String,Object> cookies) {
        return get(url, cookies, null);
    }

    public static String get(String url, Map<String,Object> cookies, Map<String, Object> headers) {
        return get(url, cookies, null,true);
    }

    /**
     *
     * @param url
     * @param cookies
     * @param headers
     * @param dealWithRespBody true :deal with RespBody,false: Don't deal with RespBody
     * @return
     */
    public static String get(String url, Map<String,Object> cookies, Map<String, Object> headers,Boolean dealWithRespBody) {
        HttpGet httpGet = new HttpGet(url);
        setConfig(httpGet);
        if(cookies!=null&&cookies.size()>0){
            httpGet.setHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        //true :默认逻辑处理 false:直接返回返回结果 不对response进行任何处理 直接返回
        try (CloseableHttpResponse response = httpClient.execute(httpGet);) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, Charsets.UTF_8);
            if (BooleanUtils.isTrue(dealWithRespBody)) {
                String respBody = null;
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    respBody = result;
                } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    throw new DtCenterDefException("登陆状态失效");
                } else {
                    logger.error("request url:{} fail:{}", url, result);
                }
                return respBody;
            } else {
                return result;
            }
        } catch (Exception e) {
            logger.error("url:{}--->http request error:{}", url, e);
        }
        return null;
    }

    public static String getCookieFormate(Map<String,Object> cookies){
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Object>> sets = cookies.entrySet();
        for (Map.Entry<String, Object> s : sets) {
            String value = Objects.isNull(s.getValue()) ? "" : s.getValue().toString();
            sb.append(s.getKey() + "=" + value + ";");
        }
        return sb.toString();
    }
}
