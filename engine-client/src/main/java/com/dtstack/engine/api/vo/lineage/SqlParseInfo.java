package com.dtstack.engine.api.vo.lineage;


import com.dtstack.engine.api.pojo.lineage.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname SqlInfo
 * @Description 解析sql基本信息对象
 * @Date 2020/10/15 10:58
 * @Created chener@dtstack.com
 */
@ApiModel
public class SqlParseInfo extends BaseParseResult{

    /**
     * 主数据库
     */
    @ApiModelProperty("主数据库")
    private String mainDb;

    /**
     * DDL、DML操作的表对象
     */
    @ApiModelProperty("DDL、DML语句解析出的操作对象")
    private Table mainTable;

    public String getMainDb() {
        return mainDb;
    }

    public void setMainDb(String mainDb) {
        this.mainDb = mainDb;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public void setMainTable(Table mainTable) {
        this.mainTable = mainTable;
    }
}
