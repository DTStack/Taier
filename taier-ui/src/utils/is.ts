import type { UniqueId } from '@dtinsight/molecule/esm/common/types';

import {
    DATA_SOURCE_ENUM,
    ENGINE_SOURCE_TYPE_ENUM,
    FLINK_VERSIONS,
    ID_COLLECTIONS,
    KAFKA_DATA_TYPE,
    RDB_TYPE_ARRAY,
} from '@/constant';

/**
 * 是否需要 schema
 */
export const isSchemaRequired = (type?: DATA_SOURCE_ENUM) => {
    return !!(
        type &&
        [
            DATA_SOURCE_ENUM.POSTGRESQL,
            DATA_SOURCE_ENUM.KINGBASE8,
            DATA_SOURCE_ENUM.SQLSERVER,
            DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        ].includes(type)
    );
};

/**
 * 是否为 kafka
 */
export function isKafka(type?: DATA_SOURCE_ENUM) {
    return (
        type &&
        [
            DATA_SOURCE_ENUM.KAFKA,
            DATA_SOURCE_ENUM.KAFKA_2X,
            DATA_SOURCE_ENUM.KAFKA_11,
            DATA_SOURCE_ENUM.KAFKA_09,
            DATA_SOURCE_ENUM.KAFKA_10,
            DATA_SOURCE_ENUM.TBDS_KAFKA,
            DATA_SOURCE_ENUM.KAFKA_HUAWEI,
            DATA_SOURCE_ENUM.KAFKA_CONFLUENT,
        ].includes(type)
    );
}

/**
 * 是否拥有字段列的权限
 */
export function isHaveTableColumn(type?: DATA_SOURCE_ENUM) {
    return (
        type &&
        [
            DATA_SOURCE_ENUM.MYSQL,
            DATA_SOURCE_ENUM.UPDRDB,
            DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
            DATA_SOURCE_ENUM.ORACLE,
            DATA_SOURCE_ENUM.POSTGRESQL,
            DATA_SOURCE_ENUM.CLICKHOUSE,
            DATA_SOURCE_ENUM.KUDU,
            DATA_SOURCE_ENUM.IMPALA,
            DATA_SOURCE_ENUM.TIDB,
            DATA_SOURCE_ENUM.KINGBASE8,
            DATA_SOURCE_ENUM.SQLSERVER,
            DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
            DATA_SOURCE_ENUM.HIVE,
            DATA_SOURCE_ENUM.INCEPTOR,
        ].includes(type)
    );
}

/**
 * 是否拥有 Topic 的列
 */
export function isHaveTopic(type?: DATA_SOURCE_ENUM) {
    return (
        type &&
        [
            DATA_SOURCE_ENUM.KAFKA,
            DATA_SOURCE_ENUM.KAFKA_11,
            DATA_SOURCE_ENUM.KAFKA_09,
            DATA_SOURCE_ENUM.KAFKA_10,
            DATA_SOURCE_ENUM.KAFKA_2X,
            DATA_SOURCE_ENUM.TBDS_KAFKA,
            DATA_SOURCE_ENUM.KAFKA_HUAWEI,
            DATA_SOURCE_ENUM.KAFKA_CONFLUENT,
        ].includes(type)
    );
}

/**
 * 是否拥有分区
 */
export function isHavePartition(type?: DATA_SOURCE_ENUM) {
    const list = [DATA_SOURCE_ENUM.IMPALA, DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR];
    return type && list.includes(type);
}

/**
 * 是否拥有 Schema
 */
export function isHaveSchema(type?: DATA_SOURCE_ENUM) {
    const list = [
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.POSTGRESQL,
        DATA_SOURCE_ENUM.KINGBASE8,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
    ];
    return type && list.includes(type);
}

/**
 * 是否拥有表字段
 * S3 数据源的 Bucket 下拉框用的也是 TableList 的接口，表单字段也不是 table 是 bucket 。。
 */
export function isHaveTableList(type?: DATA_SOURCE_ENUM) {
    const list = [
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
        DATA_SOURCE_ENUM.HBASE,
        DATA_SOURCE_ENUM.TBDS_HBASE,
        DATA_SOURCE_ENUM.HBASE_HUAWEI,
        DATA_SOURCE_ENUM.MONGODB,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.POSTGRESQL,
        DATA_SOURCE_ENUM.KUDU,
        DATA_SOURCE_ENUM.IMPALA,
        DATA_SOURCE_ENUM.TIDB,
        DATA_SOURCE_ENUM.CLICKHOUSE,
        DATA_SOURCE_ENUM.KINGBASE8,
        DATA_SOURCE_ENUM.S3,
        DATA_SOURCE_ENUM.CSP_S3,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        DATA_SOURCE_ENUM.HIVE,
        DATA_SOURCE_ENUM.INCEPTOR,
    ];
    return type && list.includes(type);
}

