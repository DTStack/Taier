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

import { FLINK_VERSIONS, SOURCE_TIME_TYPE } from '@/constant';
import { isAvro, isKafka } from '@/utils/enums';
import type { Rule } from 'antd/lib/form';
import { checkColumnsData } from '../taskFunc';
import type { PendingInputColumnType } from './flinkSource';

export function parseColumnText(text = '') {
	const columns = text
		.split('\n')
		.filter(Boolean)
		.map((v) => {
			const asCase = /^.*\w.*\s+as\s+(\w+)$/i.exec(v);
			if (asCase) {
				return {
					column: asCase[1],
				};
			}
			const [column, type] = v.trim().split(' ');

			return { column, type };
		})
		.filter((v) => v.column);
	return columns;
}

export function dimensionDefaultValue(key: string): boolean {
	const keys = [
		'table',
		'tableName',
		'columns',
		'index',
		'esType',
		'parallelism',
		'columnsText',
		'partitionedJoin',
		'lowerBoundPrimaryKey',
		'upperBoundPrimaryKey',
		'keyFilter',
		'isFaultTolerant',
		'cache',
		'cacheSize',
		'cacheTTLMs',
		'errorLimit',
		'primaryKey',
		'hbasePrimaryKey',
		'hbasePrimaryKeyType',
		'advanConf',
		'schema',
		'asyncPoolSize',
		'createType',
		'dbId',
		'tableId',
	];
	return keys.indexOf(key) > -1;
}

/**
 * 根据 flink 版本动态生成规则
 * @param data
 * @param componentVersion
 * @returns
 */
export const generateSourceValidDes = (
	data?: PendingInputColumnType,
	componentVersion?: string,
): Record<string, Rule[]> => {
	const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;
	const haveSchema = !!(
		isKafka(data?.type) &&
		isAvro(data?.sourceDataType) &&
		componentVersion !== FLINK_VERSIONS.FLINK_1_12
	);

	return {
		type: [{ required: true, message: '请选择类型' }],
		sourceId: [{ required: true, message: '请选择数据源' }],
		topic: [{ required: true, message: '请选择Topic' }],
		table: [{ required: true, message: '请输入映射表名' }],
		columnsText: [{ required: true, message: '字段信息不能为空！' }],
		sourceDataType: [{ required: isKafka(data?.type), message: '请选择读取类型' }],
		schemaInfo: [{ required: haveSchema, message: '请输入Schema' }],
		timeColumn: [
			{
				required:
					(!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
					(isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
				message: '请选择时间列',
			},
		],
		offset: [
			{
				required:
					(!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
					(isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
				message: '请输入最大延迟时间',
			},
		],
	};
};
