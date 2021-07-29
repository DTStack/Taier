package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/2/25 11:53 上午
 */
@Data
@ApiModel("查询Hive表信息-请求VO")
public class BatchHiveTableInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "表名", example = "dev", required = true)
    private String tableName;

    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

}
