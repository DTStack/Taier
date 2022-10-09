package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import lombok.Data;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:05 2020/10/29
 * @Description：UGI 缓存信息
 */
@Data
public class UGICacheData {
    /**
     * 过期时间戳 只有头结点才存在
     */
    private Long timeoutStamp;

    /**
     * UGI 信息
     */
    private UserGroupInformation ugi;

    public UGICacheData(UserGroupInformation ugi) {
        this.ugi = ugi;
        timeoutStamp = System.currentTimeMillis() + 10 * 1000;
    }
}
