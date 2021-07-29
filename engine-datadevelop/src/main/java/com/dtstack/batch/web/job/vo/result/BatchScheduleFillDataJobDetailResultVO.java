package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("补数据返回信息")
public class BatchScheduleFillDataJobDetailResultVO {

    @ApiModelProperty(value = "补数据实例名称", example = "1")
    private String fillDataJobName;

    @ApiModelProperty(value = "补数据记录列表")
    private List<BatchFillDataRecordResultVO> recordList = new ArrayList<>();

}
