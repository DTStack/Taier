package com.dtstack.engine.sql;

import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.Arrays;
import java.util.List;

/**
 * 表操作类型
 *
 * @author jiangbo
 */
public enum TableOperateEnum {

    // 数据操作
    INSERT("insert", "insert into", 1), INSERT_OVERWRIT("insert_overwrite", "Insert Overwrite Into", 2), SELECT("select", "Select", 3), TRUNCATE("truncate", "Truncate Table", 4), LOAD("load", "Load Data", 5),
    MERGE("merge", "Merge", 6),


    // 结构操作
    CREATE("create", "Create Table", 20), DROP("drop", "Drop Table", 21), SHOW("show", "Show", 22),

    DELETE("delete", "delete data", 30),
    UPDATE("update", "update data", 31),

    // alter
    ALTER("alter", "Alter Table", 41),
    ALTERTABLE_RENAME("altertable_rename", "Rename Table", 42),
    ALTERTABLE_PROPERTIES("altertable_properties", "Alter Properties", 43),
    ALTERTABLE_SERDEPROPERTIES("altertable_serdeproperties", "Alter Serde Properties", 44),
    ALTERTABLE_ADDPARTS("altertable_addparts", "Add Partition", 45),
    ALTERTABLE_ALTERPARTS("altertable_alterparts", "Alter Partition", 46),
    ALTERTABLE_RENAMEPART("altertable_renamepart", "Rename Partition", 47),
    ALTERTABLE_DROPPARTS("altertable_dropparts", "Drop Partitions", 48),
    ALTERTABLE_LOCATION("altertable_location", "Location", 49),
    ALTERTABLE_RENAMECOL("altertable_renamecol", "Rename Column", 50),
    ALTERTABLE_ADDCOLS("altertable_addcols", "Add Column", 51),
    ALTERTABLE_REPLACECOLS("altertable_replacecols", "Replace Column", 52),
    OTHER("other", "other", 53),
    INVALIDATE_TABLE("invalidate_table","invalidate_table",54);

    private String operate;

    private String desc;

    private Integer val;

    TableOperateEnum(String operate, String desc, Integer val) {
        this.operate = operate;
        this.desc = desc;
        this.val = val;
    }

    public String getOperate() {
        return operate;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getVal() {
        return val;
    }

    public static TableOperateEnum getOperateBySqlType(Integer type) {
        TableOperateEnum operateEnum;
        if (SqlType.DROP.getValue().equals(type)) {
            operateEnum = DROP;
        } else if (SqlType.INSERT.getValue().equals(type) || SqlType.INSERT_OVERWRITE.getValue().equals(type)) {
            operateEnum = INSERT;
        } else if (SqlType.TRUNCATE.getValue().equals(type)) {
            operateEnum = TRUNCATE;
        } else if (SqlType.LOAD.getValue().equals(type)) {
            operateEnum = LOAD;
        } else if (SqlType.QUERY.getValue().equals(type)) {
            operateEnum = SELECT;
        } else if (SqlType.CREATE_AS.getValue().equals(type) || SqlType.CREATE.getValue().equals(type)
                || SqlType.CREATE_LIKE.getValue().equals(type)) {
            operateEnum = CREATE;
        } else if (SqlType.getShowType().contains(SqlType.getByValue(type))) {
            operateEnum = SHOW;
        } else if (SqlType.ALTER.getValue().equals(type)) {
            operateEnum = ALTER;
        } else if (SqlType.ALTERTABLE_RENAME.getValue().equals(type)) {
            operateEnum = ALTERTABLE_RENAME;
        } else if (SqlType.ALTERTABLE_PROPERTIES.getValue().equals(type)) {
            operateEnum = ALTERTABLE_PROPERTIES;
        } else if (SqlType.ALTERTABLE_SERDEPROPERTIES.getValue().equals(type)) {
            operateEnum = ALTERTABLE_SERDEPROPERTIES;
        } else if (SqlType.ALTERTABLE_ADDPARTS.getValue().equals(type)) {
            operateEnum = ALTERTABLE_ADDPARTS;
        } else if (SqlType.ALTERTABLE_RENAMEPART.getValue().equals(type)) {
            operateEnum = ALTERTABLE_RENAMEPART;
        } else if (SqlType.ALTERTABLE_DROPPARTS.getValue().equals(type)) {
            operateEnum = ALTERTABLE_DROPPARTS;
        } else if (SqlType.ALTERTABLE_LOCATION.getValue().equals(type)) {
            operateEnum = ALTERTABLE_LOCATION;
        } else if (SqlType.ALTERTABLE_RENAMECOL.getValue().equals(type)) {
            operateEnum = ALTERTABLE_RENAMECOL;
        } else if (SqlType.ALTERTABLE_ADDCOLS.getValue().equals(type)) {
            operateEnum = ALTERTABLE_ADDCOLS;
        } else if (SqlType.ALTERTABLE_REPLACECOLS.getValue().equals(type)) {
            operateEnum = ALTERTABLE_REPLACECOLS;
        } else {
            operateEnum = OTHER;
        }

        return operateEnum;
    }

    public static List<TableOperateEnum> getDDLOperate() {
        return Arrays.asList(
                CREATE,
                ALTER,
                ALTERTABLE_RENAME,
                ALTERTABLE_PROPERTIES,
                ALTERTABLE_SERDEPROPERTIES,
                ALTERTABLE_ADDPARTS,
                ALTERTABLE_RENAMEPART,
                ALTERTABLE_DROPPARTS,
                ALTERTABLE_LOCATION,
                ALTERTABLE_RENAMECOL,
                ALTERTABLE_ADDCOLS,
                ALTERTABLE_REPLACECOLS
        );
    }

    public static List<TableOperateEnum> getDMLOperate() {
        return Arrays.asList(
                INSERT, LOAD, TRUNCATE
        );
    }

    public static TableOperateEnum getOperate(int token) {
        switch (token) {
            case HiveParser.TOK_CREATETABLE:
                return CREATE;
            case HiveParser.TOK_DROPTABLE:
                return DROP;
            case HiveParser.TOK_TRUNCATETABLE:
                return TRUNCATE;
            case HiveParser.TOK_LOAD:
                return LOAD;
            case HiveParser.TOK_SHOWTABLES:
                return SHOW;
            default:
                return SELECT;
        }
    }
}
