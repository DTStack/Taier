package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

public interface DownloadService extends DtInsightServer {

    @RequestLine("POST /node/download/handleDownload")
    void handleDownload(Long componentId, Integer downloadType, Integer componentType,
                        String hadoopVersion, String clusterName);
}
