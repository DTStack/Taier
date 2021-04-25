package com.dtstack.engine.api.vo.lineage.param;

import com.dtstack.engine.api.pojo.lineage.Column;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname ParseColumnLineageParam
 * @Description 查询字段血缘关系参数
 * @Date 2020/11/3 17:12
 * @Created chener@dtstack.com
 */
@ApiModel("查询字段血缘参数")
public class ParseColumnLineageParam extends ParseTableLineageParam{

    @ApiModelProperty("表字段map")
    private Map<String, List<Column>> tableColumnsMap;

    public Map<String, List<Column>> getTableColumnsMap() {
        return tableColumnsMap;
    }

    public void setTableColumnsMap(Map<String, List<Column>> tableColumnsMap) {
        this.tableColumnsMap = tableColumnsMap;
    }
}
