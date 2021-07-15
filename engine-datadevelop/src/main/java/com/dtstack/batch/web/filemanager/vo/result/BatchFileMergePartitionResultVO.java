package com.dtstack.batch.web.filemanager.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>小文件合并分区记录前端展示对象
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并分区记录前端展示对象")
public class BatchFileMergePartitionResultVO {

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "分区名")
    private String partitionName;

    @ApiModelProperty(value = "占用存储")
    private String storage;

    @ApiModelProperty(value = "合并状态")
    private Integer status;

    @ApiModelProperty(value = "治理前文件数")
    private Long countBefore;

    @ApiModelProperty(value = "治理后文件数")
    private Long countAfter;

    @ApiModelProperty(value = "合并失败原因")
    private String errorMsg;

}
