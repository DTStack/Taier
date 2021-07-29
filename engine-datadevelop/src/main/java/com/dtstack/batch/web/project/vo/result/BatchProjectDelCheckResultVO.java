package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("删除项目预检查结果")
public class BatchProjectDelCheckResultVO {

    @ApiModelProperty(value = "错误信息", example = "error")
    private String errorMsg;


    @ApiModelProperty(value = "检查任务结果")
    private List<BatchProjectDelCheckTaskResultVO> taskList = new ArrayList<>();
}
