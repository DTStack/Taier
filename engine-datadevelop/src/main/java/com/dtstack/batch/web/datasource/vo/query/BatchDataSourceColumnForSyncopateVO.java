package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 返回切分键需要的列名
 *
 * @author Ruomu[ruomu@dtstack.com]
 * @Data 2021/1/12 17:19
 */
@Data
@ApiModel("数据同步-返回切分键需要的列名")
public class BatchDataSourceColumnForSyncopateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "表名称", required = true)
    private String tableName;

    @ApiModelProperty(value = "查询的schema", example = "test")
    private String schema;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
