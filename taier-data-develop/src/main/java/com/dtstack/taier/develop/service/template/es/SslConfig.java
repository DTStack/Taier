package com.dtstack.taier.develop.service.template.es;

import com.dtstack.taier.datasource.api.utils.AssertUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: ES ssl连接认证需要的配置项
 * @date 2021-11-09 10:43:05
 */
public class SslConfig {

    private boolean useLocalFile = false;
    private String fileName;
    private String filePath;
    private String keyStorePass;
    private String type;
    private Map sftpConf;


    public static SslConfig setSslConfig(Map<String, Object> sourceMap, Map sftpConf) {

        // SftpConf 配置path
        final String path = (String) sftpConf.get("path");
        AssertUtils.notNull(path, "sftp路径参数未配置");
        final String finalPath = String.format("%s%s%s", StringUtils.removeEnd(path, File.separator), File.separator,
                StringUtils.removeStart(sourceMap.get("keyPath").toString(), File.separator));
        sftpConf.put("path", StringUtils.removeEnd(finalPath, File.separator));

        // SslConfig 配置
        SslConfig sslConfig = new SslConfig();
        sslConfig.setSftpConf(sftpConf);
        final String fileName = (String) sourceMap.get("sslFileName");
        sslConfig.setFileName(fileName);
        sslConfig.setType(StringUtils.endsWith(fileName, ".crt") ? "ca" : "pkcs12");

        return sslConfig;
    }

    public boolean isUseLocalFile() {
        return useLocalFile;
    }

    public void setUseLocalFile(boolean useLocalFile) {
        this.useLocalFile = useLocalFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getKeyStorePass() {
        return keyStorePass;
    }

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map getSftpConf() {
        return sftpConf;
    }

    public void setSftpConf(Map sftpConf) {
        this.sftpConf = sftpConf;
    }
}
