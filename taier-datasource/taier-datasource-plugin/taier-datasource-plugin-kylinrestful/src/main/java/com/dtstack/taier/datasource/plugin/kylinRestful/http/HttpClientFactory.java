package com.dtstack.taier.datasource.plugin.kylinRestful.http;

import com.dtstack.taier.datasource.api.dto.JobParam;
import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientFactory {
    public static HttpClient createHttpClient(KylinRestfulSourceDTO sourceDTO) {
        return new HttpClient(sourceDTO);
    }

    public static HttpClient createHttpClientWithTimeout(
            KylinRestfulSourceDTO sourceDTO, JobParam.RequestConfig requestConfig) {
        return new HttpClient(sourceDTO, requestConfig);
    }
}
