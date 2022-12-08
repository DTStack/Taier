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

package com.dtstack.taier.develop.vo.develop.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * parsing ftp file column request payload
 * 
 * @since 1.3.1
 */
@ApiModel("任务信息")
public class DevelopTaskParsingFTPFileParamVO {

    @ApiModelProperty("需要解析的文件路径")
    @NotBlank(message = "请指定解析文件")
    private String filepath;

    @ApiModelProperty("列分隔符")
    @NotBlank(message = "请指定列分隔符")
    private String columnSeparator;

    @ApiModelProperty("表头是否为列名")
    @NotNull(message = "请指定表头是否为列名")
    private Boolean firstColumnName;

    @ApiModelProperty("数据源id")
    @NotNull(message = "请指定数据源")
    private Long sourceId;

    @ApiModelProperty("编码格式")
    private String encoding = "UTF-8";

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }

    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    public Boolean getFirstColumnName() {
        return firstColumnName;
    }

    public void setFirstColumnName(Boolean firstColumnName) {
        this.firstColumnName = firstColumnName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

