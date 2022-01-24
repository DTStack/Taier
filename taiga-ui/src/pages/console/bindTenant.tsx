import { useMemo } from 'react';
import Sketch from '@/components/sketch';
import type { ColumnsType } from 'antd/lib/table';
import Api from '../../api/console';
import { isSparkEngine } from '@/utils';

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

export default ({ clusterId, engineType, clusterName, onClick }: IBindTenantProps) => {
	const requestTenantResource = (values: IFormFieldProps, { current }: { current: number }) => {
		return Api.searchTenant({
			tenantName: values.name,
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
				pagination: {
					showSizeChanger: false,
					showQuickJumper: false,
				},
			}}
		/>
	);
};
