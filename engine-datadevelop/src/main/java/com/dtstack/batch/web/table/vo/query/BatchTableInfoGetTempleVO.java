package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表基本信息")
public class BatchTableInfoGetTempleVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "表类别", example = "1", required = true)
    private Integer tableType;

}
