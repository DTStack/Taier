package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表关系")
public class BatchTableRelationTaskVO  extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称", example = "", required = true)
    private String name;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1", required = true)
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "ql 文本", example = "select * from user", required = true)
    private String sqlText;

    @ApiModelProperty(value = "任务参数", example = "1", required = true)
    private String taskParams;

    @ApiModelProperty(value = "调度配置", example = "{}", required = true)
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "1", required = true)
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "0", required = true)
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提前状态", example = "1", required = true)
    private Integer submitStatus;

    @ApiModelProperty(value = "任务发布状态，前端使用", example = "1")
    private Integer status;

    @ApiModelProperty(value = "最后修改task的用户", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "新建task的用户", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "负责人id", example = "1")
    private Long ownerUserId;

    @ApiModelProperty(value = "task版本", example = "1")
    private Integer version;

    @ApiModelProperty(value = "节点父id", example = "1")
    private Long nodePid;

    @ApiModelProperty(value = "任务描述", example = "测试任务")
    private String taskDesc;

    @ApiModelProperty(value = "入口类", example = "Main.java")
    private String mainClass;

    @ApiModelProperty(value = "运行参数", example = "1,3,7")
    private String exeArgs;

    @ApiModelProperty(value = "所属工作流id", example = "3")
    private Long flowId = 0L;

    @ApiModelProperty(value = "是否过期", example = "1")
    private Integer isExpire;

}
