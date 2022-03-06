package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:34 2019-08-21
 * @Description：数据同步插件名称
 */
public class PluginName {

    public static final String MYSQL_POLL_R = "mysqlreader";
    public static final String MySQL_R = "mysqlreader";
    public static final String Clickhouse_R = "clickhousereader";
    public static final String Polardb_for_MySQL_R = "polardbreader";
    public static final String MySQLD_R = "mysqldreader";
    public static final String Oracle_R = "oraclereader";
    public static final String SQLServer_R = "sqlserverreader";
    public static final String PostgreSQL_R = "postgresqlreader";
    public static final String ES_R = "esreader";
    public static final String FTP_R = "ftpreader";
    public static final String HBase_R = "hbasereader";
    public static final String HDFS_R = "hdfsreader";
    public static final String Hive_R = "hdfsreader";
    public static final String MONGODB_R = "mongodbreader";
    public static final String ODPS_R = "odpsreader";
    public static final String Stream_R = "streamreader";
    public static final  String DB2_R = "db2reader";
    public static final String CarbonData_R = "carbondatareader";
    public static final String GBase_R = "gbasereader";
    public static final String Kudu_R = "kudureader";
    public static final String Phoenix_R = "phoenixreader";
    public static final  String Phoenix5_R = "phoenix5reader";
    public static final  String DM_R = "dmreader";
    public static final  String GREENPLUM_R = "greenplumreader";
    public static final  String KINGBASE_R = "kingbasereader";
    public static final String AWS_S3_R = "s3reader";
    public static final String InfluxDB_R = "influxdbreader";
    public static final  String ADB_FOR_PG_R = "adbpostgresqlreader";
    public static final String OpenTSDB_R = "opentsdbreader";
    public static final String SOLR_R = "solrreader";

    public static final  String MySQL_W = "mysqlwriter";
    public static final  String Clichhouse_W = "clickhousewriter";
    public static final  String Polardb_for_MySQL_W = "polardbwriter";
    public static final String Oracle_W = "oraclewriter";
    public static final String SQLSERVER_W = "sqlserverwriter";
    public static final String POSTGRESQL_W = "postgresqlwriter";
    public static final String ES_W = "eswriter";
    public static final String ES7_R = "elasticsearch7reader";
    public static final String ES7_W = "elasticsearch7writer";
    public static final String FTP_W = "ftpwriter";
    public static final String HBASE_W = "hbasewriter";
    public static final String MONGODB_W = "mongodbwriter";
    public static final String ODPS_W = "odpswriter";
    public static final String REDIS_W = "rediswriter";
    public static final  String STREAM_W = "streamwriter";
    public static final String DB2_W = "db2writer";
    public static final String CarbonData_W = "carbondatawriter";
    public static final String GBase_W = "gbasewriter";
    public static final String Kudu_W = "kuduwriter";
    public static final String Phoenix_W = "phoenixwriter";
    public static final String Phoenix5_W = "phoenix5writer";
    public static final  String DM_W = "dmwriter";
    public static final String GREENPLUM_W = "greenplumwriter";
    public static final String KINGBASE_W = "kingbasewriter";
    public static final  String AWS_S3_W = "s3writer";
    public static final String INCEPTOR_W = "inceptorwriter";
    public static final String ADB_FOR_PG_W = "adbpostgresqlwriter";
    public static final String SOLR_W = "solrwriter";

    List<String> RDB_READER = Arrays.asList(
            MySQL_R,MySQLD_R,Oracle_R,SQLServer_R,PostgreSQL_R,DB2_R,GBase_R,Clickhouse_R,Polardb_for_MySQL_R,DM_R,GREENPLUM_R,KINGBASE_R,
            ADB_FOR_PG_R
    );

    public static final String BINLOG_R = "binlogreader";
    public static final String MYSQL_CDC_R = "mysqlcdcreader";
    public static final String WEBSOCKET_R = "websocketreader";
    public static final String SOCKET_R = "socketreader";
    public static final String RESTFUL_R = "restapireader";
    public static final String ORACLE_BINLOG_R = "oraclelogminerreader";
    public static final String ORACLE_POLL_R = "oraclereader";
    public static final String SQLSERVER_CDC_R = "sqlservercdcreader";
    public static final String SQLSERVER_POLL_R = "sqlserverreader";
    public static final String KAFKA_R = "kafkareader";
    public static final String KAFKA_09_R = "kafka09reader";
    public static final String KAFKA_10_R = "kafka10reader";
    public static final String KAFKA_11_R = "kafka11reader";
    public static final String KAFKA_2X_R = "kafkareader";
    public static final String EMQ_R = "emqxreader";
    public static final String PGWAL_R = "pgwalreader";

    public static final String HDFS_W = "hdfswriter";
    public static final String tbds_hdfs_W = "tbds_hdfswriter";
    public static final String HIVE_W = "hivewriter";
    public static final String KAFKA_W = "kafkawriter";
    public static final String KAFKA_09_W = "kafka09writer";
    public static final String KAFKA_10_W = "kafka10writer";
    public static final String KAFKA_11_W = "kafka11writer";
    public static final String KAFKA_2X_W = "kafkawriter";
    public static final String EMQ_W = "emqxwriter";
    public static final String DORIS_RESTFUL_W = "doriswriter";

    public static String getPluginName(JSONObject plugin) {
        return plugin.getString("name");
    }
}
