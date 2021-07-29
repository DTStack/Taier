package com.dtstack.batch.web.server.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
@ApiModel("根据jobId获取日志结果信息")
public class BatchServerLogResultVO {

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType = 0;

    @ApiModelProperty(value = "日志详情", example = "1")
    private String logInfo;

    @ApiModelProperty(value = "日志类型", example = "1")
    private String name;

    @ApiModelProperty(value = "开始时间", example = "2020-07-20 10:50:46")
    private Timestamp execStartTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-20 10:50:46")
    private Timestamp execEndTime;

    @ApiModelProperty(value = "总页数", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex;

    @ApiModelProperty(value = "计算类型", example = "1")
    private Integer computeType = 0;

    @ApiModelProperty(value = "读取数量", example = "1")
    private Integer readNum = 0;

    @ApiModelProperty(value = "写入数量", example = "1")
    private Integer writeNum = 0;

    @ApiModelProperty(value = "目录", example = "0.0")
    private Float dirtyPercent = 0.0F;

    @ApiModelProperty(value = "exe时间", example = "1")
    private Long execTime = 0L;

    @ApiModelProperty(value = "下载日志", example = "1")
    private String downloadLog;

    @ApiModelProperty(value = "sub节点下载日志")
    private Map<String, String> subNodeDownloadLog;
}
