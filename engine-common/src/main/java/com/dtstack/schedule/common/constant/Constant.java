package com.dtstack.schedule.common.constant;

public final class Constant {
    public static final int TIMEOUT_SECONDS = 5;
    public static final int CONNECT_TIMEOUT = 6000;
    public static final int MAX_TRY_TIMES = 4;
    public static final int SOCKET_TIMEOUT_INSECOND = 10;

    public static final int CREATE_MODEL_GUIDE = 0;
    public static final int CREATE_MODEL_TEMPLATE = 1;

    public static final int OPERATE_MODEL_EDIT = 1;
    public static final int OPERATE_MODEL_RESOURCE = 0;

    /**用于在插入数据的时候出现冲突的时候重试, 超过设定次数很可能是数据本身问题而不是并发问题*/
    public static final int INSERT_RETRY_NUM_LIMIT  = 5;

    public static final String MYSQL_DATABASE = "Unknown database";
    public static final String MYSQL_CONNEXP = "Communications link failure";
    public static final String MYSQL_ACCDENIED = "Access denied";
    public static final String MYSQL_TABLE_NAME_ERR1 = "Table";
    public static final String MYSQL_TABLE_NAME_ERR2 = "doesn't exist";
    public static final String MYSQL_SELECT_PRI = "SELECT command denied to user";
    public static final String MYSQL_COLUMN1 = "Unknown column";
    public static final String MYSQL_COLUMN2 = "field list";
    public static final String MYSQL_WHERE = "where clause";

    public static final String ORACLE_DATABASE = "ORA-12505";
    public static final String ORACLE_CONNEXP = "The Network Adapter could not establish the connection";
    public static final String ORACLE_ACCDENIED = "ORA-01017";
    public static final String ORACLE_TABLE_NAME = "table or view does not exist";
    public static final String ORACLE_SELECT_PRI = "insufficient privileges";
    public static final String ORACLE_SQL = "invalid identifier";



}
