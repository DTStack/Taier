package com.dtstack.batch.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author jiangbo
 * @date 2018/5/24 10:11
 */
@Data
public class BatchApplyVO {

    private Long applyId;

    private Long resourceId;

    private String resourceName;

    private String projectName;

    private String projectAlias;

    private Integer resourceType;

    private Timestamp applyTime;

    private String applyUser;

    private Integer applyStatus;

    private Integer day;

    private String applyReason;

    private String reply;

    private String dealUser;

    private Integer isCancel;

    private Integer isRevoke;

    private Integer resourceIsDeleted;

}
