package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;

/**
 * @author yuebai
 * @date 2019-06-10
 */
public class LibraDownloadBuilder {

    public IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        LibraDownload libraDownload = null;
        try {
            libraDownload = new LibraDownload(sql, dtuicTenantId, schema);
            libraDownload.configure();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return libraDownload;
    }

}
