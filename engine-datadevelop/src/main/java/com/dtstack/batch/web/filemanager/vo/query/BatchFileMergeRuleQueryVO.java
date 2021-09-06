package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>小文件合并记录查询
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并记录查询")
public class BatchFileMergeRuleQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "规则名", example = "test_name")
    private String ruleName;

    @ApiModelProperty(value = "规则创建人id", example = "1")
    private Long ruleCreateUserId;

    @ApiModelProperty(value = "查询的项目id", example = "1")
    @JsonProperty("pId")
    private Long pId;

    @ApiModelProperty(value = "规则状态 0启动 1停止", example = "1")
    private Integer ruleStatus;

    @ApiModelProperty(value = "治理方式 1 周期  2 一次性", example = "1")
    private Integer mergeType = 1;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "每页展示的条数", example = "10")
    private Integer pageSize;

}
