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

package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取ftp根据正则表达式的匹配结果")
public class BatchFtpPreResultVO {

    /**
     * 查询出的 前20条
     */
    @ApiModelProperty(value = "查询出的文件名称列表", example = "sss,ddd")
    private List<String> fileNameList;

    /**
     * 匹配的条数  这里取巧  最多返回101 如果是101前端就展示超过100条
     */
    @ApiModelProperty(value = "匹配的条数", example = "11")
    private Integer number;
}
