/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.ftp;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.AddressUtil;
import com.dtstack.taier.datasource.api.dto.source.FtpSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.util.List;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 22:52 2020/2/27
 * @Description：FTP 客户端
 */
@Slf4j
public class FtpClient extends AbsNoSqlClient {

    @Override
    public Boolean testCon(ISourceDTO sourceDTO) {
        FtpSourceDTO ftpSourceDTO = (FtpSourceDTO) sourceDTO;
        Integer port = FtpUtil.getFtpPort(ftpSourceDTO.getProtocol(), ftpSourceDTO.getHostPort());
        if (!AddressUtil.telnet(ftpSourceDTO.getUrl(), port)) {
            return Boolean.FALSE;
        }
        if (StringUtils.equalsIgnoreCase(ProtocolEnum.SFTP.name(), ftpSourceDTO.getProtocol())) {
            SFTPHandler sftpHandler = null;
            try {
                sftpHandler = FtpClientFactory.getSFTPHandler(ftpSourceDTO);
            } finally {
                if (Objects.nonNull(sftpHandler)) {
                    sftpHandler.close();
                }
            }
        } else {
            FTPClient ftpClient = null;
            try {
                ftpClient = FtpClientFactory.getFtpClient(ftpSourceDTO);
            } finally {
                if (Objects.nonNull(ftpClient)) {
                    try {
                        ftpClient.disconnect();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> listFileNames(ISourceDTO sourceDTO, String path, Boolean includeDir, Boolean recursive, Integer maxNum, String regexStr) {
        FtpSourceDTO ftpSourceDTO = (FtpSourceDTO) sourceDTO;
        List<String> fileNames;
        if (StringUtils.equalsIgnoreCase(ProtocolEnum.SFTP.name(), ftpSourceDTO.getProtocol())) {
            SFTPHandler sftpHandler = null;
            try {
                sftpHandler = FtpClientFactory.getSFTPHandler(ftpSourceDTO);
                fileNames = FtpUtil.getSFTPFileNames(sftpHandler, path, includeDir, recursive, maxNum, regexStr);
            } finally {
                if (Objects.nonNull(sftpHandler)) {
                    sftpHandler.close();
                }
            }
        } else {
            FTPClient ftpClient = null;
            try {
                ftpClient = FtpClientFactory.getFtpClient(ftpSourceDTO);
                fileNames = FtpUtil.getFTPFileNames(ftpClient, path, includeDir, recursive, maxNum, regexStr);
            } finally {
                try {
                    if (Objects.nonNull(ftpClient)) {
                        ftpClient.disconnect();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        fileNames.sort(String::compareTo);
        return fileNames;
    }
}
