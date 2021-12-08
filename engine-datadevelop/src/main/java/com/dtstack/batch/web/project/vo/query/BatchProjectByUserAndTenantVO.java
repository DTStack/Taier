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

@Data
@ApiModel("获取所有项目信息")
public class BatchProjectByUserAndTenantVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "total 为false,查出该userId有参加的项目", example = "false", required = true)
    private Boolean total;

    @ApiModelProperty(hidden = true, value = "是否是uic的root用户")
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

}
