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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("数据源get信息")
public class BatchDataSourceAddVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "上传文件的临时路径")
    private String tmpPath;

    @ApiModelProperty(value = "文件名")
    private String originalFilename;

    @ApiModelProperty(value = "是否复制到生产项目", example = "false")
    private Boolean isCopyToProduceProject;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "数据源名称", example = "link_name", required = true)
    private String dataName;

    @ApiModelProperty(value = "数据源描述", example = "desc")
    private String dataDesc;

    @ApiModelProperty(value = "数据源类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "应用状态 0未启用 1使用中", example = "1", required = true)
    private Integer active;

    @ApiModelProperty(value = "连接状态 0连接丢失，1连接可用", example = "1", required = true)
    private Integer linkState;

    @ApiModelProperty(value = "修改用户id", hidden = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "创建用户id", hidden = true)
    private Long createUserId;

    @ApiModelProperty(value = "修改用户")
    private BatchDataSourceUserVO modifyUser;

    @ApiModelProperty(value = "数据源连接url信息（json格式）", required = true)
    private JSONObject dataJson;

    @ApiModelProperty(value = "string格式数据")
    private String dataJsonString;

    @ApiModelProperty(value = "关联数据源id", example = "1")
    private Long linkSourceId;

    @ApiModelProperty(value = "关联数据源名称", example = "link_name")
    private String linkSourceName;

    @ApiModelProperty(value = "是否为默认数据源", example = "1")
    private Integer isDefault;

    @ApiModelProperty(value = "kerberos配置")
    private Map<String, Object> kerberosConfig;

    @ApiModelProperty(value = "本地kerberos配置")
    private String localKerberosConf;
}
