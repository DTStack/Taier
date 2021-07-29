package com.dtstack.batch.web.testproduct.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发布包信息")
public class BatchPackageItemStatusResultVO {

    @ApiModelProperty(value = "总数", example = "8")
    private Integer count;

    @ApiModelProperty(value = "成功数量", example = "10")
    private Integer successCount;

    @ApiModelProperty(value = "失败数量", example = "13")
    private Integer failCount;

    @ApiModelProperty(value = "等待完成数量", example = "14")
    private Integer waitPublishCount;

}
