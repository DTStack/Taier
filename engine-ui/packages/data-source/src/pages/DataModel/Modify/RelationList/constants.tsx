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
import { Divider } from 'antd';
import { JoinType } from 'pages/DataModel/types';

export const columnsGenerator = ({ onDelete, onEdit }) => {
	return [
		{
			title: '序号',
			key: 'index',
			width: 80,
			ellipsis: true,
			render: (value, record, index) => index + 1,
		},
		{
			title: '表别名',
			dataIndex: 'tableAlias',
			key: 'tableAlias',
			width: 100,
			ellipsis: true,
		},
		{
			title: 'schema',
			dataIndex: 'schema',
			key: 'schema',
			width: 120,
			ellipsis: true,
		},
		{
			title: '表名',
			dataIndex: 'table',
			key: 'table',
			width: 100,
			ellipsis: true,
		},
		{
			title: '关联类型',
			dataIndex: 'joinType',
			key: 'jionType',
			width: 120,
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
			width: 160,
			ellipsis: true,
			render: (value, record) => {
				const { leftTableAlias, tableAlias } = record;
				return (
					value &&
					value
						.reduce((temp, cur) => {
							const ltTable = leftTableAlias;
							const ltCol = cur.leftValue?.columnName;
							const rtTable = tableAlias;
							const rtCol = cur.rightValue?.columnName;
							return `${temp}${ltTable}.${ltCol} = ${rtTable}.${rtCol} and `;
						}, '')
						.replace(/ and $/, '')
				);
			},
		},
		{
			title: '操作',
			key: 'action',
			width: 120,
			// type hack
			fixed: 'right' as 'right',
			render: (text, record) => (
				<span>
					<a className="btn-link" onClick={() => onEdit(record.id)}>
						编辑
					</a>
					<Divider type="vertical" />
					<a className="btn-link" onClick={() => onDelete(record.id)}>
						删除
					</a>
				</span>
			),
		},
	];
};
