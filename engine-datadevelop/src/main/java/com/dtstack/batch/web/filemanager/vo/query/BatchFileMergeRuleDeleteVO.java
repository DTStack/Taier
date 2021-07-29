package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>小文件合并取消
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并取消")
public class BatchFileMergeRuleDeleteVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "治理规则id", example = "1", required = true)
    private Long ruleId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "是否是超管", hidden = true)
    private Boolean isRoot;
}
