package com.dtstack.batch.domain;

import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class Project extends BaseEntity {

    private String projectIdentifier;

    private String projectName;

    private String projectAlias;

    private String projectDesc;

    private Integer status;

    private Long createUserId;

    /**
     * 修改人id
     */
    private Long modifyUserId;

    private Long tenantId;

    private Integer projectType;

    private Long produceProjectId;

    private Integer scheduleStatus;

    /**
     * 是否允许下载查询结果 1-正常 0-禁用
     */
    private Integer isAllowDownload;

    /**
     * 项目创建人
     */
    private String createUserName;

    private Long catalogueId;

    private Integer alarmStatus;

    public Integer getIsAllowDownload() {
        return isAllowDownload;
    }

    public void setIsAllowDownload(Integer isAllowDownload) {
        this.isAllowDownload = isAllowDownload;
    }
}
