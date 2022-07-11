package com.dtstack.taier.develop.vo.develop.query;


import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/7 4:50 下午
 */
@Data
public class CatalogueLocationVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "目录名称", example = "test", required = true)
    private String name;

    @ApiModelProperty(value = "目录类型", example = "ResourceManager", required = true)
    private String catalogueType;

    @ApiModelProperty(value = "租户ID", example = "1",  required = true)
    private Long tenantId;
}
