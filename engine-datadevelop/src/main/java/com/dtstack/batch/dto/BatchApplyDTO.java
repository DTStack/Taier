package com.dtstack.batch.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author jiangbo
 * @date 2018/5/24 11:27
 */
@Data
public class BatchApplyDTO {

    private Long tenantId;

    private List<Long> projectIds;

    private List<Integer> statusList;

    private Long userId;

    private Long dealUserId;

    private Integer isCancel;

    private Integer isRevoke;

    private String resourceName;

    private Timestamp startTime;

    private Timestamp endTime;

    private Integer isDeleted;

    private Integer applyResourceType;

    private Long excludeUserId;

    private Integer tableType;


    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

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
