package com.dtstack.engine.api.vo.lineage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageTableInfoVO
 * @Description 表信息
 * @Date 2020/10/30 10:25
 * @Created chener@dtstack.com
 */
@ApiModel("表信息")
public class LineageTableVO {

    /**
     * 表id
     */
    @ApiModelProperty("表id")
    private Long tableId;

    /**
     * 表名
     */
    @ApiModelProperty("表名")
    private String tableName;

    /**
     * 数据源信息
     */
    @ApiModelProperty("数据源信息")
    private LineageDataSourceVO dataSourceVO;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LineageDataSourceVO getDataSourceVO() {
        return dataSourceVO;
    }

    public void setDataSourceVO(LineageDataSourceVO dataSourceVO) {
        this.dataSourceVO = dataSourceVO;
    }
}
