package com.dtstack.engine.api.vo.lineage;

import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.api.pojo.lineage.TableLineage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author chener
 * @Classname TableLineageInfo
 * @Description 表级血缘解析结果对象
 * @Date 2020/10/15 11:05
 * @Created chener@dtstack.com
 */
@ApiModel
public class TableLineageParseInfo extends SqlParseInfo {

    /**
     * 表级血缘  包含数据库 表
     */
    @ApiModelProperty("表级血缘")
    private List<TableLineage> tableLineages;

    /**
     * sql中涉及到的表
     */
    @ApiModelProperty("sql中使用到的表")
    private List<Table> tables;

    public List<TableLineage> getTableLineages() {
        return tableLineages;
    }

    public void setTableLineages(List<TableLineage> tableLineages) {
        this.tableLineages = tableLineages;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
