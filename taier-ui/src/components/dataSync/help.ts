import { message } from 'antd';
import { cloneDeep } from 'lodash';
import { DATA_SOURCE_ENUM, DATA_SYNC_MODE, rdbmsDaType, TASK_TYPE_ENUM } from '@/constant';
import type { IOfflineTaskProps, ISyncDataProps } from '@/interface';
import molecule from '@dtinsight/molecule';
import api from '@/api';

type IDataSyncParamProps = IOfflineTaskProps &
	ISyncDataProps & {
		// 接口要求的标记位
		preSave: true;
		dependencyTasks: IOfflineTaskProps['taskVOS'];
		/**
		 * the monaco editor content
		 */
		value: string;
	};

/**
 * 数据同步任务拼接参数
 */
export function generateRqtBody() {
	const currentTabData = molecule.editor.getState().current?.tab?.data;
	const reqBody: IDataSyncParamProps = cloneDeep(currentTabData);

	if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
		const DATASYNC_FIELDS = ['settingMap', 'sourceMap', 'targetMap'];
		if (DATASYNC_FIELDS.every((f) => currentTabData.hasOwnProperty(f) && currentTabData[f])) {
			const isIncrementMode =
				currentTabData.syncModel !== undefined &&
				DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;
			if (!isIncrementMode) {
				reqBody.sourceMap!.increColumn = undefined; // Delete increColumn
			}

			// 服务端需要的参数
			reqBody.sourceMap!.rdbmsDaType = rdbmsDaType.Poll;
		} else {
			message.error('请检查数据同步任务是否填写正确');
			return null;
		}
	}

	// 修改task配置时接口要求的标记位
	reqBody.preSave = true;

	// 接口要求上游任务字段名修改为dependencyTasks
	if (reqBody.taskVOS) {
		reqBody.dependencyTasks = reqBody.taskVOS.map((o: any) => o);
		reqBody.taskVOS = null;
	}

	reqBody.sqlText = reqBody.value;
	// 数据拼装结果
	return reqBody;
}

/**
 * 保存数据同步任务
 */
export function saveTask() {
	const params = generateRqtBody();
	if (params) {
		return api.saveOfflineJobData(params).then((res) => {
			if (res.code === 1) {
				message.success('保存成功！');
				return res;
			}
		});
	}
}

/**
 * 根据 data 来判断是否获取过数据，已经当前的步骤
 */
export function getStepStatus(data?: IOfflineTaskProps): [boolean, number] {
	const step = 0;
	const isLoaded = false;
	if (!data) return [isLoaded, step];
	// 第一步的 sourceId 存在但第二步的 sourceId 不存在则表示当前停留在第一步
	if (data.sourceMap?.sourceId && data.targetMap?.sourceId === undefined) {
		return [true, 0];
	}
	// 第二步的 sourceId 存在但第三步的连线不存在则表示停留在第二步
	if (data.targetMap?.sourceId && data.sourceMap?.column?.length === 0) {
		return [true, 1];
	}
	if (data.sourceMap?.column?.length && !data.settingMap) {
		return [true, 2];
	}
	if (data.settingMap) {
		return [true, 4];
	}
	return [false, 0];
}

export const noWhiteSpace = (_: any, value: string) => {
	if (/\s/.test(value)) {
		return Promise.reject(new Error('不支持空格！'));
	}
	return Promise.resolve();
};

// 目标表为以下数据源的时候，支持生成目标表
export const ALLOW_CREATE_TABLE_IN_TARGET = [
	DATA_SOURCE_ENUM.MYSQL,
	DATA_SOURCE_ENUM.ORACLE,
	DATA_SOURCE_ENUM.SQLSERVER,
	DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
	DATA_SOURCE_ENUM.POSTGRESQL,
	DATA_SOURCE_ENUM.HIVE,
	DATA_SOURCE_ENUM.HIVE3X,
	DATA_SOURCE_ENUM.HIVE1X,
	DATA_SOURCE_ENUM.HIVE3_CDP,
	DATA_SOURCE_ENUM.DB2,
	DATA_SOURCE_ENUM.ADS,
	DATA_SOURCE_ENUM.CARBONDATA,
	DATA_SOURCE_ENUM.GBASE_8A,
	DATA_SOURCE_ENUM.LIBRA,
	DATA_SOURCE_ENUM.CLICKHOUSE,
	DATA_SOURCE_ENUM.POLAR_DB_For_MySQL,
	DATA_SOURCE_ENUM.IMPALA,
	DATA_SOURCE_ENUM.PHOENIX,
	DATA_SOURCE_ENUM.TIDB,
	DATA_SOURCE_ENUM.DMDB,
	DATA_SOURCE_ENUM.DMDB_For_Oracle,
	DATA_SOURCE_ENUM.GREENPLUM6,
	DATA_SOURCE_ENUM.MAXCOMPUTE,
	DATA_SOURCE_ENUM.ADB_FOR_PG,
	DATA_SOURCE_ENUM.SPARKTHRIFT,
	DATA_SOURCE_ENUM.INCEPTOR,
];

// 源表为以下数据源的时候，支持生成目标表
export const ALLOW_CREATE_TABLE_IN_SOURCE = [
	DATA_SOURCE_ENUM.HIVE,
	DATA_SOURCE_ENUM.HIVE3X,
	DATA_SOURCE_ENUM.HIVE1X,
	DATA_SOURCE_ENUM.HIVE3_CDP,
	DATA_SOURCE_ENUM.SPARKTHRIFT,
	DATA_SOURCE_ENUM.TIDB,
	DATA_SOURCE_ENUM.LIBRA,
	DATA_SOURCE_ENUM.POSTGRESQL,
	DATA_SOURCE_ENUM.MYSQL,
	DATA_SOURCE_ENUM.ADB_FOR_PG,
	DATA_SOURCE_ENUM.DORIS,
];
