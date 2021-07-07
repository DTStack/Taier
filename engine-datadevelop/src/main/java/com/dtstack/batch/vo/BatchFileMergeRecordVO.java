package com.dtstack.batch.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * <p>小文件合并表历史前端展示VO</>
 *
 * @author ：wangchuan
 * date：Created in 11:42 上午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchFileMergeRecordVO {

    /**
     * 合并记录id
     */
    private Long recordId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型 一次性 或周期
     */
    private String ruleType;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 合并计划
     */
    private Timestamp planTime;

    /**
     * 合并开始时间
     */
    private Timestamp startTime;

    /**
     * 合并结束时间
     */
    private Timestamp endTime;

    /**
     * 操作人名称：这里使用的是修改人
     */
    private String operationUser;

    /**
     * 是否是分区表
     */
    private Boolean isPartition;

    /**
     * 合并状态
     */
    private Integer status;

    /**
     * 合并失败原因
     */
    private String errorMsg;

    /**
     * 治理前文件数
     */
    private Long countBefore;

    /**
     * 治理后文件数
     */
    private Long countAfter;

}
