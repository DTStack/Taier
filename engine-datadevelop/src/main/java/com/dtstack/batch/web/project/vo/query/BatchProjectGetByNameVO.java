package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/2/25 11:53 上午
 */
@Data
@ApiModel("根据名称获取项目接口-入参")
public class BatchProjectGetByNameVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目名称",required = true)
    private String projectName;

}
