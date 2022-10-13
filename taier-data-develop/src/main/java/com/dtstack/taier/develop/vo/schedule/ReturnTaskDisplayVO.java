package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 11:27 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnTaskDisplayVO {

    /**
     * 方向 0 向上 1 向下
     */
    @ApiModelProperty(value = "查询方向:\n" +
            "FATHER(1):向上查询 \n" +
            "CHILD(2):向下查询", example = "1")
    private Integer directType;

    /**
     * 顶节点（就是vo传过来的节点）
     */
    @ApiModelProperty(value = "顶节点（就是vo传过来的节点）")
    private TaskNodeVO rootTaskNode;

    public Integer getDirectType() {
        return directType;
    }

    public void setDirectType(Integer directType) {
        this.directType = directType;
    }

    public TaskNodeVO getRootTaskNode() {
        return rootTaskNode;
    }

    public void setRootTaskNode(TaskNodeVO rootTaskNode) {
        this.rootTaskNode = rootTaskNode;
    }
}
