package com.dtstack.engine.api.vo.lineage;

import com.dtstack.engine.api.pojo.lineage.ColumnLineage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author chener
 * @Classname ColumnLineageInfo
 * @Description 字段级血缘解析结果
 * @Date 2020/10/15 11:11
 * @Created chener@dtstack.com
 */
@ApiModel("字段级血缘结果对象")
public class ColumnLineageParseInfo extends TableLineageParseInfo{
    /**
     * 字段血缘，包含数据库，表，字段三部分
     *
     */
    @ApiModelProperty("字段级血缘关系列表")
    private List<ColumnLineage> columnLineages;

    public List<ColumnLineage> getColumnLineages() {
        return columnLineages;
    }

    public void setColumnLineages(List<ColumnLineage> columnLineages) {
        this.columnLineages = columnLineages;
    }
}
