package com.dtstack.batch.web.testproduct.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发布包信息")
public class BatchPackageUploadPackageResultVO {

    @ApiModelProperty(value = "发布包检查状态", example = "true")
    private Boolean checkStatus;

    @ApiModelProperty(value = "提示", example = "tip")
    private String message;

    @ApiModelProperty(value = "fileKey 用于寻找上传的压缩包", example = "7b3bcd4f-fa57-4b04-bf75-85fe44da454b ")
    private String fileKey;

    @ApiModelProperty(value = "发布包名称", example = "BP_2020_12_12_5863142ce")
    private String packageName;

}
