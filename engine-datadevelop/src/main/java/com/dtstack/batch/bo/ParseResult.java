package com.dtstack.batch.bo;

import com.dtstack.engine.api.enums.SqlType;
import com.dtstack.engine.api.pojo.lineage.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author beihai
 * @Description 自定义parseResult，替换com.dtstack.sqlparser.common.client.domain.ParseResult
 * @Date 2021/4/1 19:24
 */
@Data
public class ParseResult  {

    @ApiModelProperty("解析是否成功")
    private boolean parseSuccess = true;
    @ApiModelProperty("失败信息")
    private String failedMsg;
    @ApiModelProperty("格式化后的sql")
    private String standardSql;
    @ApiModelProperty("原始sql")
    private String originSql;
    @ApiModelProperty("sql操作类型")
    private SqlType sqlType;
    @ApiModelProperty("附加sql操作类型")
    private SqlType extraType;
    @ApiModelProperty("当前sql运行数据库")
    private String currentDb;
    @ApiModelProperty("主数据库")
    private String mainDb;
    @ApiModelProperty("DDL、DML语句解析出的操作对象")
    private Table mainTable;
}
