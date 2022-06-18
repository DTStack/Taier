package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：zhichen
 * @Date ：Created in 10:50 2021/9/11
 */
@Data
public class TaskDirtyDataManageQueryVO extends DtInsightAuthParam {

    @NotNull(message = "sourceId not null")
    @ApiModelProperty(value = "taskId", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "项目Id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "脏数据表")
    private List<String> sourceTableList;

    @ApiModelProperty(value = "开始时间", example = "1231242")
    private Long startDate;

    @ApiModelProperty(value = "结束时间", example = "12314214")
    private Long endDate;

    @ApiModelProperty(value = "当前页", example = "1")
    private int currentPage = 1;

    @ApiModelProperty(value = "页面大小", example = "20")
    private int pageSize = 20;
}
