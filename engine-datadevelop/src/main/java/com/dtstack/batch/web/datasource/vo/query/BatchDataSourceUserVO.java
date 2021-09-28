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

package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("数据源用户信息")
public class BatchDataSourceUserVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户名称", example = "admin", required = true)
    private String userName;

    @ApiModelProperty(value = "电话号码", example = "18888888888", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "dtuic用户id", hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "test@dtstack.com", required = true)
    private String email;

    @ApiModelProperty(value = "状态", example = "1", required = true)
    private Integer status;

    @ApiModelProperty(value = "默认项目id", hidden = true)
    private Long defaultProjectId;

    @ApiModelProperty(value = "整库同步id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1", required = true)
    private Integer isDeleted = 0;
}
