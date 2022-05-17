/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Circle from '@/components/circle';
import {
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	ENGINE_SOURCE_TYPE_ENUM,
	FLINK_VERSIONS,
	ID_COLLECTIONS,
	KAFKA_DATA_TYPE,
	RESOURCE_TYPE,
	TASK_LANGUAGE,
	TASK_PERIOD_ENUM,
	TASK_STATUS,
	TASK_TYPE_ENUM,
} from '@/constant';
import { UniqueId } from '@dtinsight/molecule/esm/common/types';

export function taskTypeText(type: TASK_TYPE_ENUM) {
	switch (type) {
		case TASK_TYPE_ENUM.SYNC:
			return '数据同步';
		case TASK_TYPE_ENUM.VIRTUAL:
			return '虚节点';
		case TASK_TYPE_ENUM.SQL:
			return 'FlinkSQL';
		case TASK_TYPE_ENUM.SHELL:
			return 'Shell';
		case TASK_TYPE_ENUM.HIVE_SQL:
			return 'HiveSQL';
		case TASK_TYPE_ENUM.SPARK_SQL:
			return 'SparkSQL';
		case TASK_TYPE_ENUM.DATA_ACQUISITION:
			return '实时采集';
		default:
			return '未知';
	}
}

export function taskStatusText(type: TASK_STATUS) {
	switch (type) {
		case TASK_STATUS.WAIT_SUBMIT:
			return '等待提交';
		case TASK_STATUS.CREATED:
			return '数据同步';
		case TASK_STATUS.INVOKED:
			return '已调度';
		case TASK_STATUS.DEPLOYING:
			return '部署中';
		case TASK_STATUS.RUNNING:
		case TASK_STATUS.TASK_STATUS_NOT_FOUND:
			return '运行中';
		case TASK_STATUS.FINISHED:
			return '成功';
		case TASK_STATUS.STOPING:
			return '取消中';
		case TASK_STATUS.STOPED:
			return '取消';
		case TASK_STATUS.RUN_FAILED:
			return '运行失败';
		case TASK_STATUS.SUBMIT_FAILED:
			return '提交失败';
		case TASK_STATUS.SUBMITTING:
			return '提交中';
		case TASK_STATUS.RESTARTING:
			return '重试中';
		case TASK_STATUS.SET_SUCCESS:
			return '设置成功';
		case TASK_STATUS.KILLED:
			return '已停止';
		case TASK_STATUS.SUBMITTED:
			return '已提交';
		case TASK_STATUS.WAIT_RUN:
			return '等待运行';
		case TASK_STATUS.WAIT_COMPUTE:
			return '等待计算';
		case TASK_STATUS.FROZEN:
			return '冻结';
		case TASK_STATUS.ENGINEACCEPTED:
			return '提交至引擎';
		case TASK_STATUS.PARENT_FAILD:
			return '上游失败';
		case TASK_STATUS.DO_FAIL:
			return '失败';
		case TASK_STATUS.COMPUTING:
			return '计算中';
		case TASK_STATUS.AUTO_CANCEL:
			return '自动取消';
		default:
			return '异常';
	}
}

