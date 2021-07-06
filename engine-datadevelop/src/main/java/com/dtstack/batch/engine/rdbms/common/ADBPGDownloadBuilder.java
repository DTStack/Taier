package com.dtstack.batch.engine.rdbms.common;

import com.dtstack.batch.common.exception.RdosDefineException;


/**
 * ADB For PG
 * date: 2021/6/7 2:02 下午
 * author: zhaiyue
 */
public class ADBPGDownloadBuilder {

    public static IDownload createDownLoadDealer(String sql, Long dtuicTenantId, String schema) {
        ADBPGDownload adbpgDownload = null;
        try {
            adbpgDownload = new ADBPGDownload(sql, dtuicTenantId, schema);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("文件不存在，原因是：%s", e.getMessage()), e);
        }
        return adbpgDownload;
    }


}
