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

package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>小文件合并历史查询
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并历史查询")
public class BatchFileMergeRecordQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "查询的项目id", example = "1")
    @JsonProperty("pId")
    private Long pId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "表id", example = "111")
    private Long tableId;

    @ApiModelProperty(value = "表名", example = "test_name")
    private String tableName;

    @ApiModelProperty(value = "操作人id", example = "1")
    private Long operationUserId;

    @ApiModelProperty(value = "记录状态集合")
    private List<Integer> status;

    @ApiModelProperty(value = "规则Id")
    private Long ruleId;

    @ApiModelProperty(value = "规则状态")
    private Integer ruleType;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "每页展示的条数", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "需要排序字段", example = "column_test")
    private String sortColumn;

    @ApiModelProperty(value = "生序or降序", example = "desc")
    private String order;
}
