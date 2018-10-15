import {DATA_SOURCE} from "./index";

export const jdbcUrlExample={
    [DATA_SOURCE.MYSQL]:"jdbc:mysql://host:port/dbName",
    [DATA_SOURCE.ORACLE]:["jdbc:oracle:thin:@host:port:dbName","jdbc:oracle:thin:@//host:port/service_name"],
    [DATA_SOURCE.SQLSERVER]:"jdbc:sqlserver://host:port;DatabaseName=dbName",
    [DATA_SOURCE.POSTGRESQL]:"jdbc:postgresql://host:port/database",
    [DATA_SOURCE.HIVE]:"jdbc:hive2://host:port/dbName",
    [DATA_SOURCE.RDS]:"jdbc:mysql://host:port/dbName",
    [DATA_SOURCE.DB2]:"jdbc:db2://host:port/dbName",
}