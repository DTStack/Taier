package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("读写锁信息")
public class BatchReadWriteLockGetLockVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "类别", example = "1", required = true)
    private String type;

    @ApiModelProperty(value = "文件 ID", example = "132412", required = true)
    private Long fileId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "锁版本", example = "2")
    private Integer lockVersion;

    @ApiModelProperty(value = "依赖文件ID")
    private List<Long> subFileIds;

}
