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

package com.dtstack.batch.web.security.vo.result;

import com.dtstack.batch.web.role.vo.result.BatchRoleUserResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @author: aka
 * @date: 2021/1/19 15:05
 */
@Data
@ApiModel("安全升级日志")
public class SecurityResultRoleUserAdd {
    @ApiModelProperty(name = "操作人", example = "admin")
    private String operator;

    @ApiModelProperty(name = "操作人在当前app下的userId", example = "1")
    private Long operatorId;

    @ApiModelProperty(name = "操作人在当前app下的租户id", example = "1")
    private Long tenantId;

    @ApiModelProperty(name = "操作人在当前app下的项目id", example = "1")
    private Long projectId;

    @ApiModelProperty(name = "是否不记录日志", example = "false")
    private Boolean ignoreLog;

    @ApiModelProperty(name = "参数列表")
    private Map<String,Object> securityDataMap = new HashMap<>(4);

    @ApiModelProperty(name = "结果")
    private Map<Long, List<BatchRoleUserResultVO>> result;
}