package com.dtstack.batch.web.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@ApiModel
public class TreeNodeResultVO {

    @ApiModelProperty("子节点")
    private List<TreeNodeResultVO> children = new LinkedList();

    @ApiModelProperty("节点 ID")
    private String nodeId;

    @ApiModelProperty("父节点 ID")
    private String parentId;

    @ApiModelProperty("绑定数据")
    private Object bindData;

}
