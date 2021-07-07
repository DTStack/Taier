package com.dtstack.batch.vo;

import lombok.Data;

/**
 * @author jiangbo
 * @time 2017/12/11
 */
@Data
public class BatchTableRelationVO {

    private Long relationId;

    private String name;

    private String createUser;

    private Integer taskType = 0;

    private Integer scriptType = 0;

    /**
     * 所属项目
     */
    private String projectName;

    private Long projectId;

    /**
     * 是否有权限 0-否，1-是
     */
    private Integer isPermissioned;

    public BatchTableRelationVO() {
    }

    public BatchTableRelationVO(Long relationId,String name,String createUser,int taskType,int scriptType) {
        this.relationId = relationId;
        this.name = name;
        this.createUser = createUser;
        this.taskType=taskType;
        this.scriptType = scriptType;
    }
}
