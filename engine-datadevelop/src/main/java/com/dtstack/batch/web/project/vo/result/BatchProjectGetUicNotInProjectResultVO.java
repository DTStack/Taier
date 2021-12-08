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

package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取不在项目的UIC用户")
public class BatchProjectGetUicNotInProjectResultVO  {

    @ApiModelProperty(value = "uic 用户 ID")
    private Long userId;

    @ApiModelProperty(value = "账号名称")
    private String userName;

    @ApiModelProperty(value = "姓名")
    private String fullName;

    @ApiModelProperty(value = "是否是平台管理员")
    private Boolean appRoot;

    @ApiModelProperty(value = "是否是租户管理员")
    private Boolean tenantAdmin;

    @ApiModelProperty(value = "是否是租户所有者")
    private Boolean tenantOwner;

}
