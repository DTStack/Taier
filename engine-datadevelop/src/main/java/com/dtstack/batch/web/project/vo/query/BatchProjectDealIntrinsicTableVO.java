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

package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("项目同步元数据信息")
public class BatchProjectDealIntrinsicTableVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "UIC 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "数据源类型", example = "7", required = true)
    private Integer dataSourceType;

    @ApiModelProperty(value = "租户ID", example = "1", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "所属类目 ID", example = "0", required = true)
    private Long catalogueId;

    @ApiModelProperty(value = "生命周期", example = "9999", required = true)
    private Integer lifecycle;

    @ApiModelProperty(value = "token", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "页面选择的需要添加的表名数组")
    private List<String> addTableNames;

    @ApiModelProperty(value = "页面选择的需要删除的表名数组")
    private List<String> dropTableNames;

    @ApiModelProperty(value = "是否同步所有表", example = "false")
    private Boolean synchronizeAllTable;
}
