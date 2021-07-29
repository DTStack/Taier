package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("高级运行sparkSql从引擎执行逻辑返回信息")
public class BatchExecuteSqlParseResultVO {

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "发送到引擎生成的jobid", example = "1")
    private String  jobId;

    @ApiModelProperty(value = "引擎类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "信息", example = "1")
    private String msg;

    @ApiModelProperty(value = "sql文本", example = "1")
    private String sqlText;

    @ApiModelProperty(value = "sql结果id对应的集合")
    private List<BatchSqlResultVO> sqlIdList;
}
