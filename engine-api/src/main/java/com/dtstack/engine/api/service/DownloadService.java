package com.dtstack.engine.api.service;

public interface DownloadService {
    public void handleDownload(Long componentId, Integer downloadType, Integer componentType,
                               String hadoopVersion, String clusterName);
}
