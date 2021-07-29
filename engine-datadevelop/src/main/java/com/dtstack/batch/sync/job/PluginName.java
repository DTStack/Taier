package com.dtstack.batch.sync.job;

import java.util.Arrays;
import java.util.List;

/**
 * 数据同步插件名称
 */
public interface PluginName {

    String MySQL_R = "mysqlreader";
    String Clickhouse_R = "clickhousereader";
    String Polardb_for_MySQL_R = "polardbreader";
    String MySQLD_R = "mysqldreader";
    String Oracle_R = "oraclereader";
    String SQLServer_R = "sqlserverreader";
    String PostgreSQL_R = "postgresqlreader";
    String ES_R = "esreader";
    String FTP_R = "ftpreader";
    String HBase_R = "hbasereader";
    String HDFS_R = "hdfsreader";
    String Hive_R = "hdfsreader";
    String MongoDB_R = "mongodbreader";
    String ODPS_R = "odpsreader";
    String Stream_R = "streamreader";
    String DB2_R = "db2reader";
    String CarbonData_R = "carbondatareader";
    String GBase_R = "gbasereader";
    String Kudu_R = "kudureader";
    String Phoenix_R = "phoenixreader";
    String Phoenix5_R = "phoenix5reader";
    String DM_R = "dmreader";
    String GREENPLUM_R = "greenplumreader";
    String KINGBASE_R = "kingbasereader";
    String AWS_S3_R = "s3reader";
    String InfluxDB_R = "influxdbreader";
    String ADB_FOR_PG_R = "adbpostgresqlreader";

    String MySQL_W = "mysqlwriter";
    String Clichhouse_W = "clickhousewriter";
    String Polardb_for_MySQL_W = "polardbwriter";
    String Oracle_W = "oraclewriter";
    String SQLServer_W = "sqlserverwriter";
    String PostgreSQL_W = "postgresqlwriter";
    String ES_W = "eswriter";
    String FTP_W = "ftpwriter";
    String HBase_W = "hbasewriter";
    String HDFS_W = "hdfswriter";
    String Hive_W = "hdfswriter";
    String MongoDB_W = "mongodbwriter";
    String ODPS_W = "odpswriter";
    String Redis_W = "rediswriter";
    String Stream_W = "streamwriter";
    String DB2_W = "db2writer";
    String CarbonData_W = "carbondatawriter";
    String GBase_W = "gbasewriter";
    String Kudu_W = "kuduwriter";
    String Phoenix_W = "phoenixwriter";
    String Phoenix5_W = "phoenix5writer";
    String DM_W = "dmwriter";
    String GREENPLUM_W = "greenplumwriter";
    String KINGBASE_W = "kingbasewriter";
    String AWS_S3_W = "s3writer";
    String INCEPTOR_W = "inceptorwriter";
    String ADB_FOR_PG_W = "adbpostgresqlwriter";

    List<String> RDB_READER = Arrays.asList(
            MySQL_R,MySQLD_R,Oracle_R,SQLServer_R,PostgreSQL_R,DB2_R,GBase_R,Clickhouse_R,Polardb_for_MySQL_R,DM_R,GREENPLUM_R,KINGBASE_R,
            ADB_FOR_PG_R
    );
}
