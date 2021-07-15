package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>小文件合并分区历史查询
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并分区历史查询")
public class BatchFileMergePartitionQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "记录id", required = true, example = "1")
    private Long recordId;

    @ApiModelProperty(value = "分区名称 模糊查询", example = "desc")
    private String partitionName;

    @ApiModelProperty(value = "记录状态集合")
    private List<Integer> status;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "每页展示的条数", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "需要排序字段", example = "column_test")
    private String sortColumn;

    @ApiModelProperty(value = "生序or降序", example = "desc")
    private String order;
}
