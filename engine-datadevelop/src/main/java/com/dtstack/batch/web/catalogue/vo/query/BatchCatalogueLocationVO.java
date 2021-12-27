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

package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录位置信息")
public class BatchCatalogueLocationVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "租户id", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "目录类型", example = "1", required = true)
    private String catalogueType;

    @ApiModelProperty(value = "位置id", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "任务名称", example = "a", required = true)
    private String name;
}
