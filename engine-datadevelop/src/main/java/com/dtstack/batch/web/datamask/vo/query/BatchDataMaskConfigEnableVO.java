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

import java.util.List;

@Data
@ApiModel("脱敏开启/关闭信息")
public class BatchDataMaskConfigEnableVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "脱敏id列表", required = true)
    private List<Long> ids;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "开启/关闭脱敏 0-正常 1-禁用", example = "0", required = true)
    private Integer enable = 0;
}
