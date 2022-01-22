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

package com.dtstack.taiga.develop.web.user.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("返回用户信息")
public class BatchUserBaseResultVO {

    @ApiModelProperty(value = "用户姓名", example = "ruomu")
    private String userName;

    @ApiModelProperty(value = "电话号码", example = "110")
    private String phoneNumber;

    @ApiModelProperty(value = "uic 用户 ID", example = "32")
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "zhangsan@dtstack.com")
    private String email;

    @ApiModelProperty(value = "状态", example = "0")
    private Integer status;

    @ApiModelProperty(value = "默认项目ID", example = "3")
    private Long defaultProjectId;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-28 09:22:03")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-28 09:22:03")
    private Timestamp gmtModified;
}
