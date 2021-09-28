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

package com.dtstack.batch.web.user.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加用户的Map信息")
public class BatchRoleUserAddNewMapVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "活跃状态", required = true)
    private Boolean active;

    @ApiModelProperty(value = "邮箱", example = "true", required = true)
    private String email;

    @ApiModelProperty(value = "手机号", example = "17731900898")
    private String phone;

    @ApiModelProperty(value = "uic 用户 ID", example = "1", required = true)
    private Long userId;

    @ApiModelProperty(value = "用户名称", example = "admin@dtstack.com", required = true)
    private String userName;

}
