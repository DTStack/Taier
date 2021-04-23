package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface UploadService extends DtInsightServer {

    @RequestLine("POST /node/upload/upload")
    ApiResponse<List<Object>> upload(@Param("componentType") Integer componentType, @Param("autoDelete") Boolean autoDelete);

}
