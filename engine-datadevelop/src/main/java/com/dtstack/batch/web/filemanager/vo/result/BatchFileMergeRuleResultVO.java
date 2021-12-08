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

package com.dtstack.batch.web.filemanager.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * <p>小文件合并规则前端展示对象
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并规则返回对象")
public class BatchFileMergeRuleResultVO {

    /**
     * 治理规则id
     */
    @ApiModelProperty(value = "治理规则id")
    private Long ruleId;

    /**
     * 规则名称
     */
    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id")
    private Long projectId;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 创建人名称
     */
    @ApiModelProperty(value = "创建人名称")
    private String createUser;

    /**
     * 修改人名称
     */
    @ApiModelProperty(value = "修改人名称")
    private String modifyUser;

    /**
     * 周期配置  用于周期性治理 一次性为null
     */
    @ApiModelProperty(value = "周期配置  用于周期性治理 一次性为null")
    private String scheduleConf;

    /**
     * 治理类型 1 周期  2 一次性
     */
    @ApiModelProperty(value = "治理类型 1 周期  2 一次性")
    private Integer mergeType;

    /**
     * 文件数量  治理的下限
     */
    @ApiModelProperty(value = "文件数量  治理的下限")
    private Integer fileCount;

    /**
     * 容量大小 治理的下限
     */
    @ApiModelProperty(value = "容量大小 治理的下限")
    private BigDecimal storage;

    /**
     * 规则状态  是否启动 停止
     */
    @ApiModelProperty(value = "规则状态  是否0启动 1停止")
    private Integer ruleStatus;
}
