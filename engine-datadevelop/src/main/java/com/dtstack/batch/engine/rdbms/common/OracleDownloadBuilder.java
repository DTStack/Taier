package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.batch.common.exception.RdosDefineException;

/**
 * shixi
 */
public class OracleDownloadBuilder {

    public static IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        OracleDownload oracleDownload = null;
        try {
            oracleDownload = new OracleDownload(sql, dtuicTenantId, schema);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return oracleDownload;
    }
}
