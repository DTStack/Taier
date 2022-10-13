package com.dtstack.taier.develop.vo.develop.query;


import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/7 4:50 下午
 */
public class CatalogueLocationVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "目录名称", example = "test", required = true)
    private String name;

    @ApiModelProperty(value = "目录类型", example = "ResourceManager", required = true)
    private String catalogueType;

    @ApiModelProperty(value = "租户ID", example = "1", required = true)
    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalogueType() {
        return catalogueType;
    }

    public void setCatalogueType(String catalogueType) {
        this.catalogueType = catalogueType;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
