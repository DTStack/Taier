package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("脱敏开启/关闭信息")
public class BatchDataMaskConfigEnableVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "脱敏id列表", required = true)
    private List<Long> ids;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "开启/关闭脱敏 0-正常 1-禁用", example = "0", required = true)
    private Integer enable = 0;
}
