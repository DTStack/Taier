package com.dtstack.batch.engine.rdbms.inceptor.util;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.engine.rdbms.common.IDownload;


public class InceptorDownloadBuilder {

    public static IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        InceptorPluginDownload impalaDownload = null;
        try {
            impalaDownload = new InceptorPluginDownload(sql, dtuicTenantId, schema);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return impalaDownload;
    }

}
