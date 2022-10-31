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

package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/9/20
 */
@ApiModel("数据源支持模式")
public class DsSupportVO {

    @ApiModelProperty("数据同步writer")
    private List<Integer> writers;

    @ApiModelProperty("数据同步reader")
    private List<Integer> readers;

    @ApiModelProperty("实时采集writer")
    private List<Integer> dataAcquisitionWriter;

    @ApiModelProperty("实时采集reader")
    private List<Integer> dataAcquisitionReader;

    @ApiModelProperty("flink 源表")
    private List<Integer> flinkSqlSources;

    @ApiModelProperty("flink 结果表")
    private List<Integer> flinkSqlSinks;

    @ApiModelProperty("flink 维表")
    private List<Integer> flinkSqlSides;


    public List<Integer> getWriters() {
        return writers;
    }

    public void setWriters(List<Integer> writers) {
        this.writers = writers;
    }

    public List<Integer> getReaders() {
        return readers;
    }

    public void setReaders(List<Integer> readers) {
        this.readers = readers;
    }

    public List<Integer> getDataAcquisitionWriter() {
        return dataAcquisitionWriter;
    }

    public void setDataAcquisitionWriter(List<Integer> dataAcquisitionWriter) {
        this.dataAcquisitionWriter = dataAcquisitionWriter;
    }

    public List<Integer> getDataAcquisitionReader() {
        return dataAcquisitionReader;
    }

    public void setDataAcquisitionReader(List<Integer> dataAcquisitionReader) {
        this.dataAcquisitionReader = dataAcquisitionReader;
    }

    public List<Integer> getFlinkSqlSources() {
        return flinkSqlSources;
    }

    public void setFlinkSqlSources(List<Integer> flinkSqlSources) {
        this.flinkSqlSources = flinkSqlSources;
    }

    public List<Integer> getFlinkSqlSinks() {
        return flinkSqlSinks;
    }

    public void setFlinkSqlSinks(List<Integer> flinkSqlSinks) {
        this.flinkSqlSinks = flinkSqlSinks;
    }

    public List<Integer> getFlinkSqlSides() {
        return flinkSqlSides;
    }

    public void setFlinkSqlSides(List<Integer> flinkSqlSides) {
        this.flinkSqlSides = flinkSqlSides;
    }
}