export function linkMapping(key?: string) {
	switch (key) {
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.MYSQL]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.CARBONDATA]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.CLICKHOUSE]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.POLAR_DB_For_MySQL]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.ORACLE]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.SQLSERVER]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.SQLSERVER_2017_LATER]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.DB2]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.POSTGRESQL]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.TIDB]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KINGBASE8]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.GBASE_8A]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.PRESTO]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.VERTICA]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.ADB_FOR_PG]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HIVE3_CDP]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.DORIS]:
			return [
				['jdbcUrl', 'jdbcUrl'],
				['username', '用户名'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KUDU]:
			return [['hostPorts', '集群地址']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.IMPALA]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HIVE]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HIVE1X]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HIVE3X]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.INCEPTOR]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.SPARKTHRIFT]:
			return [
				['jdbcUrl', 'jdbcUrl'],
				['defaultFS', 'defaultFS'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HBASE]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HBASE2]:
			return [['hbase_quorum', 'Zookeeper集群地址']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HDFS]:
			return [['defaultFS', 'defaultFS']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.FTP]:
			return [
				['protocol', 'Protocol'],
				['host', 'Host'],
				['port', 'Port'],
				['username', '用户名'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.MAXCOMPUTE]:
			return [
				['endPoint', 'endPoint'],
				['project', '项目名称'],
				['accessId', 'Access Id'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.ES]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.ES6]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.ES7]:
			return [['address', '集群地址']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.REDIS]:
			return [
				['hostPort', '地址'],
				['database', '数据库'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.MONGODB]:
			return [
				['hostPorts', '集群地址'],
				['database', '数据库'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KAFKA_11]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KAFKA_09]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KAFKA_10]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KAFKA]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KAFKA_2X]:
			return [
				['address', '集群地址'],
				['brokerList', 'broker地址'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.EMQ]:
			return [['address', 'Broker URL']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.S3]:
			return [['hostname', 'hostname']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.WEBSOCKET]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.SOCKET]:
			return [['url', 'url']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.GREENPLUM6]:
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.PHOENIX]:
			return [['jdbcUrl', 'jdbcUrl']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.Kylin]:
			return [
				['authURL', 'authURL'],
				['username', '用户名'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.SOLR]:
			return [
				['zkHost', '集群地址'],
				['chroot', 'chroot路径'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.INFLUXDB]:
			return [
				['url', 'URL'],
				['username', '用户名'],
			];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.AWS_S3]:
			return [['accessKey', 'ACCESS KEY']];
		case DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.OPENTSDB]:
			return [['url', 'URL']];
		default:
			break;
	}
}

export function TaskTimeType(props: { value: TASK_PERIOD_ENUM }) {
	const { value } = props;
	switch (value) {
		case TASK_PERIOD_ENUM.MINUTE:
			return <span>分钟任务</span>;
		case TASK_PERIOD_ENUM.HOUR:
			return <span>小时任务</span>;
		case TASK_PERIOD_ENUM.DAY:
			return <span>天任务</span>;
		case TASK_PERIOD_ENUM.WEEK:
			return <span>周任务</span>;
		case TASK_PERIOD_ENUM.MONTH:
			return <span>月任务</span>;
		default:
			return <span>天任务</span>;
	}
}

