package com.dtstack.engine.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sanyue
 * @date 2018/11/26
 */
public class TableOperateConstrant {

    public static final List<Twins<SqlType, String>> ddlList = new ArrayList<>(Arrays.asList(
            Twins.of(SqlType.DROP, SqlType.DROP.name()),
            Twins.of(SqlType.ALTERTABLE_RENAME, "Rename Table"),
            Twins.of(SqlType.ALTERTABLE_PROPERTIES, "Alter Properties"),
            Twins.of(SqlType.ALTERTABLE_SERDEPROPERTIES, "Altre Serdeproperties"),
            Twins.of(SqlType.ALTERTABLE_ADDPARTS, "Add Partition"),
            Twins.of(SqlType.ALTERTABLE_RENAMEPART, "Rename Partition"),
            Twins.of(SqlType.ALTERTABLE_DROPPARTS, "Drop Partition"),
            Twins.of(SqlType.ALTERTABLE_ADDCOLS, "Add Column")
    ));

    public static final List<Twins<SqlType, String>> ddlListWithOutPartition = new ArrayList<>(Arrays.asList(
            Twins.of(SqlType.DROP, SqlType.DROP.name()),
            Twins.of(SqlType.ALTERTABLE_RENAME, "Rename Table"),
            Twins.of(SqlType.ALTERTABLE_PROPERTIES, "Alter Properties"),
            Twins.of(SqlType.ALTERTABLE_SERDEPROPERTIES, "Altre Serdeproperties"),
            Twins.of(SqlType.ALTERTABLE_ADDCOLS, "Add Column")
    ));

    //SparkSql不支持update和Delete
    public static final List<Twins<SqlType, String>> dmlList = new ArrayList<>(Arrays.asList(
            Twins.of(SqlType.INSERT, SqlType.INSERT.name()),
            Twins.of(SqlType.INSERT_OVERWRITE, SqlType.INSERT_OVERWRITE.name()),
            Twins.of(SqlType.TRUNCATE, SqlType.TRUNCATE.name()),
            Twins.of(SqlType.LOAD, SqlType.LOAD.name())));


    public static final List<SqlType> FREE_TYPE = new ArrayList<>(Arrays.asList(
            SqlType.SHOW_TABLES,
            SqlType.SHOW_CREATETABLE,
            SqlType.SHOW_TBLPROPERTIES,
            SqlType.SHOW_PARTITIONS,
            SqlType.SHOW_COLUMNS,
            SqlType.DESC_TABLE,
            SqlType.EXPLAIN,
            //todo
            SqlType.OTHER
    ));


    public static final List<Twins<SqlType, String>> tablePermissionList = new ArrayList<>(Arrays.asList(
            Twins.of(SqlType.DQL, SqlType.DQL.getType()),
            Twins.of(SqlType.DML, SqlType.DML.getType()),
            Twins.of(SqlType.DDL, SqlType.DDL.getType())
    ));
}

