package com.dtstack.batch.engine.rdbms.impala.service.util;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.engine.rdbms.common.IDownload;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 4:30 下午 2019/10/18
 */
public class ImpalaDownloadBuilder {

    public static IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        ImpalaDownload impalaDownload = null;
        try {
            impalaDownload = new ImpalaDownload(sql, dtuicTenantId, schema);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return impalaDownload;
    }

}
