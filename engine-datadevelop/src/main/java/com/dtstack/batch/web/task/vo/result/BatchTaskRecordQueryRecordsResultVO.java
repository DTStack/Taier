package com.dtstack.batch.web.task.vo.result;

import com.dtstack.batch.web.task.vo.result.BatchTaskRecordResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务操作信息")
public class BatchTaskRecordQueryRecordsResultVO {

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage = 0;

    @ApiModelProperty(value = "数据")
    private List<BatchTaskRecordResultVO>  data;

    @ApiModelProperty(value = "每页展示条数", example = "10")
    private Integer pageSize = 0;

    @ApiModelProperty(value = "总条数", example = "98")
    private Integer totalCount = 0;

    @ApiModelProperty(value = "总页数", example = "9")
    private Integer totalPage = 0;

}
