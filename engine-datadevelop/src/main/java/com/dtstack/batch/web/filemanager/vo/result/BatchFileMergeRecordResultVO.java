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

import com.dtstack.batch.web.common.PartitionResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>小文件合并规则记录展示对象
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并记录返回对象")
public class BatchFileMergeRecordResultVO {

    @ApiModelProperty(value = "规则id", example = "1")
    private Long ruleId;

    @ApiModelProperty(value = "项目id", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "表id", example = "1")
    private Long tableId;

    @ApiModelProperty(value = "表名", example = "table_name")
    private String tableName;

    @ApiModelProperty(value = "创建人名称", example = "admin@dtstack.com")
    private String createUser;

    @ApiModelProperty(value = "修改人名称", example = "admin@dtstack.com")
    private String modifyUser;

    @ApiModelProperty(value = "合并日期：天", example = "2020-12-20")
    private String mergeDate;

    @ApiModelProperty(value = "合并时间：时分", example = "12:12")
    private String mergeTime;

    @ApiModelProperty(value = "是否是分区表", example = "false")
    private Boolean isPartition;

    @ApiModelProperty(value = "分区名集合")
    private List<PartitionResultVO> partitionNames;

}