export function TaskStatus(props: { value: TASK_STATUS }) {
	const { value } = props;
	switch (value) {
		case TASK_STATUS.RUNNING:
		case TASK_STATUS.TASK_STATUS_NOT_FOUND:
			return (
				<span>
					<Circle type="running" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.FINISHED:
		case TASK_STATUS.SET_SUCCESS:
			return (
				<span>
					<Circle type="finished" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.STOPED:
		case TASK_STATUS.STOPING:
		case TASK_STATUS.AUTO_CANCEL:
			return (
				<span>
					<Circle type="stopped" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.RUN_FAILED:
		case TASK_STATUS.SUBMIT_FAILED:
		case TASK_STATUS.PARENT_FAILD:
		case TASK_STATUS.DO_FAIL:
			return (
				<span>
					<Circle type="fail" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.SUBMITTING:
			return (
				<span>
					<Circle type="submitting" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.WAIT_RUN:
		case TASK_STATUS.WAIT_SUBMIT:
			return (
				<span>
					<Circle type="waitSubmit" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.FROZEN:
		case TASK_STATUS.KILLED:
			return (
				<span>
					<Circle type="frozen" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.RESTARTING:
			return (
				<span>
					<Circle type="restarting" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		default:
			return (
				<span>
					<Circle type="fail" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
	}
}

export function getEngineSourceTypeName(sourceId: ENGINE_SOURCE_TYPE_ENUM) {
	switch (sourceId) {
		case ENGINE_SOURCE_TYPE_ENUM.HADOOP:
			return 'Hadoop';
		case ENGINE_SOURCE_TYPE_ENUM.LIBRA:
			return 'LibrA';
		case ENGINE_SOURCE_TYPE_ENUM.TI_DB:
			return 'TiDB';
		case ENGINE_SOURCE_TYPE_ENUM.ORACLE:
			return 'Oracle';
		case ENGINE_SOURCE_TYPE_ENUM.GREEN_PLUM:
			return 'Greenplum';
		case ENGINE_SOURCE_TYPE_ENUM.PRESTO:
			return 'Presto';
		case ENGINE_SOURCE_TYPE_ENUM.ADB:
			return 'AnalyticDB PostgreSQL';
		case ENGINE_SOURCE_TYPE_ENUM.FLINK_ON_STANDALONE:
			return 'Flink on Standalone';
		case ENGINE_SOURCE_TYPE_ENUM.MYSQL:
			return 'MySQL';
		case ENGINE_SOURCE_TYPE_ENUM.SQLSERVER:
			return 'SQLServer';
		case ENGINE_SOURCE_TYPE_ENUM.DB2:
			return 'DB2';
		case ENGINE_SOURCE_TYPE_ENUM.OCEANBASE:
			return 'OceanBase';

		default:
			break;
	}
}

export function resourceNameMapping(type?: RESOURCE_TYPE) {
	switch (type) {
		case RESOURCE_TYPE.JAR:
			return 'jar';
		case RESOURCE_TYPE.PY:
			return 'py';
		case RESOURCE_TYPE.EGG:
			return 'egg';
		case RESOURCE_TYPE.ZIP:
			return 'zip';
		case RESOURCE_TYPE.OTHER:
			return '其它';
		default:
			return '未知';
	}
}

/**
 * 把 taskType 映射到 taskLanguage
 */
export function mappingTaskTypeToLanguage(taskType: TASK_TYPE_ENUM) {
	switch (taskType) {
		case TASK_TYPE_ENUM.SPARK_SQL:
			return TASK_LANGUAGE.SPARKSQL;
		case TASK_TYPE_ENUM.HIVE_SQL:
			return TASK_LANGUAGE.HIVESQL;
		case TASK_TYPE_ENUM.SQL:
			return TASK_LANGUAGE.FLINKSQL;
		default:
			return TASK_LANGUAGE.JSON;
	}
}

/**
 * 是不是kafka
 */
export function isKafka(type?: DATA_SOURCE_ENUM) {
	return (
		type !== undefined &&
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
 * kafak和confluent的输入输出类型avro和avro-confluent
 * @param type kafka数据类型
 * @returns boolean
 */
export function isAvro(type?: string) {
	return type && [KAFKA_DATA_TYPE.TYPE_AVRO, KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT].includes(type);
}
/**
 * 是否拥有字段列的权限
 * @param type 数据源类型
 * @returns boolean
 */
export function haveTableColumn(type?: DATA_SOURCE_ENUM) {
	const list = [
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
	];
	return type !== undefined && list.includes(type);
}

/**
 * 是否拥有Topic
 * @param type 数据源类型
 * @returns boolean
 */
export function haveTopic(type?: DATA_SOURCE_ENUM) {
	const list = [
		DATA_SOURCE_ENUM.KAFKA,
		DATA_SOURCE_ENUM.KAFKA_11,
		DATA_SOURCE_ENUM.KAFKA_09,
		DATA_SOURCE_ENUM.KAFKA_10,
		DATA_SOURCE_ENUM.KAFKA_2X,
		DATA_SOURCE_ENUM.TBDS_KAFKA,
		DATA_SOURCE_ENUM.KAFKA_HUAWEI,
		DATA_SOURCE_ENUM.KAFKA_CONFLUENT,
	];
	return type !== undefined && list.includes(type);
}

/**
 * 是否拥有分区
 * @param type 数据源类型
 * @returns boolean
 */
export function havePartition(type?: DATA_SOURCE_ENUM) {
	const list = [DATA_SOURCE_ENUM.IMPALA, DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR];
	return type !== undefined && list.includes(type);
}

/**
 * 是否拥有Schema
 * @param type 数据源类型
 * @returns boolean
 */
export function haveSchema(type?: DATA_SOURCE_ENUM) {
	const list = [
		DATA_SOURCE_ENUM.ORACLE,
		DATA_SOURCE_ENUM.POSTGRESQL,
		DATA_SOURCE_ENUM.KINGBASE8,
		DATA_SOURCE_ENUM.SQLSERVER,
		DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
	];
	return type && list.indexOf(type) > -1;
}
/**
 * 是否拥有表字段
 * S3 数据源的 Bucket 下拉框用的也是 TableList 的接口，表单字段也不是 table 是 bucket 。。
 * @param type 数据源类型
 * @returns boolean
 */
export function haveTableList(type?: DATA_SOURCE_ENUM) {
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
	return type !== undefined && list.includes(type);
}

/**
 * 是否拥有主键列的权限
 * @param type 数据源类型
 * @param version flink版本
 * @returns boolean
 */
export function havePrimaryKey(type: any, version?: string) {
	const list: any = [
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
	return list.includes(type) || (version === FLINK_VERSIONS.FLINK_1_12 && isKafka(type));
}

export function isMysqlTypeSource(type: number) {
	return [
		DATA_SOURCE_ENUM.MYSQL,
		DATA_SOURCE_ENUM.UPDRDB,
		DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
		DATA_SOURCE_ENUM.ORACLE,
		DATA_SOURCE_ENUM.SQLSERVER,
		DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
		DATA_SOURCE_ENUM.POSTGRESQL,
	].includes(type);
}
export function isHive(type: any) {
	return type === DATA_SOURCE_ENUM.HIVE;
}

/**
 * @param {Object} 参数
 * @param {string} param.version 版本
 * @param {number} param.value 数据源类型
 * @param {number[]} param.disabled112List 除 flink1.12 都支持
 * @param {number[]} param.allow110List 仅支持 flink1.10
 * @param {number[]} param.allow112List 仅支持 flink1.12
 * @returns diabled
 */
export function getFlinkDisabledSource({
	version,
	value,
	disabled112List,
	allow110List,
	allow112List,
}: {
	version: string;
	value: number;
	disabled112List?: number[];
	allow110List?: number[];
	allow112List?: number[];
}) {
	const ONLY_FLINK_1_12_DISABLED = disabled112List
		? version === '1.12' && disabled112List.includes(value)
		: false;
	const ONLY_ALLOW_FLINK_1_10_DISABLED = allow110List
		? (version === '1.12' || version === '1.8') && allow110List.includes(value)
		: false;
	const ONLY_ALLOW_FLINK_1_12_DISABLED = allow112List
		? (version === '1.10' || version === '1.8') && allow112List.includes(value)
		: false;
	return {
		ONLY_FLINK_1_12_DISABLED,
		ONLY_ALLOW_FLINK_1_10_DISABLED,
		ONLY_ALLOW_FLINK_1_12_DISABLED,
	};
}
export function isSqlServer(type: any) {
	return [DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.SQLSERVER_2017_LATER].includes(type);
}
/** 是否拥有数据预览 */
export function haveDataPreview(type: number) {
	const list: any = [
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
	return list.indexOf(type) > -1;
}

export function showTimeForOffsetReset(type?: DATA_SOURCE_ENUM) {
	return (
		type !== undefined &&
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
 * 是否拥有collection
 */
export function haveCollection(type: DATA_SOURCE_ENUM) {
	return [DATA_SOURCE_ENUM.SOLR].includes(type);
}

/**
 * 是否渲染 Bucket
 */
export function showBucket(type: DATA_SOURCE_ENUM) {
	return [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(type);
}

export function isS3(type?: DATA_SOURCE_ENUM) {
	return type !== undefined && [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(type);
}

export function isShowSchema(type: DATA_SOURCE_ENUM) {
	return (
		type === DATA_SOURCE_ENUM.ORACLE ||
		type === DATA_SOURCE_ENUM.POSTGRESQL ||
		type === DATA_SOURCE_ENUM.KINGBASE8 ||
		isSqlServer(type)
	);
}

export function isRedis(type?: DATA_SOURCE_ENUM) {
	return type !== undefined && [DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(type);
}

export function isES(type: DATA_SOURCE_ENUM): boolean {
	return [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7].includes(type);
}

export function isLowerES(type?: DATA_SOURCE_ENUM) {
	return type !== undefined && [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6].includes(type);
}
export function isHbase(type?: DATA_SOURCE_ENUM) {
	return (
		type !== undefined &&
		[
			DATA_SOURCE_ENUM.HBASE,
			DATA_SOURCE_ENUM.TBDS_HBASE,
			DATA_SOURCE_ENUM.HBASE_HUAWEI,
		].includes(type)
	);
}
/**
 * 是否有更新模式
 */
export function haveUpdateMode(type: number) {
	return ![
		DATA_SOURCE_ENUM.S3,
		DATA_SOURCE_ENUM.CSP_S3,
		DATA_SOURCE_ENUM.SOLR,
		DATA_SOURCE_ENUM.HIVE,
		DATA_SOURCE_ENUM.INCEPTOR,
	].includes(type);
}
/**
 * 是否允许更新模式切换
 */
export function haveUpsert(type: DATA_SOURCE_ENUM, version?: string) {
	const list: any = [
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
	return list.includes(type) || (version === FLINK_VERSIONS.FLINK_1_12 && isKafka(type));
}
/**
 * 更新模式为更新时，是否可以选择更新策略
 */
export function haveUpdateStrategy(type: number) {
	const list: any = [
		DATA_SOURCE_ENUM.MYSQL,
		DATA_SOURCE_ENUM.UPDRDB,
		DATA_SOURCE_ENUM.ORACLE,
		DATA_SOURCE_ENUM.TIDB,
	];
	return list.indexOf(type) > -1;
}
// 是否展示并行度
export function haveParallelism(type: number) {
	return ![DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR].includes(type);
}
/** 合并不同版本的 kafka 数据源 */
export const mergeSourceType = (type: number) => {
	if (
		[
			DATA_SOURCE_ENUM.KAFKA_2X,
			DATA_SOURCE_ENUM.KAFKA,
			DATA_SOURCE_ENUM.KAFKA_11,
			DATA_SOURCE_ENUM.KAFKA_09,
			DATA_SOURCE_ENUM.KAFKA_10,
		].includes(type)
	) {
		return DATA_SOURCE_ENUM.KAFKA_2X;
	}
	return type;
};
// 缓存策略是否只允许 ALL
export function isCacheOnlyAll(type?: DATA_SOURCE_ENUM) {
	return type && [DATA_SOURCE_ENUM.INCEPTOR].includes(type);
}

// 不支持 LRU 的情况（包含支持 None 的情况）
export function isCacheExceptLRU(type?: DATA_SOURCE_ENUM) {
	return (type && [DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(type)) || isCacheOnlyAll(type);
}
export function haveAsyncPoolSize(type: any) {
	const list: any = [
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
	return list.indexOf(type) > -1;
}
/**
 * 是否可以添加自定义参数
 * @param type
 * @returns
 */
export function haveCustomParams(type: any) {
	const list: any = [
		DATA_SOURCE_ENUM.REDIS,
		DATA_SOURCE_ENUM.UPRedis,
		DATA_SOURCE_ENUM.MONGODB,
		DATA_SOURCE_ENUM.ES,
		DATA_SOURCE_ENUM.HBASE,
		DATA_SOURCE_ENUM.HBASE_HUAWEI,
		DATA_SOURCE_ENUM.KUDU,
	];
	return list.indexOf(type) > -1;
}

/**
 * 根据任务 ID 来区分是否是任务 Tab
 */
export function isTaskTab(id?: UniqueId) {
	if (!id) return false;
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
