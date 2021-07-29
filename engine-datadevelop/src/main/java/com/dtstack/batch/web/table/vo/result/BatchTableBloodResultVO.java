package com.dtstack.batch.web.table.vo.result;

import com.dtstack.batch.web.pager.PageResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("表血缘信息")
public class BatchTableBloodResultVO {

    @ApiModelProperty(value = "所属项目 ID", example = "1")
    private Long belongProjectId;

    @ApiModelProperty(value = "数据源ID", example = "3")
    private Long dataSourceId;

    @ApiModelProperty(value = "数据源名称", example = "default")
    private String dataSource;

    @ApiModelProperty(value = "表名称", example = "user")
    private String tableName;

    @ApiModelProperty(value = "表 Id", example = "43")
    private Long tableId;

    @ApiModelProperty(value = "数据源类别", example = "1")
    private Integer dataSourceType;

    @ApiModelProperty(value = "表字段")
    private List<String> columns;

    @ApiModelProperty(value = "父血缘信息")
    private List<BatchTableBloodResultVO> parentTables;

    @ApiModelProperty(value = "子血缘信息")
    private List<BatchTableBloodResultVO> childTables;

    @ApiModelProperty(value = "父血缘信息")
    private PageResult<List<BatchTableBloodResultVO>> parentResult;

    @ApiModelProperty(value = "子血缘信息")
    private PageResult<List<BatchTableBloodResultVO>> childResult;

}
