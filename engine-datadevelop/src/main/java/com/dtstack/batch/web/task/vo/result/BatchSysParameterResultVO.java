package com.dtstack.batch.web.task.vo.result;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("系统参数")
public class BatchSysParameterResultVO {

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "参数名称")
    private String paramName;

    @ApiModelProperty(value = "命令")
    private String paramCommand;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted = 0;

}
