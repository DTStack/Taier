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

package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 引擎信息
 *
 * @author Ruomu[ruomu@dtstack.com]
 * @Data 2021/1/8 11:43
 */
@Data
@ApiModel("引擎信息返回信息")
public class BatchEngineInfoResultVO {

    @ApiModelProperty(value = "jdbc URL", example = "jdbc:hive2://****")
    private String jdbcURL;

    @ApiModelProperty(value = "默认 FS", example = "hdfs://***")
    private String defaultFS;

    @ApiModelProperty(value = "jdbc URL", example = "jdbc:hive2://****")
    private String userName;

    @ApiModelProperty(value = "引擎类型", example = "GREENPLUM")
    private String engineTypeEnum;

    @ApiModelProperty(value = "引擎类型", example = "Greenplum")
    private String engineType;
}
