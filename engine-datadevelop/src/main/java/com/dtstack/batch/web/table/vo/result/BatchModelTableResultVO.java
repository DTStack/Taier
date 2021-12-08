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

package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("表基本信息")
public class BatchModelTableResultVO {

    @ApiModelProperty(value = "1 层级 2 主题域 3 刷新频率 4 增量定义")
    private Integer type;

    @ApiModelProperty(value = "层级编号", example = "2")
    private Integer level;

    @ApiModelProperty(value = "表名", example = "dev")
    private String name;

    @ApiModelProperty(value = "说明", example = "表备注")
    private String modelDesc;

    @ApiModelProperty(value = "前缀标识", example = "dev")
    private String prefix;

    @ApiModelProperty(value = "生命周期  单位：天", example = "99")
    private Integer lifeDay;

    @ApiModelProperty(value = "是否层级依赖 0:不依赖, 1:依赖", example = "0")
    private Integer depend;

    @ApiModelProperty(value = "最近修改人id", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "创建者用户id", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "平台类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

}
