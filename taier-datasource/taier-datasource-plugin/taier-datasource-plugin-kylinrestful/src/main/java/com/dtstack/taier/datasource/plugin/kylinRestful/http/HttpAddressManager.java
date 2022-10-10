package com.dtstack.taier.datasource.plugin.kylinRestful.http;

import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;

public class HttpAddressManager {

    private final String address;

    public static final String HTTP_PREFIX = "http://";

    private static final String HTTPS_PREFIX = "https://";

    private HttpAddressManager(KylinRestfulSourceDTO sourceDTO) {
        // 默认 http 协议
        address = (sourceDTO.getUrl().startsWith(HTTP_PREFIX) || sourceDTO.getUrl().startsWith(HTTPS_PREFIX)) ?
                sourceDTO.getUrl() : HTTP_PREFIX + sourceDTO.getUrl();
    }

    public static HttpAddressManager createHttpAddressManager(KylinRestfulSourceDTO sourceDTO) {
        return new HttpAddressManager(sourceDTO);
    }

    public String getAddress() {
        return this.address;
    }
}
