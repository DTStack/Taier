package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取除项目外的所有成员信息")
public class BatchProjectGetUsersNotInProjectVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户名称", example = "ruomu", required = true)
    private String name;
}
