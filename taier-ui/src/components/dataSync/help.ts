import { message } from 'antd';
import { cloneDeep } from 'lodash';
import { DATA_SYNC_MODE } from '@/constant';
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
	const DATASYNC_FIELDS = ['keymap', 'setting', 'sourceMap', 'targetMap'];
	if (DATASYNC_FIELDS.every((f) => currentTabData.hasOwnProperty(f) && currentTabData[f])) {
		const reqBody: IDataSyncParamProps = cloneDeep(currentTabData);
		const isIncrementMode =
			currentTabData.syncModel !== undefined &&
			DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;

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

		if (!isIncrementMode) {
			reqBody.sourceMap!.increColumn = undefined; // Delete increColumn
		}

		// 修改task配置时接口要求的标记位
		reqBody.preSave = true;

		// 接口要求上游任务字段名修改为dependencyTasks
		if (reqBody.taskVOS) {
			reqBody.dependencyTasks = reqBody.taskVOS.concat();
			reqBody.taskVOS = null;
		}

		Reflect.deleteProperty(reqBody, 'keymap');
		Reflect.deleteProperty(reqBody, 'setting');

		// 数据拼装结果
		return reqBody;
	}
	message.error('请检查数据同步任务是否填写正确');
	return null;
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
