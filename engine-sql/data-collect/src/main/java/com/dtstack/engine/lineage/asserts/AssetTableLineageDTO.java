package com.dtstack.engine.lineage.asserts;

/**
 * @author chener
 * @Classname AssetTableLineageDTO
 * @Description TODO
 * @Date 2020/11/30 10:21
 * @Created chener@dtstack.com
 */
public class AssetTableLineageDTO {

    private Integer isManual;

    private Long lineageTableId;

    private Long inputTableId;

    private Long tenantId;

    public Integer getIsManual() {
        return isManual;
    }

    public void setIsManual(Integer isManual) {
        this.isManual = isManual;
    }

    public Long getLineageTableId() {
        return lineageTableId;
    }

    public void setLineageTableId(Long lineageTableId) {
        this.lineageTableId = lineageTableId;
    }

    public Long getInputTableId() {
        return inputTableId;
    }

    public void setInputTableId(Long inputTableId) {
        this.inputTableId = inputTableId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
