import { DATA_SOURCE } from './const';

export const jdbcUrlExample: any = {
    [DATA_SOURCE.KYLIN]: 'http://ip:port',
    [DATA_SOURCE.MYSQL]: 'jdbc:mysql://host:port/dbName',
    [DATA_SOURCE.POLAR_DB]: 'jdbc:mysql://host:port/dbName',
    [DATA_SOURCE.CLICK_HOUSE]: 'jdbc:clickhouse://<host>:<port>[/<database>]',
    [DATA_SOURCE.ORACLE]: [
        'jdbc:oracle:thin:@host:port:dbName',
        'jdbc:oracle:thin:@//host:port/service_name'
    ],
    [DATA_SOURCE.SQLSERVER]:
        'jdbc:jtds:sqlserver://host:port;DatabaseName=dbName',
    [DATA_SOURCE.POSTGRESQL]: 'jdbc:postgresql://host:port/database',
    [DATA_SOURCE.HIVE_2]: 'jdbc:hive2://host:port/dbName',
    [DATA_SOURCE.HIVE_1]: 'jdbc:hive://host:port/dbName',
    [DATA_SOURCE.CARBONDATA]: 'jdbc:hive2://host:port/dbName',
    [DATA_SOURCE.LIBRASQL]: 'jdbc:postgresql://host:port/database',
    [DATA_SOURCE.DB2]: 'jdbc:db2://host:port/dbName',
    [DATA_SOURCE.GBASE]: 'jdbc:gbase://host:port/dbName'
};
