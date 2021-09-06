package com.dtstack.batch.web.download.vo;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("下载KerberosXML信息")
public class BatchDownloadKerberosVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "数据源类型", example = "1", required = true)
    private Integer sourceType;
}
