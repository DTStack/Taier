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

import { JoinType } from '../../types';
import { holder } from '../constants';
import { EnumSize } from '../types';

export const relationListColumns = (size: EnumSize) => {
	const isLarge = size === EnumSize.LARGE;
	return [
		{
			title: '表别名',
			dataIndex: 'tableAlias',
			key: 'tableAlias',
			render: holder,
			width: isLarge ? 120 : 100,
			ellipsis: true,
		},
		{
			title: '数据库',
			dataIndex: 'schema',
			key: 'schema',
			render: holder,
			width: isLarge ? 200 : 100,
			ellipsis: true,
		},
		{
			title: '表名',
			dataIndex: 'table',
			key: 'table',
			render: holder,
			width: isLarge ? 200 : 100,
			ellipsis: true,
		},
		{
			title: '关联类型',
			dataIndex: 'joinType',
			key: 'joinType',
			width: isLarge ? 120 : 100,
			ellipsis: true,
			render: (type) => {
				switch (type) {
					case JoinType.LEFT_JOIN:
						return 'Left Join';
					case JoinType.RIGHT_JOIN:
						return 'Right Join';
					case JoinType.INNER_JOIN:
						return 'Inner Join';
					default:
						return 'error type';
				}
			},
		},
		{
			title: '关联条件',
			dataIndex: 'joinPairs',
			key: 'joinPairs',
			width: isLarge ? 300 : 238,
			ellipsis: true,
			render: (joinPairs, record) => {
				return joinPairs
					.reduce((temp, cur) => {
						// TODO: 逻辑重复，可优化
						const ltTable = record.leftTableAlias;
						const ltCol = cur.leftValue.columnName;
						const rtTable = record.tableAlias;
						const rtCol = cur.rightValue.columnName;
						return `${temp}${ltTable}.${ltCol} = ${rtTable}.${rtCol} and `;
					}, '')
					.replace(/ and $/, '');
			},
		},
	];
};

export const columns = (size: EnumSize) => {
	const isLarge = size === EnumSize.LARGE;
	return [
		{
			title: '表',
			dataIndex: 'tableName',
			key: 'tableName',
			render: holder,
			ellipsis: true,
			width: isLarge ? 200 : 160,
		},
		{
			title: '字段名称',
			dataIndex: 'columnName',
			key: 'columnName',
			render: holder,
			ellipsis: true,
			width: isLarge ? 200 : 160,
		},
		{
			title: '字段描述',
			dataIndex: 'columnComment',
			key: 'columnComment',
			ellipsis: true,
			render: holder,
			width: isLarge ? 200 : 160,
		},
		{
			title: '字段类型',
			dataIndex: 'columnType',
			key: 'columnType',
			ellipsis: true,
			render: holder,
			width: isLarge ? 200 : 158,
		},
	];
};
