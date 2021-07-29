package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>小文件合并规则根据ruleId获取
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并规则根据ruleId获取")
public class BatchFileMergeRuleGetVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "治理规则id", example = "1", required = true)
    private Long ruleId;
}
