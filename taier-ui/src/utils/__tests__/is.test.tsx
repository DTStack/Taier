import {DATA_SOURCE_ENUM, ENGINE_SOURCE_TYPE_ENUM, FLINK_VERSIONS, ID_COLLECTIONS, KAFKA_DATA_TYPE,} from '@/constant';
import {
	isAvro,
	isCacheExceptLRU,
	isCacheOnlyAll,
	isES,
	isHaveAsyncPoolSize,
	isHaveCollection,
	isHaveCustomParams,
	isHaveDataPreview,
	isHaveParallelism,
	isHavePartition,
	isHavePrimaryKey,
	isHaveSchema,
	isHaveTableColumn,
	isHaveTableList,
	isHaveTopic,
	isHaveUpdateMode,
	isHaveUpdateStrategy,
	isHaveUpsert,
	isHbase,
	isHdfsType,
	isHive,
	isKafka,
	isLowerES,
	isMysqlTypeSource,
	isOracleEngine,
	isRDB,
	isRedis,
	isS3,
	isSchemaRequired,
	isShowBucket,
	isShowSchema,
	isShowTimeForOffsetReset,
	isSparkEngine,
	isSqlServer,
	isTaskTab,
} from '../is';

describe('utils/is', () => {
	it('isSchemaRequired', () => {
		expect(isSchemaRequired(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isSchemaRequired(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isSchemaRequired(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isSchemaRequired(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isSchemaRequired()).toBeFalsy();
		expect(isSchemaRequired(DATA_SOURCE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isKafka', () => {
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA_2X)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA_11)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA_09)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA_10)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.TBDS_KAFKA)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA_HUAWEI)).toBeTruthy();
		expect(isKafka(DATA_SOURCE_ENUM.KAFKA_CONFLUENT)).toBeTruthy();

		expect(isKafka()).toBeFalsy();
		expect(isKafka(DATA_SOURCE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isHaveTableColumn', () => {
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.POLAR_DB_For_MySQL)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.CLICKHOUSE)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.KUDU)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.IMPALA)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.TIDB)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.HIVE)).toBeTruthy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.INCEPTOR)).toBeTruthy();

		expect(isHaveTableColumn()).toBeFalsy();
		expect(isHaveTableColumn(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHaveTopic', () => {
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA_11)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA_09)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA_10)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA_2X)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.TBDS_KAFKA)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA_HUAWEI)).toBeTruthy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.KAFKA_CONFLUENT)).toBeTruthy();

		expect(isHaveTopic()).toBeFalsy();
		expect(isHaveTopic(DATA_SOURCE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isHavePartition', () => {
		expect(isHavePartition(DATA_SOURCE_ENUM.IMPALA)).toBeTruthy();
		expect(isHavePartition(DATA_SOURCE_ENUM.HIVE)).toBeTruthy();
		expect(isHavePartition(DATA_SOURCE_ENUM.INCEPTOR)).toBeTruthy();

		expect(isHavePartition()).toBeFalsy();
		expect(isHavePartition(DATA_SOURCE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isHaveSchema', () => {
		expect(isHaveSchema(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveSchema(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isHaveSchema(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isHaveSchema(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHaveSchema(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isHaveSchema()).toBeFalsy();
		expect(isHaveSchema(DATA_SOURCE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isHaveTableList', () => {
		expect(isHaveTableList(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.POLAR_DB_For_MySQL)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.HBASE)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.TBDS_HBASE)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.HBASE_HUAWEI)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.MONGODB)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.KUDU)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.IMPALA)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.TIDB)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.CLICKHOUSE)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.S3)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.CSP_S3)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.HIVE)).toBeTruthy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.INCEPTOR)).toBeTruthy();

		expect(isHaveTableList()).toBeFalsy();
		expect(isHaveTableList(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHavePrimaryKey', () => {
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.POLAR_DB_For_MySQL)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.CLICKHOUSE)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.DB2)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.TIDB)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.KAFKA)).toBeTruthy();

		expect(isHavePrimaryKey()).toBeFalsy();
		expect(isHavePrimaryKey(DATA_SOURCE_ENUM.KAFKA, FLINK_VERSIONS.FLINK_1_10)).toBeFalsy();
	});

	it('isHaveDataPreview', () => {
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.REDIS)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.UPRedis)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.ES)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.ES6)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.ES7)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.HBASE)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.HBASE_HUAWEI)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.HIVE)).toBeTruthy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.INCEPTOR)).toBeTruthy();

		expect(isHaveDataPreview()).toBeFalsy();
		expect(isHaveDataPreview(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHaveCollection', () => {
		expect(isHaveCollection(DATA_SOURCE_ENUM.SOLR)).toBeTruthy();

		expect(isHaveCollection()).toBeFalsy();
		expect(isHaveCollection(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isShowBucket', () => {
		expect(isShowBucket(DATA_SOURCE_ENUM.S3)).toBeTruthy();
		expect(isShowBucket(DATA_SOURCE_ENUM.CSP_S3)).toBeTruthy();

		expect(isShowBucket()).toBeFalsy();
		expect(isShowBucket(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isShowTimeForOffsetReset', () => {
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.KAFKA)).toBeTruthy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.KAFKA_2X)).toBeTruthy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.KAFKA_10)).toBeTruthy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.KAFKA_11)).toBeTruthy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.TBDS_KAFKA)).toBeTruthy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.KAFKA_HUAWEI)).toBeTruthy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.KAFKA_CONFLUENT)).toBeTruthy();

		expect(isShowTimeForOffsetReset()).toBeFalsy();
		expect(isShowTimeForOffsetReset(DATA_SOURCE_ENUM.MySQL8)).toBeFalsy();
	});

	it('isShowSchema', () => {
		expect(isShowSchema(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isShowSchema(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isShowSchema(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();

		expect(isShowSchema(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isShowSchema(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isShowSchema()).toBeFalsy();
		expect(isShowSchema(DATA_SOURCE_ENUM.MySQL8)).toBeFalsy();
	});

	it('isShowSchema', () => {
		expect(isShowSchema(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isShowSchema(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isShowSchema(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();

		expect(isShowSchema(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isShowSchema(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isShowSchema()).toBeFalsy();
		expect(isShowSchema(DATA_SOURCE_ENUM.MySQL8)).toBeFalsy();
	});

	it('isHaveUpdateMode', () => {
		expect(isHaveUpdateMode(DATA_SOURCE_ENUM.S3)).toBeFalsy();
		expect(isHaveUpdateMode(DATA_SOURCE_ENUM.CSP_S3)).toBeFalsy();
		expect(isHaveUpdateMode(DATA_SOURCE_ENUM.SOLR)).toBeFalsy();
		expect(isHaveUpdateMode(DATA_SOURCE_ENUM.HIVE)).toBeFalsy();
		expect(isHaveUpdateMode(DATA_SOURCE_ENUM.INCEPTOR)).toBeFalsy();

		expect(isHaveUpdateMode()).toBeFalsy();
		expect(isHaveUpdateMode(DATA_SOURCE_ENUM.MySQL8)).toBeTruthy();
	});

	it('isHaveUpsert', () => {
		expect(isHaveUpsert(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.POLAR_DB_For_MySQL)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.CLICKHOUSE)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.KUDU)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.DB2)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.TIDB)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isHaveUpsert(DATA_SOURCE_ENUM.KAFKA)).toBeTruthy();

		expect(isHaveUpsert()).toBeFalsy();
		expect(isHaveUpsert(DATA_SOURCE_ENUM.KAFKA, FLINK_VERSIONS.FLINK_1_10)).toBeFalsy();
	});

	it('isHaveUpdateStrategy', () => {
		expect(isHaveUpdateStrategy(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHaveUpdateStrategy(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHaveUpdateStrategy(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveUpdateStrategy(DATA_SOURCE_ENUM.TIDB)).toBeTruthy();

		expect(isHaveUpdateStrategy()).toBeFalsy();
		expect(isHaveUpdateStrategy(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHaveParallelism', () => {
		expect(isHaveParallelism(DATA_SOURCE_ENUM.HIVE)).toBeFalsy();
		expect(isHaveParallelism(DATA_SOURCE_ENUM.INCEPTOR)).toBeFalsy();

		expect(isHaveParallelism()).toBeFalsy();
		expect(isHaveParallelism(DATA_SOURCE_ENUM.KAFKA)).toBeTruthy();
	});

	it('isCacheOnlyAll', () => {
		expect(isCacheOnlyAll(DATA_SOURCE_ENUM.INCEPTOR)).toBeTruthy();

		expect(isCacheOnlyAll()).toBeFalsy();
		expect(isCacheOnlyAll(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isCacheExceptLRU', () => {
		expect(isCacheExceptLRU(DATA_SOURCE_ENUM.HBASE_HUAWEI)).toBeTruthy();

		expect(isCacheExceptLRU()).toBeFalsy();
		expect(isCacheExceptLRU(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHaveAsyncPoolSize', () => {
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.TIDB)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.POLAR_DB_For_MySQL)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.CLICKHOUSE)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.KINGBASE8)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.IMPALA)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.INCEPTOR)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.SOLR)).toBeTruthy();

		expect(isHaveAsyncPoolSize()).toBeFalsy();
		expect(isHaveAsyncPoolSize(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHaveCustomParams', () => {
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.REDIS)).toBeTruthy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.UPRedis)).toBeTruthy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.MONGODB)).toBeTruthy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.ES)).toBeTruthy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.HBASE)).toBeTruthy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.HBASE_HUAWEI)).toBeTruthy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.KUDU)).toBeTruthy();

		expect(isHaveCustomParams()).toBeFalsy();
		expect(isHaveCustomParams(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isMysqlTypeSource', () => {
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.UPDRDB)).toBeTruthy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.POLAR_DB_For_MySQL)).toBeTruthy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.ORACLE)).toBeTruthy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.POSTGRESQL)).toBeTruthy();

		expect(isMysqlTypeSource()).toBeFalsy();
		expect(isMysqlTypeSource(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isHbase', () => {
		expect(isHbase(DATA_SOURCE_ENUM.HBASE)).toBeTruthy();
		expect(isHbase(DATA_SOURCE_ENUM.TBDS_HBASE)).toBeTruthy();
		expect(isHbase(DATA_SOURCE_ENUM.HBASE_HUAWEI)).toBeTruthy();

		expect(isHbase()).toBeFalsy();
		expect(isHbase(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isES', () => {
		expect(isES(DATA_SOURCE_ENUM.ES)).toBeTruthy();
		expect(isES(DATA_SOURCE_ENUM.ES6)).toBeTruthy();
		expect(isES(DATA_SOURCE_ENUM.ES7)).toBeTruthy();

		expect(isES(DATA_SOURCE_ENUM.KAFKA)).toBeFalsy();
	});

	it('isLowerES', () => {
		expect(isLowerES(DATA_SOURCE_ENUM.ES)).toBeTruthy();
		expect(isLowerES(DATA_SOURCE_ENUM.ES6)).toBeTruthy();

		expect(isLowerES()).toBeFalsy();
		expect(isLowerES(DATA_SOURCE_ENUM.ES7)).toBeFalsy();
	});

	it('isRedis', () => {
		expect(isRedis(DATA_SOURCE_ENUM.REDIS)).toBeTruthy();
		expect(isRedis(DATA_SOURCE_ENUM.UPRedis)).toBeTruthy();

		expect(isRedis()).toBeFalsy();
		expect(isRedis(DATA_SOURCE_ENUM.ES7)).toBeFalsy();
	});

	it('isHive', () => {
		expect(isHive(DATA_SOURCE_ENUM.HIVE)).toBeTruthy();

		expect(isHive()).toBeFalsy();
		expect(isHive(DATA_SOURCE_ENUM.ES7)).toBeFalsy();
	});

	it('isS3', () => {
		expect(isS3(DATA_SOURCE_ENUM.S3)).toBeTruthy();
		expect(isS3(DATA_SOURCE_ENUM.CSP_S3)).toBeTruthy();

		expect(isS3()).toBeFalsy();
		expect(isS3(DATA_SOURCE_ENUM.ES7)).toBeFalsy();
	});

	it('isSqlServer', () => {
		expect(isSqlServer(DATA_SOURCE_ENUM.SQLSERVER)).toBeTruthy();
		expect(isSqlServer(DATA_SOURCE_ENUM.SQLSERVER_2017_LATER)).toBeTruthy();

		expect(isSqlServer()).toBeFalsy();
		expect(isSqlServer(DATA_SOURCE_ENUM.ES7)).toBeFalsy();
	});

	it('isAvro', () => {
		expect(isAvro(KAFKA_DATA_TYPE.TYPE_AVRO)).toBeTruthy();
		expect(isAvro(KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT)).toBeTruthy();

		expect(isAvro()).toBeFalsy();
		expect(isAvro(KAFKA_DATA_TYPE.TYPE_COLLECT_TEXT)).toBeFalsy();
	});

	it('isHdfsType', () => {
		expect(isHdfsType(DATA_SOURCE_ENUM.HDFS)).toBeTruthy();

		expect(isHdfsType()).toBeFalsy();
		expect(isHdfsType(DATA_SOURCE_ENUM.ES7)).toBeFalsy();
	});

	it('isSparkEngine', () => {
		expect(isSparkEngine(ENGINE_SOURCE_TYPE_ENUM.HADOOP)).toBeTruthy();

		expect(isSparkEngine()).toBeFalsy();
		expect(isSparkEngine(ENGINE_SOURCE_TYPE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isOracleEngine', () => {
		expect(isOracleEngine(ENGINE_SOURCE_TYPE_ENUM.ORACLE)).toBeTruthy();

		expect(isOracleEngine(ENGINE_SOURCE_TYPE_ENUM.MYSQL)).toBeFalsy();
	});

	it('isRDB', () => {
		expect(isRDB(DATA_SOURCE_ENUM.MYSQL)).toBeTruthy();

		expect(isRDB(DATA_SOURCE_ENUM.OCEANBASE)).toBeFalsy();
	});

	it('isTaskTab', () => {
		expect(isTaskTab()).toBeFalsy();
		expect(isTaskTab(123)).toBeTruthy();
		expect(isTaskTab(`${ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX}-tab`)).toBeFalsy();
		expect(isTaskTab(`${ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX}-tab`)).toBeFalsy();
		expect(isTaskTab(`${ID_COLLECTIONS.CREATE_TASK_PREFIX}-tab`)).toBeFalsy();
		expect(isTaskTab(`${ID_COLLECTIONS.EDIT_TASK_PREFIX}-tab`)).toBeFalsy();
		expect(isTaskTab(`${ID_COLLECTIONS.EDIT_FOLDER_PREFIX}-tab`)).toBeFalsy();
		expect(isTaskTab(`123-tab`)).toBeTruthy();
	});
});
