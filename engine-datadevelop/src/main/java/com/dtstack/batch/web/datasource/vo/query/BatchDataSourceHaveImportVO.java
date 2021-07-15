package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/16 2:57 下午
 */
@Data
@ApiModel("获取所有可引入数据源接口的入参")
public class BatchDataSourceHaveImportVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "uic的租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "每页显示的数量", example = "1", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "模糊搜索条件", example = "db")
    private String search;

    @ApiModelProperty(value = "数据源类型 字符串类型", example = "1")
    private List<Integer> dataTypeCodeList;

    @ApiModelProperty(value = "是否默认数据源", example = "1")
    private Integer isMate;

    @ApiModelProperty(value = "根据名称精确查询", example = "1")
    private String dataName;

}
