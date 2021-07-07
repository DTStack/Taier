package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表字段信息")
public class BatchColumnVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "编号", example = "1")
    private Long id;

    @ApiModelProperty(value = "字段名", example = "name", required = true)
    private String columnName;

    @ApiModelProperty(value = "字段类型", example = "id", required = true)
    private String columnType;

    @ApiModelProperty(value = "备注", example = "用户编号", required = true)
    private String comment;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer index;

    @ApiModelProperty(value = "精度", example = "4")
    private Integer precision;

    @ApiModelProperty(value = "保留位数", example = "1")
    private Integer scale;

    @ApiModelProperty(value = "删除标识", example = "0", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "字符长度", example = "12", hidden = true)
    private String charLen;

}
