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

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>小文件合并规则 添加/更新 对象
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并规则添加或更新")
public class BatchFileMergeRuleAddVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否是超管", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "规则名称", example = "规则1", required = true)
    private String ruleName;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "治理规则id ：ruleId > 0 表示更新操作", example = "1", required = true)
    private Long ruleId = 0L;

    @ApiModelProperty(value = "要治理的项目id", example = "1", required = true)
    @JsonProperty("pId")
    private Long pId;

    @ApiModelProperty(value = "调度配置", example = "{ \"mergeCron\": \"* * * 1 * * ?\",\"periodType\": 1,     \"startNow\":\"false\" }", required = true)
    private String scheduleConf;

    @ApiModelProperty(value = "治理类型 1 周期  2 一次性", example = "1", required = true)
    private Integer mergeType;

    /**
     * 文件数量  治理的下限
     */
    @ApiModelProperty(value = "文件数量  治理的下限", example = "1000", required = true)
    private Integer fileCount;

    /**
     * 容量大小 治理的下限
     */
    @ApiModelProperty(value = "容量大小 治理的下限 单位kb", example = "0.01", required = true)
    private BigDecimal storage;

    @ApiModelProperty(value = "治理的表的信息 一次性治理才会用到这个字段")
    private List<FileMergeTableAddVO> tableInfo;

}
