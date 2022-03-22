import { message } from 'antd';
import { cloneDeep } from 'lodash';
import { DATA_SYNC_MODE, TASK_TYPE_ENUM } from '@/constant';
import type { IOfflineTaskProps } from '@/interface';
import molecule from '@dtinsight/molecule';
import api from '@/api';
import type { ISyncDataProps } from '.';

type IDataSyncParamProps = IOfflineTaskProps &
	ISyncDataProps & {
		// 接口要求的标记位
		preSave: true;
		dependencyTasks: IOfflineTaskProps['taskVOS'];
		settingMap: ISyncDataProps['setting'];
	};

/**
 * 数据同步任务拼接参数
 */
export function generateRqtBody() {
	const currentTabData = molecule.editor.getState().current?.tab?.data;
	const reqBody: IDataSyncParamProps = cloneDeep(currentTabData);

	if (currentTabData.taskType === TASK_TYPE_ENUM.SYNC) {
		const DATASYNC_FIELDS = ['keymap', 'setting', 'sourceMap', 'targetMap'];
		if (DATASYNC_FIELDS.every((f) => currentTabData.hasOwnProperty(f) && currentTabData[f])) {
			const isIncrementMode =
				currentTabData.syncModel !== undefined &&
				DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;
			if (!isIncrementMode) {
				reqBody.sourceMap!.increColumn = undefined; // Delete increColumn
			}

			const { sourceMap, targetMap, keymap, setting } = reqBody;
			// 接口要求keymap中的连线映射数组放到sourceMap中
			sourceMap!.column = keymap!.source;
			targetMap!.column = keymap!.target;
			// 把 type 类型下的所有真值放到 sourceMap 下，type 只存放原 sourceMap.type.type 的值
			// put it into a tmp obj prevent lost
			let tmpObj = sourceMap!.type!;
			Object.keys(tmpObj).forEach((key) => {
				if ((tmpObj as any)[key] !== undefined) {
					sourceMap![key] = (tmpObj as any)[key];
				}
			});

			tmpObj = targetMap!.type!;
			// targetMap should keep consistent with sourceMap
			Object.keys(tmpObj).forEach((key) => {
				if ((tmpObj as any)![key] !== undefined) {
					targetMap![key] = (tmpObj as any)![key];
				}
			});

			// put setting into settingMap field and delete setting field
			reqBody.settingMap = setting;

			Reflect.deleteProperty(reqBody, 'keymap');
			Reflect.deleteProperty(reqBody, 'setting');
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
export function getStepStatus(data?: IOfflineTaskProps & ISyncDataProps): [boolean, number] {
	const step = 0;
	const isLoaded = false;
	if (!data) return [isLoaded, step];
	// 第一步的 sourceId 存在但第二步的 sourceId 不存在则表示当前停留在第一步
	if (data.sourceMap?.sourceId && data.targetMap?.sourceId === undefined) {
		return [true, 0];
	}
	// 第二步的 sourceId 存在但第三步的连线不存在则表示停留在第二步
	if (data.targetMap?.sourceId && data.keymap?.source.length === 0) {
		return [true, 1];
	}
	if (data.keymap?.source.length && !data.setting) {
		return [true, 2];
	}
	if (data.setting) {
		return [true, 4];
	}
	return [false, 0];
}
