package com.dtstack.taier.datasource.plugin.restful.core.http;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.datasource.plugin.restful.core.http.request.HttpPutWithEntity;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * hbase restful专用http client，增加一些特殊的请求方式
 *
 * @author luming
 * @date 2022/5/5
 */
public class HbaseHttpClient extends HttpClient {

    HbaseHttpClient(RestfulSourceDTO sourceDTO,
                    CloseableHttpAsyncClient httpclient,
                    ScheduledExecutorService clearConnService) {
        super(sourceDTO, httpclient, clearConnService);
    }

    public Response hbasePut(String bodyData) {
        HttpPutWithEntity request = new HttpPutWithEntity(httpAddressManager.getAddress());
        setHeaderAndCookie(request, null, headers);
        return hbaseExecute(request, bodyData);
    }

    private Response hbaseExecute(HttpEntityEnclosingRequestBase request, String bodyData) {
        // body 不为空时设置 entity
        if (StringUtils.isNotEmpty(bodyData)) {
            request.setEntity(generateStringEntity(bodyData));
        }
        unCompletedTaskNum.incrementAndGet();
        Future<HttpResponse> future = httpclient.execute(request, null);
        try {
            HttpResponse httpResponse = future.get();
            return handleHbaseResponse(httpResponse);
        } catch (Throwable e) {
            Response errResponse = Response.builder()
                    .build();
            handleException(errResponse, "execute http request error", e);
            return errResponse;
        } finally {
            unCompletedTaskNum.decrementAndGet();
        }
    }

    /**
     * 特殊处理hbase restful 获取scannerId的http返回值
     *
     * @param httpResponse response信息
     * @return response对象
     */
    private Response handleHbaseResponse(HttpResponse httpResponse) {
        StatusLine statusLine = httpResponse.getStatusLine();
        Response response = Response.builder()
                .statusCode(statusLine.getStatusCode())
                .build();
        //scannerId在返回的header中
        Header[] headers = httpResponse.getHeaders("Location");
        if (headers != null && headers.length == 1) {
            String value = headers[0].getValue();
            if (StringUtils.isNotBlank(value)) {
                String[] split = value.split("/");
                response.setContent(split[split.length - 1]);
            }
        } else {
            throw new SourceException(
                    "can't get scanner Id, httpResponse : " + JSON.toJSONString(httpResponse));
        }

        return response;
    }

    /**
     * 处理 http response
     *
     * @param httpResponse http 相应
     * @return 处理后封装的的 Response
     */
    @Override
    protected Response handleResponse(HttpResponse httpResponse) {
        StatusLine statusLine = httpResponse.getStatusLine();
        Response response = Response.builder()
                .statusCode(statusLine.getStatusCode())
                .build();
        if (response.getStatusCode() != null && response.getStatusCode() == 204){
            return response;
        }
        HttpEntity entity = httpResponse.getEntity();
        try {
            String content;
            Header[] headers = httpResponse.getHeaders("Content-Encoding");
            // 兼容 gzip 压缩
            if (headers != null && headers.length > 0 && headers[0].getValue().equalsIgnoreCase("gzip")) {
                GzipDecompressingEntity gzipEntity = new GzipDecompressingEntity(entity);
                content = EntityUtils.toString(gzipEntity, HttpClient.DEFAULT_CHARSET);
            } else {
                content = EntityUtils.toString(entity, HttpClient.DEFAULT_CHARSET);
            }
            response.setContent(content);
        } catch (Exception e) {
            handleException(response, "Failed to parse HttpEntity", e);
        }
        return response;
    }
}
