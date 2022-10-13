package com.dtstack.taier.datasource.plugin.restful.core;

import com.dtstack.taier.datasource.api.client.IRestful;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.restful.core.http.HttpClient;
import com.dtstack.taier.datasource.plugin.restful.core.http.HttpClientFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * restful 特有客户端
 *
 * @author ：wangchuan
 * date：Created in 上午10:38 2021/8/11
 * company: www.dtstack.com
 */
public class RestfulSpecialClient implements IRestful {

    @Override
    public Response get(ISourceDTO sourceDTO, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers) {
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            return httpClient.get(params, cookies, headers);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public Response post(ISourceDTO sourceDTO, String bodyData, Map<String, String> cookies, Map<String, String> headers) {
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            return httpClient.post(bodyData, cookies, headers);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public Response delete(ISourceDTO sourceDTO, String bodyData, Map<String, String> cookies, Map<String, String> headers) {
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            return httpClient.delete(bodyData, cookies, headers);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public Response put(ISourceDTO sourceDTO, String bodyData, Map<String, String> cookies, Map<String, String> headers) {
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            return httpClient.put(bodyData, cookies, headers);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public Response postMultipart(ISourceDTO sourceDTO, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers, Map<String, File> files) {
        try (HttpClient httpClient = HttpClientFactory.createHttpClientAndStart(sourceDTO)) {
            return httpClient.postMultipart(params, cookies, headers, files);
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }
}
