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

package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.*;

import java.nio.charset.StandardCharsets;

/**
 * ftp source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FtpSourceDTO extends AbstractSourceDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据源类型
     */
    protected Integer sourceType;

    /**
     * 是否缓存
     */
    @Builder.Default
    protected Boolean isCache = false;

    /**
     * 地址
     */
    private String url;

    /**
     * 端口号
     */
    private String hostPort;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 认证
     */
    private String auth;

    /**
     * 目录
     * FTP rsa 路径
     */
    private String path;

    /**
     * 连接模式
     */
    private String connectMode;

    /**
     * column separator
     *  Process according to the column separator when previewing data
     */
    private String columnSeparator;

    /**
     * whether the first line is the column name
     *  default false
     *  if true: Parse the first row of data and use columnSeparator to separate and return the column name
     *  if false: return the column name through column${index}
     */
    private Boolean firstLineColumnName = false;

    /**
     * full file path
     */
    private String filepath;

    /**
     * file encoding format
     */
    private String encoding = StandardCharsets.UTF_8.name();


    @Override
    public Integer getSourceType() {
        return DataSourceType.FTP.getVal();
    }
}
