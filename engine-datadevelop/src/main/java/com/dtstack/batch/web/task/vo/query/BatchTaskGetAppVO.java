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

package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * date: 2021/4/12 4:18 下午
 * author: zhaiyue
 */
@Data
@ApiModel("获取产品信息")
public class BatchTaskGetAppVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", required = true, example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "UIC 用户 ID", hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(value = "dt token", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "产品 code", hidden = true)
    private String productCode;

}
