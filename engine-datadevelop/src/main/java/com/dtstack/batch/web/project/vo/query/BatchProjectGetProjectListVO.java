package com.dtstack.batch.web.project.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据项目名/别名分页查询信息")
public class BatchProjectGetProjectListVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "模糊名称", example = "模糊名称", required = true)
    private String fuzzyName;

    @ApiModelProperty(value = "项目类型", example = "1", required = true)
    private Integer projectType;

    @ApiModelProperty(value = "排序规则", example = "rps.stick")
    private String orderBy;

    @ApiModelProperty(value = "排序", example = "desc")
    private String sort;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer page;

    @ApiModelProperty(value = "总页数", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "目录ID", example = "1", required = true)
    private Long catalogueId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isAdmin;
}
