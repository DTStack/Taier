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

package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("数据脱敏管理信息")
public class BatchDataMaskConfigVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "脱敏名称", example = "aka", required = true)
    private String name;

    @ApiModelProperty(value = "脱敏表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "脱敏表字段名称", example = "id", required = true)
    private String columnName;

    @ApiModelProperty(value = "脱敏规则id", example = "1", required = true)
    private Long ruleId;

    @ApiModelProperty(value = "脱敏样例", example = "*", required = true)
    private String example;

    @ApiModelProperty(value = "修改用户id", example = "1", required = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "开启/关闭脱敏 0-正常 1-禁用", example = "0", required = true)
    private Integer enable = 0;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型 RDOS(1) DQ(2), API(3) TAG(4) MAP(5) CONSOLE(6) STREAM(7) DATASCIENCE(8)", example = "1", required = true)
    private Integer appType;

    @ApiModelProperty(value = "脱敏id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1", required = true)
    private Integer isDeleted = 0;
}
