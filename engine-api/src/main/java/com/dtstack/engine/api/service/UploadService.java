package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface UploadService extends DtInsightServer {

    @RequestLine("POST /node/upload/upload")
    List<Object> upload(Integer componentType, Boolean autoDelete);
}
