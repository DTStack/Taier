package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author jiangbo
 * @time 2018/5/19
 */
@Data
public class BatchApply extends BaseEntity {

    private Long tenantId;

    /**
     * 申请用户
     */
    private Long userId;

    /**
     * 申请的资源类型 0-hive表，1-函数,2-资源
     */
    private Integer applyResourceType;

    /**
     * 资源所属项目
     */
    private Long projectId;

    /**
     * 资源id
     */
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源申请期限
     */
    private Integer day;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 申请状态
     */
    private Integer status;

    /**
     * 处理人
     */
    private Long dealUserId;

    /**
     * 回复内容
     */
    private String reply;

    /**
     * 是否取消
     */
    private Integer isCancel;

    /**
     * 是否回收
     */
    private Integer isRevoke;

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getIsRevoke() {
        return isRevoke;
    }

    public void setIsRevoke(Integer isRevoke) {
        this.isRevoke = isRevoke;
    }

}
