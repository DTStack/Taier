package com.dtstack.batch.web.filemanager.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("文件治理信息")
public class BatchFileMergeRecordQueryResultVO {

    /**
     * 合并记录id
     */
    @ApiModelProperty(value = "合并记录id")
    private Long recordId;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 规则名称
     */
    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    /**
     * 规则类型 一次性 或周期
     */
    @ApiModelProperty(value = "规则类型 一次性 或周期")
    private String ruleType;

    /**
     * 表名
     */
    @ApiModelProperty(value = "表名")
    private String tableName;

    /**
     * 合并计划
     */
    @ApiModelProperty(value = "合并计划")
    private Timestamp planTime;

    /**
     * 合并开始时间
     */
    @ApiModelProperty(value = "合并开始时间")
    private Timestamp startTime;

    /**
     * 合并结束时间
     */
    @ApiModelProperty(value = "合并结束时间")
    private Timestamp endTime;

    /**
     * 操作人名称：这里使用的是修改人
     */
    @ApiModelProperty(value = "操作人名称：这里使用的是修改人")
    private String operationUser;

    /**
     * 是否是分区表
     */
    @ApiModelProperty(value = "是否是分区表")
    private Boolean isPartition;

    /**
     * 合并状态
     */
    @ApiModelProperty(value = "合并状态")
    private Integer status;

    /**
     * 合并失败原因
     */
    @ApiModelProperty(value = "合并失败原因")
    private String errorMsg;

    /**
     * 治理前文件数
     */
    @ApiModelProperty(value = "治理后文件数")
    private Long countBefore;

    /**
     * 治理后文件数
     */
    @ApiModelProperty(value = "治理后文件数")
    private Long countAfter;

}
