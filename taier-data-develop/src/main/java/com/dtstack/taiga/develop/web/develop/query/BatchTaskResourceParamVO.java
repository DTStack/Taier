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

package com.dtstack.taiga.develop.web.develop.query;

import com.dtstack.taiga.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("任务信息")
public class BatchTaskResourceParamVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "")
    private List<Long> resourceIdList;

    @ApiModelProperty(value = "")
    private List<Long> refResourceIdList;

    @ApiModelProperty(value = "", example = "false")
    private Boolean preSave = false;

    @ApiModelProperty(value = "")
    private Map<String, Object> sourceMap;

    @ApiModelProperty(value = "")
    private Map<String, Object> targetMap;

    @ApiModelProperty(value = "设置")
    private Map<String, Object> settingMap;

    @ApiModelProperty(value = "依赖任务信息")
    private List<BatchTaskBaseVO> dependencyTasks;

    @ApiModelProperty(value = "发布描述", example = "test")
    private String publishDesc;

    @ApiModelProperty(value = "锁版本 ID", example = "1")
    private Integer lockVersion = 0;

    @ApiModelProperty(value = "是否覆盖更新", example = "false")
    private Boolean forceUpdate = false;

    @ApiModelProperty(value = "任务版本 ID")
    private List<Map> taskVariables;

    @ApiModelProperty(value = "数据源 ID", example = "24")
    private Long dataSourceId;

    @ApiModelProperty(value = "0-向导模式,1-脚本模式", example = "1")
    private Integer createModel = 0;

    @ApiModelProperty(value = "操作模式 0-资源模式，1-编辑模式D", example = "0")
    private Integer operateModel = 1;

    @ApiModelProperty(value = "同步模式 0-无增量标识，1-有增量标识", example = "1")
    private Integer syncModel = 0;

    @ApiModelProperty(value = "2-python2.x,3-python3.xD", example = "2")
    private Integer pythonVersion = 0;

    @ApiModelProperty(value = "0-TensorFlow,1-MXNet", example = "1")
    private Integer learningType = 0;

    @ApiModelProperty(value = "输入数据文件的路径", example = "")
    private String input;

    @ApiModelProperty(value = "输出模型的路径", example = "")
    private String output;

    @ApiModelProperty(value = "脚本的命令行参数", example = "")
    private String options;

    @ApiModelProperty(value = "任务流中待更新的子任务D")
    private List<BatchTaskResourceParamVO> toUpdateTasks;

    @ApiModelProperty(value = "是否是右键编辑任务", example = "false")
    private Boolean isEditBaseInfo = false;

    @ApiModelProperty(value = "工作流父任务版本号  用于子任务获取父任务锁", example = "43")
    private Integer parentReadWriteLockVersion ;

    @ApiModelProperty(value = "读写锁", example = "")
    private BatchReadWriteLockBaseVO readWriteLockVO;

    @ApiModelProperty(value = "任务名称", example = "test")
    private String name;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "0", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1", required = true)
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本", example = "show tables", required = true)
    private String sqlText;

    @ApiModelProperty(value = "任务参数", example = "{}")
    private String taskParams;

    @ApiModelProperty(value = "调度配置", example = "")
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "0")
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "1", required = true)
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "0")
    private Integer submitStatus;

    @ApiModelProperty(value = "任务发布状态，前端使用D", example = "1")
    private Integer status;

    @ApiModelProperty(value = "最后修改task的用户", example = "3")
    private Long modifyUserId;
    
    @ApiModelProperty(value = "新建task的用户", example = "3")
    private Long createUserId;
    
    @ApiModelProperty(value = "负责人id", example = "111")
    private Long ownerUserId;
    
    @ApiModelProperty(value = "任务版本 ID", example = "14")
    private Integer version;

    @ApiModelProperty(value = "节点 ID", example = "7")
    private Long nodePid;
    
    @ApiModelProperty(value = "任务描述", example = "tes")
    private String taskDesc;
    
    @ApiModelProperty(value = "入口类", example = "1")
    private String mainClass;

    @ApiModelProperty(value = "参数 ID", example = "1")
    private String exeArgs;
  
    @ApiModelProperty(value = " 所属工作流id", example = "1")
    private Long flowId = 0L;
    
    @ApiModelProperty(value = "是否过期", hidden = true)
    private Integer isExpire;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID",  hidden = true)
    private Long projectId;
   
    @ApiModelProperty(value = "平台类别", hidden = true)
    private Integer appType;

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id = 0L;
    
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;
    
    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;
    
    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "组件版本号", example = "111")
    private String componentVersion;

}
