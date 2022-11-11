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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.service.template.BaseReaderPlugin;
import com.dtstack.taier.develop.service.template.FtpFileReaderParam;
import com.dtstack.taier.develop.service.template.PluginName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author bnyte
 * @since 1.3.1
 */
public class FtpFileReader extends BaseReaderPlugin implements Reader {

    private static final Logger logger = LoggerFactory.getLogger(FtpFileReader.class);

    /**
     * ftp文件中的列映射列表
     */
    public static class FtpColumn {

        /**
         * 列索引 0开始
         */
        private Integer index;

        /**
         * 列数据类型
         */
        private String type;

        /**
         * 列名称
         */
        private String name;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "FtpColumn{" +
                    "index=" + index +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * 文件所在ftp服务器路径
     */
    private String path;

    /**
     * 协议
     *  ftp或sftp, 默认sftp
     */
    private String protocol = "sftp";

    /**
     * 端口
     *  ftp服务器端口,默认22
     */
    private Integer port = 22;

    /**
     * 是文件的第一行为标题
     */
    private Boolean isFirstLineHeader;

    /**
     * ftp服务器host
     */
    private String host;

    /**
     * ftp reader文件列映射列表
     */
    private List<FtpColumn> column;

    /**
     * ftp服务器密码
     */
    private String password;

    /**
     * 列分隔符
     */
    private String fieldDelimiter;

    /**
     * 编码格式
     */
    private String encoding;

    /**
     * 用户名
     */
    private String username;


    @Override
    public String pluginName() {
        return PluginName.FTP_R;
    }

    @Override
    public void checkFormat(JSONObject data) {
        logger.info("data info --> {}", data);
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

    public List<FtpColumn> getColumn() {
        return column;
    }

    public void setColumn(List<FtpColumn> column) {
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
