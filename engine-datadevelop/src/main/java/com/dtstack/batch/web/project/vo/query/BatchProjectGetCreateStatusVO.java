package com.dtstack.batch.web.project.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取项目创建状态信息")
public class BatchProjectGetCreateStatusVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目ID列表",required = true)
    private List<Long> projectIdList;
}
