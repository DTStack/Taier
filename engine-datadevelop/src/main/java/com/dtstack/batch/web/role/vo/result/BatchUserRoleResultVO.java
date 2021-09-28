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

package com.dtstack.batch.web.role.vo.result;

import com.dtstack.batch.web.user.vo.result.BatchUserBaseResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("用户角色返回信息")
public class BatchUserRoleResultVO {

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "用户信息")
    private BatchUserBaseResultVO user;

    @ApiModelProperty(value = "角色")
    private List<BatchRoleUpdateResultVO> roles = new ArrayList<>();

    @ApiModelProperty(value = "是否安全", example = "0")
    private Integer isSelf = 0;

    @ApiModelProperty(value = "加入项目时间", example = "2020-12-28 09:22:03")
    private Timestamp gmtCreate;
}
