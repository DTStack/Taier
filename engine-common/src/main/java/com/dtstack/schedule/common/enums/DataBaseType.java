package com.dtstack.schedule.common.enums;


import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * refer:http://blog.csdn.net/ring0hx/article/details/6152528
 * <p/>
 */
public enum DataBaseType {
    MySql("mysql", "com.mysql.jdbc.Driver"),
    TDDL("mysql", "com.mysql.jdbc.Driver"),
    DRDS("drds", "com.mysql.jdbc.Driver"),
    Oracle("oracle", "oracle.jdbc.OracleDriver"),
    SQLServer("sqlserver", "net.sourceforge.jtds.jdbc.Driver"),
    SQLSSERVER_2017_LATER("sqlserver_2017_later", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    PostgreSQL("postgresql", "org.postgresql.Driver"),
    RDBMS("rdbms", "com.alibaba.rdbms.plugin.rdbms.util.DataBaseType"),
    DB2("db2", "com.ibm.db2.jcc.DB2Driver"),
    HIVE("hive", "org.apache.hive.jdbc.HiveDriver"),
    CarbonData("carbonData", "org.apache.hive.jdbc.HiveDriver"),
    Spark("Spark", "org.apache.hive.jdbc.HiveDriver"),
    ADS("ads","com.mysql.jdbc.Driver"),
    RDS("rds","com.mysql.jdbc.Driver"),
    MaxCompute("maxcompute","com.aliyun.odps.jdbc.OdpsDriver"),
    LIBRA("libra", "org.postgresql.Driver"),
    GBase8a("GBase8a","com.gbase.jdbc.Driver"),
    Kylin("Kylin","org.apache.kylin.jdbc.Driver"),
    Kudu("Kudu","org.apache.hive.jdbc.HiveDriver"),
    Impala("Impala", "com.cloudera.impala.jdbc41.Driver"),
    Clickhouse("Clickhouse","ru.yandex.clickhouse.ClickHouseDriver"),
    HIVE1X("hive", "org.apache.hive.jdbc.HiveDriver"),
    Polardb_For_MySQL("polardb for mysql","com.mysql.jdbc.Driver"),
    Phoenix("Phoenix", "org.apache.phoenix.jdbc.PhoenixDriver"),
    TiDB("TiDB", "com.mysql.jdbc.Driver");

    private String typeName;
    private String driverClassName;

    DataBaseType(String typeName, String driverClassName) {
        this.typeName = typeName;
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String appendJDBCSuffixForReader(String jdbc) {
        String result = jdbc;
        String suffix = null;
        switch (this) {
            case MySql:
            case DRDS:
                suffix = "yearIsDateType=false&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false&rewriteBatchedStatements=true";
                if (jdbc.contains("?")) {
                    result = jdbc + "&" + suffix;
                } else {
                    result = jdbc + "?" + suffix;
                }
                break;
            case Oracle:
                break;
            case SQLServer:
                break;
            case DB2:
                break;
            case PostgreSQL:
            	break;
            case RDBMS:
                break;
            case LIBRA:
                break;
            default:
                throw new RdosDefineException("unsupported database type.", ErrorCode.UNSUPPORTED_TYPE);
        }

        return result;
    }

    public String appendJDBCSuffixForWriter(String jdbc) {
        String result = jdbc;
        String suffix = null;
        switch (this) {
            case MySql:
                suffix = "yearIsDateType=false&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true&tinyInt1isBit=false";
                if (jdbc.contains("?")) {
                    result = jdbc + "&" + suffix;
                } else {
                    result = jdbc + "?" + suffix;
                }
                break;
            case DRDS:
                suffix = "yearIsDateType=false&zeroDateTimeBehavior=convertToNull";
                if (jdbc.contains("?")) {
                    result = jdbc + "&" + suffix;
                } else {
                    result = jdbc + "?" + suffix;
                }
                break;
            case Oracle:
                break;
            case SQLServer:
                break;
            case DB2:
                break;
            case PostgreSQL:
            	break;
            case LIBRA:
                break;
            case RDBMS:
                break;
            default:
                throw new RdosDefineException("unsupported database type.", ErrorCode.UNSUPPORTED_TYPE);
        }

        return result;
    }

    public String formatPk(String splitPk) {
        String result = splitPk;

        switch (this) {
            case MySql:
            case Oracle:
                if (splitPk.length() >= 2 && splitPk.startsWith("`") && splitPk.endsWith("`")) {
                    result = splitPk.substring(1, splitPk.length() - 1).toLowerCase();
                }
                break;
            case SQLServer:
                if (splitPk.length() >= 2 && splitPk.startsWith("[") && splitPk.endsWith("]")) {
                    result = splitPk.substring(1, splitPk.length() - 1).toLowerCase();
                }
                break;
            case DB2:
            case PostgreSQL:
            case LIBRA:
            	break;
            default:
                throw new RdosDefineException("unsupported database type.", ErrorCode.UNSUPPORTED_TYPE);
        }

        return result;
    }


    public String quoteColumnName(String columnName) {
        String result = columnName;

        switch (this) {
            case MySql:
                result = "`" + columnName.replace("`", "``") + "`";
                break;
            case Oracle:
                break;
            case SQLServer:
                result = "[" + columnName + "]";
                break;
            case DB2:
            case PostgreSQL:
            case LIBRA:
                break;
            default:
                throw new RdosDefineException("unsupported database type.", ErrorCode.UNSUPPORTED_TYPE);
        }

        return result;
    }

    public String quoteTableName(String tableName) {
        String result = tableName;

        switch (this) {
            case MySql:
                result = "`" + tableName.replace("`", "``") + "`";
                break;
            case Oracle:
                break;
            case SQLServer:
                break;
            case DB2:
                break;
            case PostgreSQL:
                break;
            case LIBRA:
                break;
            default:
                throw new RdosDefineException("unsupported database type.", ErrorCode.UNSUPPORTED_TYPE);
        }

        return result;
    }

    private static Pattern mysqlPattern = Pattern.compile("jdbc:mysql://(.+):\\d+/.+");
    private static Pattern oraclePattern = Pattern.compile("jdbc:oracle:thin:@(.+):\\d+:.+");

    /**
     * 注意：目前只实现了从 mysql/oracle 中识别出ip 信息.未识别到则返回 null.
     */
    public static String parseIpFromJdbcUrl(String jdbcUrl) {
        Matcher mysql = mysqlPattern.matcher(jdbcUrl);
        if (mysql.matches()) {
            return mysql.group(1);
        }
        Matcher oracle = oraclePattern.matcher(jdbcUrl);
        if (oracle.matches()) {
            return oracle.group(1);
        }
        return null;
    }
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