/**
 * 是否拥有主键列的权限
 */
export function isHavePrimaryKey(type?: DATA_SOURCE_ENUM, version: string = FLINK_VERSIONS.FLINK_1_12) {
    const list = [
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.POSTGRESQL,
        DATA_SOURCE_ENUM.KINGBASE8,
        DATA_SOURCE_ENUM.CLICKHOUSE,
        DATA_SOURCE_ENUM.DB2,
        DATA_SOURCE_ENUM.TIDB,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
    ];
    return type && (list.includes(type) || (version === FLINK_VERSIONS.FLINK_1_12 && isKafka(type)));
}

/** 是否拥有数据预览 */
export function isHaveDataPreview(type?: DATA_SOURCE_ENUM) {
    const list = [
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.REDIS,
        DATA_SOURCE_ENUM.UPRedis,
        DATA_SOURCE_ENUM.ES,
        DATA_SOURCE_ENUM.ES6,
        DATA_SOURCE_ENUM.ES7,
        DATA_SOURCE_ENUM.HBASE,
        DATA_SOURCE_ENUM.HBASE_HUAWEI,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        DATA_SOURCE_ENUM.HIVE,
        DATA_SOURCE_ENUM.INCEPTOR,
    ];
    return type && list.includes(type);
}

/**
 * 是否拥有 collection
 */
export function isHaveCollection(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.SOLR].includes(type);
}

/**
 * 是否渲染 Bucket
 */
export function isShowBucket(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(type);
}

/**
 * 是否展示 offsetReset 的 time 列
 */
export function isShowTimeForOffsetReset(type?: DATA_SOURCE_ENUM) {
    return (
        type &&
        [
            DATA_SOURCE_ENUM.KAFKA,
            DATA_SOURCE_ENUM.KAFKA_2X,
            DATA_SOURCE_ENUM.KAFKA_10,
            DATA_SOURCE_ENUM.KAFKA_11,
            DATA_SOURCE_ENUM.TBDS_KAFKA,
            DATA_SOURCE_ENUM.KAFKA_HUAWEI,
            DATA_SOURCE_ENUM.KAFKA_CONFLUENT,
        ].includes(type)
    );
}

/**
 * 是否展示 schema
 */
export function isShowSchema(type?: DATA_SOURCE_ENUM) {
    const list = [DATA_SOURCE_ENUM.ORACLE, DATA_SOURCE_ENUM.POSTGRESQL, DATA_SOURCE_ENUM.KINGBASE8];
    return type && (isSqlServer(type) || list.includes(type));
}

/**
 * 是否有更新模式
 */
export function isHaveUpdateMode(type?: DATA_SOURCE_ENUM) {
    return (
        type &&
        ![
            DATA_SOURCE_ENUM.S3,
            DATA_SOURCE_ENUM.CSP_S3,
            DATA_SOURCE_ENUM.SOLR,
            DATA_SOURCE_ENUM.HIVE,
            DATA_SOURCE_ENUM.INCEPTOR,
        ].includes(type)
    );
}

/**
 * 是否允许更新模式切换
 */
export function isHaveUpsert(type?: DATA_SOURCE_ENUM, version: string = FLINK_VERSIONS.FLINK_1_12) {
    const list = [
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.POSTGRESQL,
        DATA_SOURCE_ENUM.CLICKHOUSE,
        DATA_SOURCE_ENUM.KUDU,
        DATA_SOURCE_ENUM.DB2,
        DATA_SOURCE_ENUM.TIDB,
        DATA_SOURCE_ENUM.KINGBASE8,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
    ];
    return type && (list.includes(type) || (version === FLINK_VERSIONS.FLINK_1_12 && isKafka(type)));
}

/**
 * 更新模式为更新时，是否可以选择更新策略
 */
export function isHaveUpdateStrategy(type?: DATA_SOURCE_ENUM) {
    const list = [DATA_SOURCE_ENUM.MYSQL, DATA_SOURCE_ENUM.UPDRDB, DATA_SOURCE_ENUM.ORACLE, DATA_SOURCE_ENUM.TIDB];
    return type && list.includes(type);
}

/**
 * 是否展示并行度
 */
export function isHaveParallelism(type?: DATA_SOURCE_ENUM) {
    return type && ![DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR].includes(type);
}

/**
 * 缓存策略是否只允许 ALL
 */
export function isCacheOnlyAll(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.INCEPTOR].includes(type);
}

/**
 * 不支持 LRU 的情况（包含支持 None 的情况）
 */
