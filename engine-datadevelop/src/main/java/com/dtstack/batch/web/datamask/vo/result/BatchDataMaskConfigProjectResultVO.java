package com.dtstack.batch.web.datamask.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏管理所属项目结果信息")
public class BatchDataMaskConfigProjectResultVO {
    @ApiModelProperty(value = "项目id")
    private Long pjId;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;
}
