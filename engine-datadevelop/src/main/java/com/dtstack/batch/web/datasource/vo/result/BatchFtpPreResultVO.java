package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取ftp根据正则表达式的匹配结果")
public class BatchFtpPreResultVO {

    /**
     * 查询出的 前20条
     */
    @ApiModelProperty(value = "查询出的文件名称列表", example = "sss,ddd")
    private List<String> fileNameList;

    /**
     * 匹配的条数  这里取巧  最多返回101 如果是101前端就展示超过100条
     */
    @ApiModelProperty(value = "匹配的条数", example = "11")
    private Integer number;
}
