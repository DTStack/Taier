package com.dtstack.batch.web.role.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分页查询角色列表信息")
public class BatchRolePageQueryVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "总页数", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "角色名称", example = "name", required = true)
    private String name;
}
