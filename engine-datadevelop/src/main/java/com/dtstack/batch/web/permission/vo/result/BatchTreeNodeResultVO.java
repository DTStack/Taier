package com.dtstack.batch.web.permission.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@ApiModel("权限树返回信息")
public class BatchTreeNodeResultVO {

    @ApiModelProperty(value = "树子结点")
    private List<BatchTreeNodeResultVO> children = new LinkedList<>();

    @ApiModelProperty(value = "树结点 ID", example = "1")
    private String nodeId;

    @ApiModelProperty(value = "父结点 ID", example = "1")
    private String parentId;

    @ApiModelProperty(value = "绑定数据")
    private Object bindData;
}
