package com.dtstack.batch.web.filemanager.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("小文件合并规则添加一次性治理 表信息选择")
public class FileMergeTableAddVO {

    @ApiModelProperty(value = "表id hive_table_info表id", hidden = true)
    private Long tableId;

    @ApiModelProperty(value = "选择治理的 分区的id", hidden = true)
    private List<Long> partitionIds;
}
