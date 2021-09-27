package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("读写锁信息")
public class BatchReadWriteLockGetReadWriteLockVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "关联 ID", example = "3", required = true)
    private Long relationId;

    @ApiModelProperty(value = "类别", example = "1", required = true)
    private String type;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

}
