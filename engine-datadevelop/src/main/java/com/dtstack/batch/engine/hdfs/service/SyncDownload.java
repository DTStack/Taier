package com.dtstack.batch.engine.hdfs.service;


import com.dtstack.batch.engine.rdbms.common.IDownload;

import java.util.ArrayList;
import java.util.List;

public class SyncDownload implements IDownload {
    @Override
    public void configure() {

    }

    @Override
    public List<String> getMetaInfo() {
        return new ArrayList<>();
    }

    @Override
    public Object readNext() {
        return null;
    }

    @Override
    public boolean reachedEnd() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public String getFileName() {
        return null;
    }

    private String logInfo;


    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }
}
