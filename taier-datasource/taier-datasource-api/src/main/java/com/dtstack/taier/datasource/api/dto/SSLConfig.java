package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * SSL 配置相关
 *
 * @author ：wangchuan
 * date：Created in 上午10:47 2022/2/28
 * company: www.dtstack.com
 */
@Data
@Builder
public class SSLConfig {

    /**
     * ssl 文件上传的时间戳, 用于和 sftp 上 文件比较避免认证文件重复下载
     */
    private Timestamp sslFileTimestamp;

    /**
     * ssl 认证文件夹的 sftp 绝对路径
     */
    private String remoteSSLDir;

    /**
     * ssl-client.xml 文件的文件名称
     */
    private String sslClientConf;

    /**
     * 其他扩展
     */
    private Map<String, Object> otherConfig;
}
