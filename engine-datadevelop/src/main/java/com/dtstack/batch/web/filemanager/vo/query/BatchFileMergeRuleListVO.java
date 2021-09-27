package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小文件合并规则 根据tenantId 获取当前租户下的
 *
 * @author ：wangchuan
 * date：Created in 3:46 下午 2020/12/15
 * company: www.dtstack.com
 */
@Data
@ApiModel("小文件合并规则根据tenantId 获取当前租户下的所有规则")
public class BatchFileMergeRuleListVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户id", required = true)
    private Long tenantId;
}
