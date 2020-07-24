package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;


public interface DownloadService extends DtInsightServer {

    @RequestLine("POST /node/download/handleDownload")
    ApiResponse handleDownload(@Param("componentId") Long componentId, @Param("downloadType") Integer downloadType, @Param("componentType") Integer componentType,
                               @Param("hadoopVersion") String hadoopVersion, @Param("clusterName") String clusterName);
}
