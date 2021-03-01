package com.dtstack.engine.api.vo.lineage;

/**
 * sql操作类型
 *
 * @author jiangbo
 */
public enum SqlType {

    // 数据库操作
    DATABASE_OPERATE("databases_operate", 1),

    // 函数操作
    FUNCTION_OPERATE("function_operate", 21),
    CREATE_FUNCTION("create_function",22),
    DROP_FUNCTION("drop_function",23),
    RELOAD_FUNCTION("reload_function",24),
    SHOW_FUNCTION("show_function",25),

    // 数据操作
    INSERT("insert", 41),INSERT_OVERWRITE("insert_overwrite", 42),
    TRUNCATE("truncate", 43), QUERY("select", 44),UPDATE("update", 45),DELETE("delete", 46),LOAD("load", 47),
    QUERY_NO_FROM("xxx",48),WITH_QUERY("with_query",49),

    // 结构操作
    CREATE("create", 101),CREATE_LIKE("create_like", 102),CREATE_AS("create_as", 103),
    DROP("drop", 104),

    ALTER("alter", 105),
    COMMENT_ON("comment_on",200),
    ALTERTABLE_RENAME("altertable_rename", 106),
    ALTERTABLE_PROPERTIES("altertable_properties", 107),
    ALTERTABLE_SERDEPROPERTIES("altertable_serdeproperties", 108),
    ALTERTABLE_ADDPARTS("altertable_addparts", 109),
    ALTERTABLE_RENAMEPART("altertable_renamepart", 110),
    ALTERTABLE_DROPPARTS("altertable_dropparts", 111),
    ALTERTABLE_LOCATION("altertable_location", 112),
    ALTERTABLE_RENAMECOL("altertable_renamecol", 113),
    ALTERTABLE_ADDCOLS("altertable_addcols", 114),
    ALTERTABLE_REPLACECOLS("altertable_replacecols", 115),

    SHOW_TABLES("show_tables", 171),SHOW_CREATETABLE("show_createtable", 172),SHOW_TBLPROPERTIES("show_tblproperties", 173),
    SHOW_PARTITIONS("show_partitions", 174),SHOW_COLUMNS("show_columns", 175),DESC_TABLE("desc_table", 176),SHOW("show",180),

    EXPLAIN("explain", 177),
    /**
     * 增加创建临时表类型
     */
    CREATE_TEMP("create_temp",200),

    INVALIDATE_TABLE("invalidate_table",300),

    /**
     * 其他类型
     */
    OTHER("other", 500),

    /**
     * 切换数据库
     */
    USE_DB("use_db",501),
    /**
     * 未知类型
     */
    UNKNOWN("unknown",404)
    ;

    private String type;

    private Integer value;

    SqlType(String type, Integer value) {
        this.type = type;
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public static SqlType getByValue(Integer value) {
        for (SqlType sqlType : SqlType.values()) {
            if (sqlType.getValue().equals(value)) {
                return sqlType;
            }
        }
        return null;
    }
}
