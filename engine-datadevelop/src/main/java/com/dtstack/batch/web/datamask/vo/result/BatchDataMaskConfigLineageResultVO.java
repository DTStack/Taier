package com.dtstack.batch.web.datamask.vo.result;

import com.dtstack.batch.web.pager.PageResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("脱敏获取血缘结果信息")
public class BatchDataMaskConfigLineageResultVO {
    @ApiModelProperty(value = "表名称", example = "test")
    private String tableName;

    @ApiModelProperty(value = "脱敏表id", example = "1")
    private Long tableId;

    @ApiModelProperty(value = "表字段名称", example = "col_name")
    private String column;

    @ApiModelProperty(value = "表所属项目id", example = "1")
    private Long belongProjectId;

    @ApiModelProperty(value = "开启/关闭脱敏", example = "0")
    private Integer enable;

    @ApiModelProperty(value = "脱敏id", example = "1")
    private Long configId;

    @ApiModelProperty(value = "项目名称", example = "project_name")
    private String projectName;

    @ApiModelProperty(value = "父血缘结果")
    private PageResult<List<BatchDataMaskConfigLineageResultVO>> parentResult;

    @ApiModelProperty(value = "子血缘结果")
    private PageResult<List<BatchDataMaskConfigLineageResultVO>> childResult;
}
