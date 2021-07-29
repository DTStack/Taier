package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("发布包信息")
public class BatchPackagePageQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "展示条数", example = "10", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页数", example = "1", required = true)
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "排序字段", example = "gmt_create")
    private String orderBy = "gmt_create";

    @ApiModelProperty(value = "排序方式", example = "desc")
    private String sort = "desc";

    @ApiModelProperty(value = "发布用户 ID", example = "3")
    private Long publishUserId;

    @ApiModelProperty(value = "", hidden = true)
    private Long applyUserId;

    @ApiModelProperty(value = "发布日期 开始", example = "1609381335537", required = true)
    private Timestamp publishTimeStart;

    @ApiModelProperty(value = "发布日期 截止", example = "1609381455537", required = true)
    private Timestamp publishTimeEnd;

    @ApiModelProperty(value = "发布日期 开始", example = "1609381335537", required = true)
    private Timestamp applyTimeStart;

    @ApiModelProperty(value = "发布日期 截止", example = "1609381455537", required = true)
    private Timestamp applyTimeEnd;

    @ApiModelProperty(value = "发布包名称", example = "test")
    private String packageName;

    @ApiModelProperty(value = "发布状态", example = "0")
    private Integer status;

    @ApiModelProperty(value = "发布包类别", example = "1")
    private Integer packageType;
}
