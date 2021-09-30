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

import React from 'react';
import { FieldColumn } from 'pages/DataModel/types';
import { Input } from 'antd';
import _ from 'lodash';

// interface Column {
//   dataIndex?: keyof FieldColumn;
//   title: string;
//   width?: number;
//   render?: any;
// }

export const data: FieldColumn[] = [
	{
		columnComment: 'aaa',
		columnName: 'bbb',
		columnType: 'ccc',
		schema: 'schema',
		tableName: 'tableName',
	},
	{
		columnComment: 'aaa',
		columnName: 'bbb',
		columnType: 'ccc',
		schema: 'schema',
		tableName: 'tableName',
	},
	{
		columnComment: 'aaa',
		columnName: 'bbb',
		columnType: 'ccc',
		schema: 'schema',
		tableName: 'tableName',
	},
];

export const idGenerator = () => {
	let _id = 0;
	return () => ++_id;
};

export const columnsGenerator = ({ onInputBlur, data }): any[] => {
	return [
		{
			title: '序号',
			width: 80,
			render: (value, record, index) => index + 1,
			ellipsis: true,
		},
		{
			title: '表',
			dataIndex: 'tableName',
			width: 120,
			filters: _.uniqBy(data, (item) => (item as any).tableName).map((item) => ({
				text: (item as any).tableName,
				value: (item as any).tableName,
			})),
			onFilter: (value, record) => value === record.tableName,
			ellipsis: true,
		},
		// TODO: 字段
		{
			title: '表别名',
			width: 120,
			dataIndex: 'tableAlias',
			ellipsis: true,
		},
		{
			title: 'schema',
			dataIndex: 'schema',
			width: 120,
			filters: _.uniqBy(data, (item) => (item as any).schema).map((item) => ({
				text: (item as any).schema,
				value: (item as any).schema,
			})),
			onFilter: (value, record) => value === record.schema,
			ellipsis: true,
		},
		{
			title: '字段名称',
			dataIndex: 'columnName',
			width: 140,
			ellipsis: true,
		},
		{
			title: '描述',
			dataIndex: 'columnComment',
			width: 160,
			render: (comment, record) => {
				return (
					<Input
						defaultValue={comment}
						onBlur={(e) => onInputBlur(record.id, e.currentTarget.value)}
						autoComplete="off"
					/>
				);
			},
		},
		{
			title: '字段类型',
			dataIndex: 'columnType',
			width: 120,
			ellipsis: true,
		},
	];
};
