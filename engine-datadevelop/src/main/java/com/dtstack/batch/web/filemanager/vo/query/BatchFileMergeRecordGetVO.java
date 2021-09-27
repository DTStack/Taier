package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>小文件合并记录根据recordId获取
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并记录根据recordId获取")
public class BatchFileMergeRecordGetVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "治理记录id", example = "1", required = true)
    private Long recordId;
}
