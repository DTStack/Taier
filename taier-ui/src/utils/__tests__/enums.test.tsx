import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
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
import {
    getEngineSourceTypeName,
    getFlinkDisabledSource,
    linkMapping,
    mappingTaskTypeToLanguage,
    resourceNameMapping,
    TaskStatus,
    taskStatusText,
    TaskTimeType,
} from '../enums';

describe('enums', () => {
    it('Render Task Status Text', () => {
        expect(taskStatusText(TASK_STATUS.AUTO_CANCEL)).toBe('自动取消');
        expect(taskStatusText(TASK_STATUS.COMPUTING)).toBe('计算中');
        expect(taskStatusText(TASK_STATUS.CREATED)).toBe('数据同步');
        expect(taskStatusText(TASK_STATUS.DEPLOYING)).toBe('部署中');
        expect(taskStatusText(TASK_STATUS.DO_FAIL)).toBe('失败');
        expect(taskStatusText(TASK_STATUS.ENGINEACCEPTED)).toBe('提交至引擎');
        expect(taskStatusText(TASK_STATUS.FINISHED)).toBe('成功');
        expect(taskStatusText(TASK_STATUS.FROZEN)).toBe('冻结');
        expect(taskStatusText(TASK_STATUS.INVOKED)).toBe('已调度');
        expect(taskStatusText(TASK_STATUS.KILLED)).toBe('已停止');
        expect(taskStatusText(TASK_STATUS.LACKING)).toBe('异常');
        expect(taskStatusText(TASK_STATUS.PARENT_FAILD)).toBe('上游失败');
        expect(taskStatusText(TASK_STATUS.RESTARTING)).toBe('重试中');
        expect(taskStatusText(TASK_STATUS.RUNNING)).toBe('运行中');
        expect(taskStatusText(TASK_STATUS.RUN_FAILED)).toBe('运行失败');
        expect(taskStatusText(TASK_STATUS.SET_SUCCESS)).toBe('设置成功');
        expect(taskStatusText(TASK_STATUS.STOPED)).toBe('取消');
        expect(taskStatusText(TASK_STATUS.STOPING)).toBe('取消中');
        expect(taskStatusText(TASK_STATUS.SUBMITTED)).toBe('已提交');
        expect(taskStatusText(TASK_STATUS.SUBMITTING)).toBe('提交中');
        expect(taskStatusText(TASK_STATUS.SUBMIT_FAILED)).toBe('提交失败');
        expect(taskStatusText(TASK_STATUS.TASK_STATUS_NOT_FOUND)).toBe('运行中');
        expect(taskStatusText(TASK_STATUS.WAIT_COMPUTE)).toBe('等待计算');
        expect(taskStatusText(TASK_STATUS.WAIT_RUN)).toBe('等待运行');
        expect(taskStatusText(TASK_STATUS.WAIT_SUBMIT)).toBe('等待提交');
    });

    it('Render Link Info Text', () => {
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.MYSQL])).toEqual([
            ['jdbcUrl', 'jdbcUrl'],
            ['username', '用户名'],
        ]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KUDU])).toEqual([['hostPorts', '集群地址']]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.IMPALA])).toEqual([
            ['jdbcUrl', 'jdbcUrl'],
            ['defaultFS', 'defaultFS'],
        ]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HBASE])).toEqual([['hbase_quorum', 'Zookeeper集群地址']]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.HDFS])).toEqual([['defaultFS', 'defaultFS']]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.FTP])).toEqual([
            ['protocol', 'Protocol'],
            ['host', 'Host'],
            ['port', 'Port'],
            ['username', '用户名'],
        ]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.MAXCOMPUTE])).toEqual([
            ['endPoint', 'endPoint'],
            ['project', '项目名称'],
            ['accessId', 'Access Id'],
        ]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.ES])).toEqual([['address', '集群地址']]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.REDIS])).toEqual([
            ['hostPort', '地址'],
            ['database', '数据库'],
        ]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.MONGODB])).toEqual([
            ['hostPorts', '集群地址'],
            ['database', '数据库'],
        ]);
        expect(linkMapping(DATA_SOURCE_TEXT[DATA_SOURCE_ENUM.KAFKA_11])).toEqual([
            ['address', '集群地址'],
            ['brokerList', 'broker地址'],
        ]);
    });

    it('Render Time Text', () => {
        let component = render(TaskTimeType({ value: TASK_PERIOD_ENUM.DAY }));
        expect(component.asFragment()).toMatchInlineSnapshot(`
		<DocumentFragment>
		  <span>
		    天任务
		  </span>
		</DocumentFragment>
	`);

        component = render(TaskTimeType({ value: TASK_PERIOD_ENUM.HOUR }));
        expect(component.asFragment()).toMatchInlineSnapshot(`
		<DocumentFragment>
		  <span>
		    小时任务
		  </span>
		</DocumentFragment>
	`);

        component = render(TaskTimeType({ value: TASK_PERIOD_ENUM.MINUTE }));
        expect(component.asFragment()).toMatchInlineSnapshot(`
		<DocumentFragment>
		  <span>
		    分钟任务
		  </span>
		</DocumentFragment>
	`);

        component = render(TaskTimeType({ value: TASK_PERIOD_ENUM.MONTH }));
        expect(component.asFragment()).toMatchInlineSnapshot(`
		<DocumentFragment>
		  <span>
		    月任务
		  </span>
		</DocumentFragment>
	`);

        component = render(TaskTimeType({ value: TASK_PERIOD_ENUM.WEEK }));
        expect(component.asFragment()).toMatchInlineSnapshot(`
		<DocumentFragment>
		  <span>
		    周任务
		  </span>
		</DocumentFragment>
	`);

        // default is day
        component = render(TaskTimeType({ value: 1000 }));
        expect(component.asFragment()).toMatchInlineSnapshot(`
		<DocumentFragment>
		  <span>
		    天任务
		  </span>
		</DocumentFragment>
	`);
    });

    it('Redner Task Status', () => {
        let component = render(TaskStatus({ value: TASK_STATUS.AUTO_CANCEL }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.COMPUTING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.CREATED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.DEPLOYING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.DO_FAIL }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.ENGINEACCEPTED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.FINISHED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.FROZEN }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.INVOKED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.KILLED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.LACKING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.PARENT_FAILD }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.RESTARTING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.RUNNING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.RUN_FAILED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.SET_SUCCESS }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.STOPED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.STOPING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.SUBMITTED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.SUBMITTING }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.SUBMIT_FAILED }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.TASK_STATUS_NOT_FOUND }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.WAIT_COMPUTE }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.WAIT_RUN }));
        expect(component.asFragment()).toMatchSnapshot();

        component = render(TaskStatus({ value: TASK_STATUS.WAIT_SUBMIT }));
        expect(component.asFragment()).toMatchSnapshot();
    });

    it('Render Engine Text', () => {
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.ADB)).toBe('AnalyticDB PostgreSQL');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.DB2)).toBe('DB2');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.FLINK_ON_STANDALONE)).toBe('Flink on Standalone');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.GREEN_PLUM)).toBe('Greenplum');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.HADOOP)).toBe('Hadoop');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.KUBERNETES)).toBeUndefined();
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.LIBRA)).toBe('LibrA');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.MYSQL)).toBe('MySQL');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.OCEANBASE)).toBe('OceanBase');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.ORACLE)).toBe('Oracle');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.PRESTO)).toBe('Presto');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.SQLSERVER)).toBe('SQLServer');
        expect(getEngineSourceTypeName(ENGINE_SOURCE_TYPE_ENUM.TI_DB)).toBe('TiDB');
    });

    it('Render Resource Ext Text', () => {
        expect(resourceNameMapping(RESOURCE_TYPE.EGG)).toBe('egg');
        expect(resourceNameMapping(RESOURCE_TYPE.JAR)).toBe('jar');
        expect(resourceNameMapping(RESOURCE_TYPE.KUBERNETES)).toBe('未知');
        expect(resourceNameMapping(RESOURCE_TYPE.OTHER)).toBe('其它');
        expect(resourceNameMapping(RESOURCE_TYPE.PY)).toBe('py');
        expect(resourceNameMapping(RESOURCE_TYPE.YARN)).toBe('未知');
        expect(resourceNameMapping(RESOURCE_TYPE.ZIP)).toBe('zip');
        expect(resourceNameMapping()).toBe('未知');
    });

    it('Mapping Type To Language', () => {
        // 默认值是 TASK_LANGUAGE.SQL
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.DATA_ACQUISITION)).toBe(TASK_LANGUAGE.JSON);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.OCEANBASE)).toBe(TASK_LANGUAGE.SQL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.HIVE_SQL)).toBe(TASK_LANGUAGE.HIVESQL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.SHELL)).toBe(TASK_LANGUAGE.SHELL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.SPARK)).toBe(TASK_LANGUAGE.SQL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.SPARK_SQL)).toBe(TASK_LANGUAGE.SPARKSQL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.SQL)).toBe(TASK_LANGUAGE.FLINKSQL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.SYNC)).toBe(TASK_LANGUAGE.JSON);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.VIRTUAL)).toBe(TASK_LANGUAGE.SQL);
        expect(mappingTaskTypeToLanguage(TASK_TYPE_ENUM.WORK_FLOW)).toBe(TASK_LANGUAGE.SQL);
    });

    it('getFlinkDisabledSource', () => {
        expect(getFlinkDisabledSource({ version: '1.12', value: 0 })).toEqual({
            ONLY_FLINK_1_12_DISABLED: false,
            ONLY_ALLOW_FLINK_1_10_DISABLED: false,
            ONLY_ALLOW_FLINK_1_12_DISABLED: false,
        });

        expect(getFlinkDisabledSource({ version: '1.12', value: 0, disabled112List: [0] })).toEqual({
            ONLY_FLINK_1_12_DISABLED: true,
            ONLY_ALLOW_FLINK_1_10_DISABLED: false,
            ONLY_ALLOW_FLINK_1_12_DISABLED: false,
        });

        expect(
            getFlinkDisabledSource({
                version: '1.12',
                value: 0,
                disabled112List: [0],
                allow110List: [0],
            })
        ).toEqual({
            ONLY_FLINK_1_12_DISABLED: true,
            ONLY_ALLOW_FLINK_1_10_DISABLED: true,
            ONLY_ALLOW_FLINK_1_12_DISABLED: false,
        });

        expect(
            getFlinkDisabledSource({
                version: '1.12',
                value: 0,
                disabled112List: [0],
                allow112List: [0],
            })
        ).toEqual({
            ONLY_FLINK_1_12_DISABLED: true,
            ONLY_ALLOW_FLINK_1_10_DISABLED: false,
            ONLY_ALLOW_FLINK_1_12_DISABLED: false,
        });
    });
});
