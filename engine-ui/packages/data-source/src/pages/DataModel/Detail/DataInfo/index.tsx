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
import { Table } from 'antd';
import './style';
import { relationListColumns, columns } from './constants';
import { EnumSize } from '../types';

interface ITableItem {
	title: string;
	columns: any[];
	dataSource: any[];
}

const tableListGenerator = (size: EnumSize, dsList: any[][]): ITableItem[] => {
	const list = [
		{
			title: '关联表',
			columns: relationListColumns(size),
			dataSource: [],
		},
		{
			title: '维度',
			columns: columns(size),
			dataSource: [],
		},
		{
			title: '度量',
			columns: columns(size),
			dataSource: [],
		},
	];
	// 若没有度量列，则不展示度量列
	if (dsList[2].length === 0) delete list[2];
	list.forEach((item, index) => {
		item.dataSource = dsList[index];
	});
	return list;
};

interface IPropsDataInfo {
	relationTableList: any[];
	dimensionList: any[];
	metricList: any[];
	size?: EnumSize;
}

const DataInfo = (props: IPropsDataInfo) => {
	const { relationTableList, dimensionList, metricList, size = EnumSize.LARGE } = props;
	const list = [relationTableList, dimensionList, metricList];
	// 构造tableList参数
	const tableList = tableListGenerator(size, list);
	const total = (index: number, count: number) => {
		switch (index) {
			case 0:
				return `共${count}张表`;
			default:
				return `共${count}个字段`;
		}
	};

	return (
		<div className="data-info">
			{tableList.map((item, index) => (
				<div key={index} data-testid={`di-table-${index}`}>
					<div className="title-wrappe">
						<div className="title float-left">{item.title}</div>
						<div className="float-right sumary">
							{total(index, item.dataSource?.length)}
						</div>
					</div>
					<Table
						className="dt-table-border dt-table-last-row-noborder"
						rowKey={(record, index) => index.toString()}
						columns={item.columns}
						dataSource={item.dataSource}
						pagination={false}
					/>
				</div>
			))}
		</div>
	);
};

export default DataInfo;
