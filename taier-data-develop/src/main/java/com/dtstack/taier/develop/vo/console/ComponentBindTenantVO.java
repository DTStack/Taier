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

package com.dtstack.taier.develop.vo.console;


import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("租户对接集群信息")
public class ComponentBindTenantVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户ID", example = "1", required = true)
    private Long tenantId;

    @ApiModelProperty(value = "集群ID", example = "2", required = true)
    private Long clusterId;

    @ApiModelProperty(value = "队列ID", example = "12")
    private Long queueId;

    @ApiModelProperty(value = "计算引擎对接信息")
    private List<ComponentBindDBVO> bindDBList;

}
