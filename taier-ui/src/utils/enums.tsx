import Circle from '@/components/circle';
import {
	DATA_SOURCE_ENUM,
	ENGINE_SOURCE_TYPE_ENUM,
	RESOURCE_TYPE,
	TASK_PERIOD_ENUM,
	TASK_STATUS,
	TASK_TYPE_ENUM,
} from '@/constant';

export function taskTypeText(type: TASK_TYPE_ENUM) {
	switch (type) {
		case TASK_TYPE_ENUM.MR:
			return 'Spark';
		case TASK_TYPE_ENUM.SYNC:
			return '数据同步';
		case TASK_TYPE_ENUM.VIRTUAL_NODE:
			return '虚节点';
		case TASK_TYPE_ENUM.PYTHON_23:
			return 'Python';
		case TASK_TYPE_ENUM.PYTHON:
			return 'PySpark';
		case TASK_TYPE_ENUM.R:
			return 'R';
		case TASK_TYPE_ENUM.SQL:
			return 'SparkSQL';
		case TASK_TYPE_ENUM.SHELL:
			return 'Shell';
		case TASK_TYPE_ENUM.DEEP_LEARNING:
			return '深度学习';
		case TASK_TYPE_ENUM.ML:
			return '机器学习';
		case TASK_TYPE_ENUM.HAHDOOPMR:
			return 'HadoopMR';
		case TASK_TYPE_ENUM.WORKFLOW:
			return '工作流';
		case TASK_TYPE_ENUM.CARBONSQL:
			return 'CarbonSQL';
		case TASK_TYPE_ENUM.NOTEBOOK:
			return 'Notebook';
		case TASK_TYPE_ENUM.EXPERIMENT:
			return '算法实验';
		case TASK_TYPE_ENUM.LIBRASQL:
			return 'LibraSQL';
		case TASK_TYPE_ENUM.IMPALA_SQL:
			return 'ImpalaSQL';
		case TASK_TYPE_ENUM.CUBE_KYLIN:
			return 'Kylin';
		case TASK_TYPE_ENUM.HIVESQL:
			return 'HiveSQL';
		case TASK_TYPE_ENUM.TI_DB_SQL:
			return 'TiDBSQL';
		case TASK_TYPE_ENUM.ORACLE_SQL:
			return 'OracleSQL';
		case TASK_TYPE_ENUM.GREEN_PLUM_SQL:
			return 'GreenPlumSQL';
		case TASK_TYPE_ENUM.TENSORFLOW_1X:
			return 'TensorFlow 1.x';
		case TASK_TYPE_ENUM.KERAS:
			return 'Keras';
		case TASK_TYPE_ENUM.PRESTO:
			return 'Presto';
		case TASK_TYPE_ENUM.PYTORCH:
			return 'PyTorch';
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

export function linkMapping(key?: DATA_SOURCE_ENUM) {
	switch (key) {
		case DATA_SOURCE_ENUM.MYSQL:
		case DATA_SOURCE_ENUM.CARBONDATA:
		case DATA_SOURCE_ENUM.CLICKHOUSE:
		case DATA_SOURCE_ENUM.POLAR_DB_For_MySQL:
		case DATA_SOURCE_ENUM.ORACLE:
		case DATA_SOURCE_ENUM.SQLSERVER:
		case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER:
		case DATA_SOURCE_ENUM.DB2:
		case DATA_SOURCE_ENUM.POSTGRESQL:
		case DATA_SOURCE_ENUM.TIDB:
		case DATA_SOURCE_ENUM.KINGBASE8:
		case DATA_SOURCE_ENUM.GBASE_8A:
		case DATA_SOURCE_ENUM.PRESTO:
		case DATA_SOURCE_ENUM.VERTICA:
		case DATA_SOURCE_ENUM.ADB_FOR_PG:
		case DATA_SOURCE_ENUM.HIVE3_CDP:
		case DATA_SOURCE_ENUM.DORIS:
			return [
				['jdbcUrl', 'jdbcUrl'],
				['username', '用户名'],
			];
		case DATA_SOURCE_ENUM.KUDU:
			return [['hostPorts', '集群地址']];
		case DATA_SOURCE_ENUM.IMPALA:
		case DATA_SOURCE_ENUM.HIVE:
		case DATA_SOURCE_ENUM.HIVE1X:
		case DATA_SOURCE_ENUM.HIVE3X:
		case DATA_SOURCE_ENUM.INCEPTOR:
		case DATA_SOURCE_ENUM.SPARKTHRIFT:
			return [
				['jdbcUrl', 'jdbcUrl'],
				['defaultFS', 'defaultFS'],
			];
		case DATA_SOURCE_ENUM.HBASE:
		case DATA_SOURCE_ENUM.HBASE2:
			return [['hbase_quorum', 'Zookeeper集群地址']];
		case DATA_SOURCE_ENUM.HDFS:
			return [['defaultFS', 'defaultFS']];
		case DATA_SOURCE_ENUM.FTP:
			return [
				['protocol', 'Protocol'],
				['host', 'Host'],
				['port', 'Port'],
				['username', '用户名'],
			];
		case DATA_SOURCE_ENUM.MAXCOMPUTE:
			return [
				['endPoint', 'endPoint'],
				['project', '项目名称'],
				['accessId', 'Access Id'],
			];
		case DATA_SOURCE_ENUM.ES:
		case DATA_SOURCE_ENUM.ES6:
		case DATA_SOURCE_ENUM.ES7:
			return [['address', '集群地址']];
		case DATA_SOURCE_ENUM.REDIS:
			return [
				['hostPort', '地址'],
				['database', '数据库'],
			];
		case DATA_SOURCE_ENUM.MONGODB:
			return [
				['hostPorts', '集群地址'],
				['database', '数据库'],
			];
		case DATA_SOURCE_ENUM.KAFKA_11:
		case DATA_SOURCE_ENUM.KAFKA_09:
		case DATA_SOURCE_ENUM.KAFKA_10:
		case DATA_SOURCE_ENUM.KAFKA:
		case DATA_SOURCE_ENUM.KAFKA_2X:
			return [
				['address', '集群地址'],
				['brokerList', 'broker地址'],
			];
		case DATA_SOURCE_ENUM.EMQ:
			return [['address', 'Broker URL']];
		case DATA_SOURCE_ENUM.S3:
			return [['hostname', 'hostname']];
		case DATA_SOURCE_ENUM.WEBSOCKET:
		case DATA_SOURCE_ENUM.SOCKET:
			return [['url', 'url']];
		case DATA_SOURCE_ENUM.GREENPLUM6:
		case DATA_SOURCE_ENUM.PHOENIX:
			return [['jdbcUrl', 'jdbcUrl']];
		case DATA_SOURCE_ENUM.Kylin:
			return [
				['authURL', 'authURL'],
				['username', '用户名'],
			];
		case DATA_SOURCE_ENUM.SOLR:
			return [
				['zkHost', '集群地址'],
				['chroot', 'chroot路径'],
			];
		case DATA_SOURCE_ENUM.INFLUXDB:
			return [
				['url', 'URL'],
				['username', '用户名'],
			];
		case DATA_SOURCE_ENUM.AWS_S3:
			return [['accessKey', 'ACCESS KEY']];
		case DATA_SOURCE_ENUM.OPENTSDB:
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
			return (
				<span>
					<Circle type="finished" />
					&nbsp; {taskStatusText(value)}
				</span>
			);
		case TASK_STATUS.STOPED:
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

export function getResourceName(): Partial<Record<RESOURCE_TYPE, string>> {
	return {
		[RESOURCE_TYPE.JAR]: 'jar',
		[RESOURCE_TYPE.PY]: 'py',
		[RESOURCE_TYPE.EGG]: 'egg',
		[RESOURCE_TYPE.ZIP]: 'zip',
		[RESOURCE_TYPE.OTHER]: '其它',
	};
}
