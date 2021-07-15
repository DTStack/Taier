package com.dtstack.batch.web.datasource.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("ftp正则匹配查询类")
public class BatchFtpPreQueryVO {

    @ApiModelProperty(value = "自定义参数", example = "1", required = true)
    private List<BatchTaskParamVO> taskParamList = new ArrayList<>();

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "正则表达式 路径+正则表达式", example = "/sss/sss/*.", required = true)
    private String regexStr;
}
