package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("小文件合并分区历史查询")
public class BatchWaitMergeTableQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目id", required = true)
    private Long projectId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "文件总数")
    private Long fileCount;

    @ApiModelProperty(value = "文件大小")
    private BigDecimal storage;

    @ApiModelProperty(value = "表名称 模糊查询")
    private String tableName;

    /**
     * 分页条件
     */
    private Integer pageSize = 10;
    private Integer pageIndex = 1;
}
