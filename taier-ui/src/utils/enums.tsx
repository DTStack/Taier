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
    RESOURCE_TYPE,
    TASK_LANGUAGE,
    TASK_PERIOD_ENUM,
    TASK_STATUS,
    TASK_TYPE_ENUM,
} from '@/constant';

export function taskStatusText(type?: TASK_STATUS) {
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

export function TaskTimeType(props: { value?: TASK_PERIOD_ENUM }) {
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

export function TaskStatus(props: { value?: TASK_STATUS }) {
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
        case TASK_TYPE_ENUM.DORIS:
        case TASK_TYPE_ENUM.CLICKHOUSE:
            return TASK_LANGUAGE.SPARKSQL;
        case TASK_TYPE_ENUM.HIVE_SQL:
            return TASK_LANGUAGE.HIVESQL;
        case TASK_TYPE_ENUM.SQL:
            return TASK_LANGUAGE.FLINKSQL;
        case TASK_TYPE_ENUM.OCEANBASE:
            return TASK_LANGUAGE.SQL;
        case TASK_TYPE_ENUM.SYNC:
        case TASK_TYPE_ENUM.DATA_ACQUISITION:
        case TASK_TYPE_ENUM.DATAX:
            return TASK_LANGUAGE.JSON;
        case TASK_TYPE_ENUM.PYTHON:
            return TASK_LANGUAGE.PYTHON;
        case TASK_TYPE_ENUM.SHELL:
            return TASK_LANGUAGE.SHELL;
        case TASK_TYPE_ENUM.MYSQL:
            return TASK_LANGUAGE.MYSQL;
        default:
            return TASK_LANGUAGE.SQL;
    }
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
    const ONLY_FLINK_1_12_DISABLED = disabled112List ? version === '1.12' && disabled112List.includes(value) : false;
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
