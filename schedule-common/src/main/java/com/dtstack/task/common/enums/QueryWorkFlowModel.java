package com.dtstack.task.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public enum QueryWorkFlowModel {
    /**
     * 1.排除工作流子节点                       flow_job_id = 0
     * 2.只查询工作流子节点                     flow_job_id != 0
     * 3.父子节点都有查                        无参数
     * 4.排除工作流父节点                      task_type != 10
     */

    Eliminate_Workflow_SubNodes(1),
    Only_Workflow_SubNodes(2),
    Full_Workflow_Job(3),
    Eliminate_Workflow_ParentNodes(4);

    private Integer type;

    QueryWorkFlowModel(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
