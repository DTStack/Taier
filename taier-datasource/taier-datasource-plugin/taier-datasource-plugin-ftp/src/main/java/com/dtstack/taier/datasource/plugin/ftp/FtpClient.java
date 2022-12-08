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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.AddressUtil;
import com.dtstack.taier.datasource.api.dto.source.FtpSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        return super.getColumnMetaData(source, queryDTO);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        FtpSourceDTO ftpSourceDTO = (FtpSourceDTO) source;
        List<String> columns = new ArrayList<>();

        List<List<Object>> datas = new ArrayList<>();
        if (StringUtils.isBlank(ftpSourceDTO.getFilepath())) {
            log.error("[Datasource Plugin On FTP#(preview())] get filepath be empty but it is necessary.");
            throw new RuntimeException("[Datasource Plugin On FTP#(preview())] filepath cannot be empty.");
        }
        Integer port = FtpUtil.getFtpPort(ftpSourceDTO.getProtocol(), ftpSourceDTO.getHostPort());
        if (!AddressUtil.telnet(ftpSourceDTO.getUrl(), port)) {
            log.error("[Datasource Plugin On FTP#(preview())] connection to the ftp server is abnormal with ftp payload for --> {}", JSONObject.toJSONString(ftpSourceDTO));
            throw new RuntimeException("connection to the ftp server is abnormal");
        }
        if (StringUtils.equalsIgnoreCase(ProtocolEnum.SFTP.name(), ftpSourceDTO.getProtocol())) {
            SFTPHandler sftpHandler = null;
            try {
                sftpHandler = FtpClientFactory.getSFTPHandler(ftpSourceDTO);
                InputStream fileInputStream = sftpHandler.getFileInputStream(ftpSourceDTO.getFilepath());
                BufferedReader bis = new BufferedReader(new InputStreamReader(fileInputStream, ftpSourceDTO.getEncoding()));
                // memory to store buffered stream per read
                int limit = 1;

                String line = null;
                while ((line = bis.readLine()) != null && limit <= 10) {
                    String[] split = line.split(ftpSourceDTO.getColumnSeparator());
                    if (ftpSourceDTO.getFirstLineColumnName() && limit == 1) {
                        columns.addAll(Arrays.asList(split));
                    } else if (!ftpSourceDTO.getFirstLineColumnName() && limit == 1) {
                        for (int i = 0; i < split.length; i++) {
                            columns.add(split[i] + (i + 1));
                        }
                    } else {
                        datas.add(new ArrayList<>(Arrays.asList(split)));
                    }
                    limit++;
                }
            }
            catch (Exception e) {
                log.error("[Datasource Plugin On FTP#(preview())] connection to the ftp server is abnormal with ftp payload for --> {}", JSONObject.toJSONString(ftpSourceDTO), e);
                throw new RuntimeException("connection to the ftp server is abnormal");
            } finally {
                if (Objects.nonNull(sftpHandler)) {
                    sftpHandler.close();
                }
            }
        } else {
            FTPClient ftpClient = null;
            try {
                ftpClient = FtpClientFactory.getFtpClient(ftpSourceDTO);
                InputStream fileInputStream = ftpClient.retrieveFileStream(ftpSourceDTO.getFilepath());
                BufferedReader bis = new BufferedReader(new InputStreamReader(fileInputStream, ftpSourceDTO.getEncoding()));
                // memory to store buffered stream per read
                int limit = 1;

                String line = null;
                while ((line = bis.readLine()) != null && limit <= 10) {
                    String[] split = line.split(ftpSourceDTO.getColumnSeparator());
                    if (ftpSourceDTO.getFirstLineColumnName() && limit == 1) {
                        columns.addAll(Arrays.asList(split));
                    } else if (!ftpSourceDTO.getFirstLineColumnName() && limit == 1) {
                        for (int i = 0; i < split.length; i++) {
                            columns.add(split[i] + (i + 1));
                        }
                    } else {
                        datas.add(new ArrayList<>(Arrays.asList(split)));
                    }
                    limit++;
                }
            } catch (IOException e) {
                log.error("[Datasource Plugin On FTP#(preview())] failed to get remote file with ftp payload for --> {}", JSONObject.toJSONString(ftpSourceDTO));
                throw new RuntimeException(e);
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

        if (log.isDebugEnabled()) {
            log.debug("[Datasource Plugin On FTP#(preview())] reader filepath columns data of the {}", columns);
        }
        return datas;
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
