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

package com.dtstack.taier.develop.service.template;

/**
 * ftp文件数据来源参数
 * @author bnyte
 * @since 1.0.0
 */
public class FtpFileReaderParam extends DaPluginParam {

    /**
     * 文件所在ftp服务器路径
     */
    private String path;

    /**
     * 是文件的第一行为标题
     */
    private Boolean isFirstLineHeader;

    /**
     * 列分隔符
     */
    private String fieldDelimiter;

    /**
     * 编码格式
     */
    private String encoding;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getFirstLineHeader() {
        return isFirstLineHeader;
    }

    public void setFirstLineHeader(Boolean firstLineHeader) {
        isFirstLineHeader = firstLineHeader;
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

}
