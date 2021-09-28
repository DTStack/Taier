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

package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/18 11:11 上午
 */
@Data
@ApiModel("获取已经引入的数据源接口 回参")
public class BatchDataSourceHaveImportResultVO {

    @ApiModelProperty(value = "离线数据源id", example = "1")
    private Long id;

    @ApiModelProperty(value = "数据源名称", example = "myData")
    private String dataName;

    @ApiModelProperty(value = "数据源类型", example = "1")
    private Integer type;

    @ApiModelProperty(value = "数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "是否开启Kerberos")
    private Boolean openKerberos;

    @ApiModelProperty(value = "数据源描述", example = "desc")
    private String dataDesc;

    @ApiModelProperty(value = "数据源连接url信息（json格式）")
    private String linkJson;

    @ApiModelProperty(value = "是否为默认数据源", example = "1")
    private Integer isDefault;

    @ApiModelProperty(value = "连接状态 0连接丢失，1连接可用", example = "1")
    private Integer status;

    @ApiModelProperty(value = "映射状态 0=未映射，1=已映射", example = "1")
    private Integer linkStatus;

    @ApiModelProperty(value = "映射的数据源id", example = "1")
    private Long linkSourceId;

    @ApiModelProperty(value = "是否使用 0=未使用，1=已使用", example = "1")
    private Integer active;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Date gmtModified;

    @ApiModelProperty(value = "是否对接hadoop引擎 如果false 默认数据源不显示整库同步和同步记录")
    private Boolean hasHadoopEngine;
}