export function isCacheExceptLRU(type?: DATA_SOURCE_ENUM) {
    return (type && [DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(type)) || isCacheOnlyAll(type);
}

/**
 * 是否展示异步线程池
 */
export function isHaveAsyncPoolSize(type?: DATA_SOURCE_ENUM) {
    const list = [
        DATA_SOURCE_ENUM.MYSQL,
        DATA_SOURCE_ENUM.UPDRDB,
        DATA_SOURCE_ENUM.TIDB,
        DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
        DATA_SOURCE_ENUM.ORACLE,
        DATA_SOURCE_ENUM.POSTGRESQL,
        DATA_SOURCE_ENUM.CLICKHOUSE,
        DATA_SOURCE_ENUM.KINGBASE8,
        DATA_SOURCE_ENUM.IMPALA,
        DATA_SOURCE_ENUM.INCEPTOR,
        DATA_SOURCE_ENUM.SQLSERVER,
        DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        DATA_SOURCE_ENUM.SOLR,
    ];
    return type && list.includes(type);
}

/**
 * 是否可以添加自定义参数
 * @param type
 * @returns
 */
export function isHaveCustomParams(type?: DATA_SOURCE_ENUM) {
    const list = [
        DATA_SOURCE_ENUM.REDIS,
        DATA_SOURCE_ENUM.UPRedis,
        DATA_SOURCE_ENUM.MONGODB,
        DATA_SOURCE_ENUM.ES,
        DATA_SOURCE_ENUM.HBASE,
        DATA_SOURCE_ENUM.HBASE_HUAWEI,
        DATA_SOURCE_ENUM.KUDU,
    ];
    return type && list.includes(type);
}

/**
 * 是否为 Mysql 类型数据源
 */
export function isMysqlTypeSource(type?: DATA_SOURCE_ENUM) {
    return (
        type &&
        [
            DATA_SOURCE_ENUM.MYSQL,
            DATA_SOURCE_ENUM.UPDRDB,
            DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
            DATA_SOURCE_ENUM.ORACLE,
            DATA_SOURCE_ENUM.SQLSERVER,
            DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
            DATA_SOURCE_ENUM.POSTGRESQL,
        ].includes(type)
    );
}

/**
 * 是否为 Hbase 类型
 */
export function isHbase(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.HBASE, DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(type);
}

/**
 * 是否为 ES 类型
 */
export function isES(type: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7].includes(type);
}

/**
 * 是否为低版本的 ES
 */
export function isLowerES(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6].includes(type);
}

/**
 * 是否为 Redis 类型
 */
export function isRedis(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(type);
}

/**
 * 是否为 Hive 类型
 */
export function isHive(type?: DATA_SOURCE_ENUM) {
    return type === DATA_SOURCE_ENUM.HIVE;
}

/**
 * 是否为 S3 数据源
 */
export function isS3(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(type);
}

/**
 * 是否为 SqlServer 类型
 */
export function isSqlServer(type?: DATA_SOURCE_ENUM) {
    return type && [DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.SQLSERVER_2017_LATER].includes(type);
}

/**
 * 是否为 kafak 和 confluent 的输入输出类型 avro 和 avro-confluent
 */
export function isAvro(type?: KAFKA_DATA_TYPE) {
    return type && [KAFKA_DATA_TYPE.TYPE_AVRO, KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT].includes(type);
}

/**
 * 是否为 HDFS 类型
 */
export function isHdfsType(type?: DATA_SOURCE_ENUM) {
    return DATA_SOURCE_ENUM.HDFS === type;
}

/**
 * 是否是 Hadoop 引擎
 */
export function isSparkEngine(engineType?: numOrStr) {
    return ENGINE_SOURCE_TYPE_ENUM.HADOOP.toString() === engineType?.toString();
}

/**
 * 是否是 Oracle 引擎
 */
export function isOracleEngine(engineType: numOrStr) {
    return ENGINE_SOURCE_TYPE_ENUM.ORACLE.toString() === engineType.toString();
}

/**
 * 是否属于关系型数据源
 */
export function isRDB(type?: DATA_SOURCE_ENUM) {
    return !!(type && RDB_TYPE_ARRAY.includes(type));
}

/**
 * 根据任务 ID 来区分是否是任务 Tab
 */
export function isTaskTab(id?: UniqueId) {
    if (id === undefined) return false;
    if (typeof id === 'number') return true;
    const NON_TASK_TAB = [
        ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
        ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX,
        ID_COLLECTIONS.CREATE_TASK_PREFIX,
        ID_COLLECTIONS.EDIT_TASK_PREFIX,
        ID_COLLECTIONS.EDIT_FOLDER_PREFIX,
    ];

    return !NON_TASK_TAB.some((prefix) => id.startsWith(prefix));
}
