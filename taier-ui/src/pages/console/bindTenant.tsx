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

import { forwardRef, useImperativeHandle, useMemo } from 'react';
import Sketch, { useSketchRef } from '@/components/sketch';
import type { ColumnsType } from 'antd/lib/table';
import Api from '../../api';
import { isSparkEngine } from '@/utils/is';

interface IFormFieldProps {
	name: string;
}

export interface ITableProps {
	maxCapacity: string;
	minCapacity: string;
	queue: string;
	queueId: number;
	tenantId: number;
	tenantName: string;
}

interface IBindTenantProps {
	clusterId: number;
	engineType: number;
	clusterName?: string;
	onClick?: (record: ITableProps) => void;
}

export default forwardRef(
	({ clusterId, engineType, clusterName, onClick }: IBindTenantProps, ref) => {
		const actionRef = useSketchRef();
		const requestTenantResource = (
			values: IFormFieldProps,
			{ current }: { current: number },
		) => {
			return Api.searchTenant({
				name: values.name,
				currentPage: current,
				clusterId,
				engineType,
				clusterName,
				pageSize: 20,
			}).then((res) => {
				if (res.code === 1) {
					return {
						total: res.data.totalCount,
						data: res.data.data,
					};
				}
			});
		};

		useImperativeHandle(ref, () => ({
			getTenant: actionRef.current?.submit(),
		}));

		const isHadoop = isSparkEngine(engineType);

		const columns = useMemo(() => {
			if (!isHadoop) {
				return [
					{
						title: '租户',
						dataIndex: 'tenantName',
					},
				] as ColumnsType<ITableProps>;
			}
			return [
				{
					title: '租户',
					dataIndex: 'tenantName',
				},
				{
					title: '资源队列',
					dataIndex: 'queue',
				},
				{
					title: '最小容量（%）',
					dataIndex: 'minCapacity',
				},
				{
					title: '最大容量（%）',
					dataIndex: 'maxCapacity',
				},
				{
					title: '操作',
					dataIndex: 'deal',
					render: (_, record) => {
						return <a onClick={() => onClick?.(record)}>资源管理</a>;
					},
				},
			] as ColumnsType<ITableProps>;
		}, [isHadoop]);

		return (
			<Sketch<ITableProps, IFormFieldProps>
				actionRef={actionRef}
				header={[
					{
						name: 'input',
						props: {
							slotProps: {
								placeholder: '按租户名称搜索',
							},
						},
					},
				]}
				request={requestTenantResource}
				columns={columns}
				tableProps={{
					rowSelection: undefined,
					rowKey: (record) => `${record.tenantId}-${record.queueId}`,
				}}
			/>
		);
	},
);
