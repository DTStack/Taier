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

import java.util.List;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description hive数据源-同步元数据信息，获取所有需要添加和删除的表名称。
 * @date 2021/3/5 10:26 上午
 */
@Data
@ApiModel("获取hive元数据同步需要添加和删除的表")
public class BatchCompareIntrinsicTableResultVO {

    @ApiModelProperty(value = "需要添加的表名")
    private List<String> addTablesName;

    @ApiModelProperty(value = "需要删除的表名")
    private List<String> dropTablesName;

    @ApiModelProperty(value = "生命周期，单位：day", example = "7")
    private Integer lifecycle;

}
