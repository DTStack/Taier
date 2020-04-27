package com.dtstack.engine.master.download;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/6 16:08
 */
public interface IDownload {

    void configure() throws Exception;

    List<String> getMetaInfo() throws Exception;

    Object readNext() throws Exception;

    boolean reachedEnd() throws Exception;

    void close() throws Exception;

    String getFileName();
}
