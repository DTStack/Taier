package com.dtstack.taier.datasource.plugin.ftp;

import com.dtstack.taier.datasource.api.dto.source.FtpSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.util.Map;
import java.util.Optional;

/**
 * Ftp 客户端工厂
 *
 * @author ：wangchuan
 * date：Created in 下午4:11 2021/6/21
 * company: www.dtstack.com
 */
public class FtpClientFactory {

    /**
     * 默认连接超时时间
     */
    private static final int TIMEOUT = 60000;

    /**
     * 获取 FTP 客户端
     *
     * @param ftpSourceDTO ftp 数据源连接信息
     * @return FTP 客户端
     */
    public static FTPClient getFtpClient(FtpSourceDTO ftpSourceDTO) {
        FTPClient ftpClient = new FTPClient();
        Integer port = FtpUtil.getFtpPort(ftpSourceDTO.getProtocol(), ftpSourceDTO.getHostPort());
        try {
            ftpClient.connect(ftpSourceDTO.getUrl(), port);
            ftpClient.login(ftpSourceDTO.getUsername(), ftpSourceDTO.getPassword());
            ftpClient.setConnectTimeout(TIMEOUT);
            ftpClient.setDataTimeout(TIMEOUT);
            if (StringUtils.equalsIgnoreCase(FTPConnectMode.PASV.name(), ftpSourceDTO.getConnectMode())) {
                ftpClient.enterRemotePassiveMode();
                ftpClient.enterLocalPassiveMode();
            } else if (StringUtils.equalsIgnoreCase(FTPConnectMode.PORT.name(), ftpSourceDTO.getConnectMode())) {
                ftpClient.enterLocalActiveMode();
            }
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new SourceException("Failed to establish a connection with the ftp server, please check whether the user name and password are correct");
            }
        } catch (Exception e) {
            throw new SourceException(e.getMessage(), e);
        }
        return ftpClient;
    }

    /**
     * 获取 SFTP client
     *
     * @param ftpSourceDTO sftp 数据源信息
     * @return SFTP client
     */
    public static SFTPHandler getSFTPHandler(FtpSourceDTO ftpSourceDTO) {
        try {
            Integer finalPort = FtpUtil.getFtpPort(ftpSourceDTO.getProtocol(), ftpSourceDTO.getHostPort());
            Map<String, String> sftpConfig = Maps.newHashMap();
            sftpConfig.put(SFTPHandler.KEY_HOST, ftpSourceDTO.getUrl());
            sftpConfig.put(SFTPHandler.KEY_PORT, String.valueOf(finalPort));
            sftpConfig.put(SFTPHandler.KEY_USERNAME, ftpSourceDTO.getUsername());
            sftpConfig.put(SFTPHandler.KEY_PASSWORD, ftpSourceDTO.getPassword());
            sftpConfig.put(SFTPHandler.KEY_TIMEOUT, String.valueOf(TIMEOUT));
            sftpConfig.put(SFTPHandler.KEY_AUTHENTICATION, Optional.ofNullable(ftpSourceDTO.getAuth()).orElse(""));
            sftpConfig.put(SFTPHandler.KEY_RSA, Optional.ofNullable(ftpSourceDTO.getPath()).orElse(""));
            return SFTPHandler.getInstance(sftpConfig);
        } catch (Exception e) {
            throw new SourceException(String.format("failed to get sftp connection:%s", e.getMessage()));
        }
    }
}
