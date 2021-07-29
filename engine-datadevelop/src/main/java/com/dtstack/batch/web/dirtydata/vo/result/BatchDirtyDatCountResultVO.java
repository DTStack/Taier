package com.dtstack.batch.web.dirtydata.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据count结果信息")
public class BatchDirtyDatCountResultVO {
    @ApiModelProperty(value = "总数", example = "99")
    private Long totalNum;

    @ApiModelProperty(value = "npe错误数量", example = "10")
    private Long npe;

    @ApiModelProperty(value = "duplicate错误数量", example = "5")
    private Long duplicate;

    @ApiModelProperty(value = "conversion错误数量", example = "3")
    private Long conversion;

    @ApiModelProperty(value = "other错误数量", example = "3")
    private Long other;

}