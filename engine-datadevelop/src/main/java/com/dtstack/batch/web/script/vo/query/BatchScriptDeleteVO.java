package com.dtstack.batch.web.script.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除脚本信息")
public class BatchScriptDeleteVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "脚本ID", example = "3", required = true)
    private Long scriptId;
}
