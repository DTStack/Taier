package com.dtstack.taier.datasource.plugin.kylinRestful.http;

import com.dtstack.taier.datasource.api.dto.JobParam;
import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class HttpClient {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final String POST_REQUEST = "POST";

    public static final String GET_REQUEST = "GET";

    public static final String PUT_REQUEST = "PUT";

    private static final int DEFAULT_SOCKET_TIME = 30000;

    private static final int DEFAULT_CONNECT_TIME = 3000;

    public String authorization;

    private Integer socketTimeout;

    private Integer connectionTimeout;
    /**
     * http 地址、port 管理
     */
    private final HttpAddressManager httpAddressManager;


    HttpClient(KylinRestfulSourceDTO sourceDTO) {
        //用户名密码base64加密
        this.authorization = Base64.getEncoder().encodeToString((sourceDTO.getUsername() + ":" + sourceDTO.getPassword()).getBytes(StandardCharsets.UTF_8));
        this.httpAddressManager = HttpAddressManager.createHttpAddressManager(sourceDTO);
    }

    HttpClient(KylinRestfulSourceDTO sourceDTO, JobParam.RequestConfig requestConfig) {
        this(sourceDTO);
        if (requestConfig != null) {
            socketTimeout = Optional.ofNullable(requestConfig.getSocketTimeout()).orElse(DEFAULT_SOCKET_TIME);
            connectionTimeout = Optional.ofNullable(requestConfig.getConnectTimeout()).orElse(DEFAULT_CONNECT_TIME);
        } else {
            socketTimeout = DEFAULT_SOCKET_TIME;
            connectionTimeout = DEFAULT_CONNECT_TIME;
        }
    }

    private String execute(String apiPath, String method, String body, Map<String, String> headers) {
        apiPath = getUrl(apiPath);
        StringBuilder out = new StringBuilder();
        HttpURLConnection connection;
        BufferedReader in;
        try {
            URL url = new URL(apiPath);
            connection = (HttpURLConnection) url.openConnection();
            //请求超时时间设置
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(socketTimeout);

            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + authorization);
            connection.setRequestProperty("Content-Type", "application/json");
            if (MapUtils.isNotEmpty(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (body != null) {
                byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
                OutputStream os = connection.getOutputStream();
                os.write(outputInBytes);
                os.close();
            }
            InputStream content = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                out.append(line);
            }
            in.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        }
        return out.toString();
    }

    private String getUrl(String apiPath) {
        return this.httpAddressManager.getAddress() + apiPath;
    }

    public String post(String apiPath, String body) {
        return post(apiPath, body, null);
    }

    public String post(String apiPath, String body, Map<String, String> headers) {
        return execute(apiPath, POST_REQUEST, body, headers);
    }

    public String get(String apiPath) {
        return get(apiPath, null);
    }

    public String get(String apiPath, String body) {
        return get(apiPath, body, null);
    }

    public String get(String apiPath, String body, Map<String, String> headers) {
        return execute(apiPath, GET_REQUEST, body, headers);
    }

    public String put(String apiPath) {
        return put(apiPath, null);
    }

    public String put(String apiPath, String body) {
        return execute(apiPath, PUT_REQUEST, body, null);
    }
}
