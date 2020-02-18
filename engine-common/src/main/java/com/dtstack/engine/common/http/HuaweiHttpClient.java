package com.dtstack.engine.common.http;

import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


/**
 *
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class HuaweiHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HuaweiHttpClient.class);

    private static int SocketTimeout = 60000;// 10秒

    private static int ConnectTimeout = 60000;// 10秒

    // 将最大连接数增加到100
    private static int maxTotal = 100;

    // 将每个路由基础的连接增加到20
    private static int maxPerRoute = 20;

    private String loginName;

    private String loginPassword;

    private String loginUrl;

    private String uri;

    private CloseableHttpClient httpClient;

    public static HttpClientContext context = null;

    public static String jSessionId = null;

    public HuaweiHttpClient(String loginName, String loginPassword, String loginUrl) {
        this.loginName = loginName;
        this.loginPassword = loginPassword;
        this.loginUrl = loginUrl;
        this.uri = "https://" + loginUrl;
        this.httpClient = getHttpClient();
    }

    private CloseableHttpClient getHttpClient() {

        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();

        SSLContext sslcontext = null;
        try {
            sslcontext = createIgnoreVerifySsl();
        } catch (Exception e){
            throw new RuntimeException("", e);
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);

        String[] ipPort = loginUrl.split(":");
        String ip = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(ip, port),
                new UsernamePasswordCredentials(loginName, loginPassword));

        return HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(new RdosHttpRequestRetryHandler())
                .setDefaultCredentialsProvider(provider)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .build();
    }

    public static SSLContext createIgnoreVerifySsl() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLS");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    public CloseableHttpResponse cas(String loginUrl) throws IOException{
        HttpGet get = new HttpGet(loginUrl);
        CloseableHttpResponse response =  httpClient.execute(get);
        return response;
    }

    public HttpEntity getHeader(String lt){
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("name=\"keytab\"; filename=\"\"", new byte[]{});
        builder.addTextBody("username", loginName);
        builder.addTextBody("password", loginPassword);
        builder.addTextBody("lt", lt);  //添加文本类型参数
        builder.addTextBody("_eventId", "submit");
        builder.addTextBody("submit", "Login");
        return builder.build();
    }

    public String login(String loginUrl, String realUrl) throws IOException {

        CloseableHttpResponse casResponse = cas(loginUrl);
        Header header = casResponse.getFirstHeader("Set-Cookie");
        String v = header.getValue();
        String retStr1 = EntityUtils.toString(casResponse.getEntity(), "utf-8");

        Document doc = Jsoup.parse(retStr1);
        String lt = doc.select("div[class = row btn-row]").select("input[name=lt]").attr("value");
        HttpPost postMethod = new HttpPost(loginUrl);
        postMethod.setHeader("Cookie", v.split(";")[0]);
        setHeaders(postMethod);

        CloseableHttpResponse response = null;

        try {
            postMethod.setEntity(getHeader(lt));

            String retStr = "";
            response = httpClient.execute(postMethod);

            int statusCode = response.getStatusLine().getStatusCode();
            retStr = EntityUtils.toString(response.getEntity());

            Header[] cookies = response.getHeaders("Set-Cookie");
            StringBuilder cookieString = new StringBuilder();
            for (Header cookie : cookies){
                if (!cookie.getValue().startsWith("CASPRIVACY")){
                    cookieString.append(cookie.getValue().split(";")[0]).append(";");
                }
            }

            String uri = getUrl(cookieString.toString(), lt, realUrl);

            String ticket = loginUrl(lt, cookieString.toString(), uri);

            jSessionId = ticketPost(ticket, lt);

        }catch (Exception e){

        }

        return taskUrl(jSessionId, realUrl);
    }

    private void setHeaders(HttpEntityEnclosingRequestBase postMethod){
        postMethod.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        postMethod.addHeader("Accept-Encoding", "gzip, deflate, br");
        postMethod.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        postMethod.addHeader("Host", loginUrl);
        postMethod.addHeader("Origin", uri);
        postMethod.addHeader("Upgrade-Insecure-Requests", "1");
    }

    public String ticketPost(String ticket, String lt) throws IOException {
        HttpPost ticketPost = new HttpPost(ticket);
        ticketPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        ticketPost.setHeader("Accept-Encoding", "gzip, deflate, br");
        ticketPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        ticketPost.setHeader("Cache-Control", "max-age=0");
        ticketPost.setHeader("Connection", "keep-alive");
        //ticketPost.setHeader("Host", "172.16.8.152:20026");
        ticketPost.setHeader("Upgrade-Insecure-Requests", "1");

        ticketPost.setEntity(getHeader(lt));

        CloseableHttpResponse ticketPostR =  httpClient.execute(ticketPost);
        return ticketPostR.getHeaders("Set-Cookie")[0].getValue().split(";")[0];
    }

    public String getUrl(String cookie, String lt, String realUrl) throws IOException {
        HttpPost getUrl = new HttpPost(realUrl);
        getUrl.setHeader("Cookie", cookie);
        setHeaders(getUrl);
        getUrl.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryh0eAABrUYZAV8qXw");
        getUrl.setEntity(getHeader(lt));

        CloseableHttpResponse getUrlR =  httpClient.execute(getUrl);
        return getUrlR.getFirstHeader("Location").toString().split("Location: ")[1];
    }

    public String loginUrl(String lt, String cookie, String uri) throws IOException {
        HttpPost loginPost = new HttpPost(uri);
        loginPost.setHeader("Cookie", cookie);
        setHeaders(loginPost);
        loginPost.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryh0eAABrUYZAV8qXw");

        loginPost.setEntity(getHeader(lt));

        CloseableHttpResponse loginPostR =  httpClient.execute(loginPost);
        return "https" + loginPostR.getFirstHeader("Location").toString().split("https")[1];
    }

    public String taskUrl(String jsession, String realUrl) throws IOException {
        HttpGet taskGet = new HttpGet(realUrl);
        taskGet.setHeader("Cookie", jsession);
        taskGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        taskGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        taskGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        taskGet.setHeader("Cache-Control", "max-age=0");
        taskGet.setHeader("Connection", "keep-alive");
        taskGet.setHeader("Upgrade-Insecure-Requests", "1");
        taskGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36");
        CloseableHttpResponse taskGetR =  httpClient.execute(taskGet);
        String taskGetS = EntityUtils.toString(taskGetR.getEntity(), "utf-8");

        return taskGetS;
    }

    public static void main(String[] args) throws IOException {
        HuaweiHttpClient hw = new HuaweiHttpClient("yanxi01", "Admin@1234", "172.16.8.152:20009");
        String s = hw.login("https://172.16.8.152:20009/cas/login", "https://node2:26010/node/containerlogs/container_e17_1569423304990_0010_02_000001/yanxi01");
        System.out.println(s);
    }
}
