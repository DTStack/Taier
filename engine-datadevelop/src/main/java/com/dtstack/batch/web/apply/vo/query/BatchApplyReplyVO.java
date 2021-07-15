package com.dtstack.batch.web.apply.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("申请回复信息")
public class BatchApplyReplyVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "申请id列表", required = true)
    private List<Long> ids;

    @ApiModelProperty(value = "回复内容", example = "reply", required = true)
    private String reply;

    @ApiModelProperty(value = "申请状态 0-待审批，1-通过，2-不通过", example = "0", required = true)
    private Integer status;

    @ApiModelProperty(value = "申请用户id", example = "1", required = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", example = "1", required = true)
    private Long tenantId;

    @ApiModelProperty(value = "是否为root", example = "false")
    private  Boolean isRoot;
}
