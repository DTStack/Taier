package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("表收藏信息")
public class BatchTableCollectionVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "表 ID", hidden = true)
    private Long tableId;

    @ApiModelProperty(value = "id", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;
}
