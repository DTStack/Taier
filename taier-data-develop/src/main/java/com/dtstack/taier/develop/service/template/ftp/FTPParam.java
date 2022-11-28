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

package com.dtstack.taier.develop.service.template.ftp;

import java.util.List;

/**
 * 
 * @since 1.3.1
 */
public class FTPParam {

    /**
     * current source id
     */
    private Integer sourceId;

    /**
     * the ftp server path where the file is located
     */
    private String path;

    /**
     * protocol
     *  ftp or sftp default sftp
     */
    private String protocol = "sftp";

    /**
     * port
     *  ftp server port default 22
     */
    private Integer port = 22;

    /**
     * is the header of the first line of the file
     */
    private Boolean isFirstLineHeader;

    /**
     * ftp server host
     */
    private String host;

    /**
     * ftp reader file column mapping list
     */
    private List<FTPColumn> column;

    /**
     * ftp server password
     */
    private String password;

    /**
     * column separator
     */
    private String fieldDelimiter;

    /**
     * encoding format
     */
    private String encoding;

    /**
     * username
     */
    private String username;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getFirstLineHeader() {
        return isFirstLineHeader;
    }

    public void setFirstLineHeader(Boolean firstLineHeader) {
        isFirstLineHeader = firstLineHeader;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<FTPColumn> getColumn() {
        return column;
    }

    public void setColumn(List<FTPColumn> column) {
        this.column = column;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
