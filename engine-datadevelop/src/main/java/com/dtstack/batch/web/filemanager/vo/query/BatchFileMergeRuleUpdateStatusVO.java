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

package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("小文件合并规则根据ruleId更新规则状态")
public class BatchFileMergeRuleUpdateStatusVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "治理规则id", example = "1", required = true)
    private Long ruleId;


    @ApiModelProperty(value = "治理规则状态 0启动 1停止", example = "1", required = true)
    private Integer ruleStatus;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "是否是超管", hidden = true)
    private Boolean isRoot;

}
