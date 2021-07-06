package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.batch.common.exception.RdosDefineException;

/**
 * @author yuebai
 * @date 2019-06-10
 */
public class TiDBDownloadBuilder {

    public static IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        TiDBDownload tiDBDownload = null;
        try {
            tiDBDownload = new TiDBDownload(sql, dtuicTenantId, schema);
            tiDBDownload.configure();
        } catch (Exception e) {
            throw new RdosDefineException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return tiDBDownload;
    }

}
