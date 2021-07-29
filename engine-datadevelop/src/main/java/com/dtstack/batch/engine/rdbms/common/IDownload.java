package com.dtstack.batch.engine.rdbms.common;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/6 16:08
 */
public interface IDownload {

    void configure() throws Exception;

    List<String> getMetaInfo() throws Exception;

    Object readNext();

    boolean reachedEnd();

    void close() throws Exception;

    String getFileName();
}
