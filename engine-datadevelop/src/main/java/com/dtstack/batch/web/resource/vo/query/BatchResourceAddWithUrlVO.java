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

package com.dtstack.batch.web.resource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加资源路径信息")
public class BatchResourceAddWithUrlVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "租户 ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "资源 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "资源名称", example = "我是资源", required = true)
    private String resourceName;

    @ApiModelProperty(value = "源文件名称", example = "我是源文件名", required = true)
    private String originFileName;

    @ApiModelProperty(value = "资源路径", example = "hdfs://ns1/rdos/batch/***", required = true)
    private String url;

    @ApiModelProperty(value = "资源描述", example = "我是描述", required = true)
    private String resourceDesc;

    @ApiModelProperty(value = "资源类型", example = "1", required = true)
    private Integer resourceType;

    @ApiModelProperty(value = "目录ID", example = "1L", required = true)
    private Long nodePid;
}
