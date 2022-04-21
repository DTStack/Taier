import type { IFlinkSinkProps } from '@/interface';
import { Utils } from '@dtinsight/dt-utils';
import type { Moment } from 'moment';
import moment from 'moment';
/**
 * 生成form Mapfield方法的自定义属性对象
 * @param {*} customParams
 */
export function generateMapValues(customParams: any = []) {
	const map: any = {};
	customParams.forEach((customParam: any) => {
		map[`${customParam.id}-key`] = customParam.key;
		map[`${customParam.id}-value`] = customParam.value;
	});
	return map;
}
/**
 * 改变自定义参数触发事件
 * @param {*} panel
 * @param {*} value
 * @param {*} id
 * @param {*} type
 */
export function changeCustomParams(panel: any, value: any, extParam: any = {}) {
	/**
	 * 这边取得传进来的customParams的引用，以便直接操作原对象
	 */
	const customParams = panel.customParams ? panel.customParams : (panel.customParams = []);
	/**
	 * 检查是不是新增一个自定义参数
	 */
	if (extParam.type == 'newCustomParam') {
		customParams.push({
			id: Utils.generateAKey(),
		});
		return;
	} else if (extParam.type == 'deleteCustomParam') {
		/**
		 * 检查是不是删除自定义参数
		 */
		const index = customParams.findIndex((customParam: any) => {
			return customParam.id == extParam.id;
		});
		if (index || index == 0) {
			customParams.splice(index, 1);
		}
		return;
	}
	const param = customParams.find((customParam: any) => {
		return customParam.id == extParam.id;
	});
	if (param) {
		param[extParam.type] = value;
	}
}

/**
 * 为不存在 id 的自定义参数生成随机 id
 */
export function initCustomParam(panel: IFlinkSinkProps) {
	const { customParams = [] } = panel;
	for (let i = 0; i < customParams.length; i += 1) {
		const customParam = customParams[i];
		if (!customParam.id) {
			customParam.id = Utils.generateAKey();
		}
	}
}

export function getColumnsByColumnsText(text: string = '') {
	const columns: { field: string; type: string }[] = [];
	const tmpMap: Record<string, boolean> = {};
	if (text) {
		text.split('\n')
			.filter(Boolean)
			.forEach((v) => {
				const asCase = /^\s*(.+)\s+(.+)\s*$/i.exec(v?.trim());
				if (asCase && !tmpMap[asCase[1]]) {
					tmpMap[asCase[1]] = true;
					columns.push({
						field: asCase[1],
						type: asCase[2],
					});
				}
			});
	}
	return columns;
}

export const offsetResetFormat = 'YYYY-MM-DD HH:mm:ss';

export const formatOffsetResetTime = (timestamp: number): Moment => {
	const dateString = moment(timestamp).format(offsetResetFormat);
	return moment(dateString, offsetResetFormat);
};
