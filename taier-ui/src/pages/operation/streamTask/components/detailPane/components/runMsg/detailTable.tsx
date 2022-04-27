import { Table } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import type { IDataSource } from './index';

interface IProps {
	loading: boolean;
	tableData: IDataSource[];
}

export default function DetailTable({ loading, tableData }: IProps) {
	const columns: ColumnsType<IDataSource> = [
		{
			title: 'Name',
			dataIndex: 'jobVertexName',
			width: '250px',
		},
		{
			title: 'Parallelism',
			dataIndex: 'parallelism',
			sorter: (a, b) => a.parallelism - b.parallelism,
		},
		{
			title: 'Bytes received',
			dataIndex: 'bytesReceived',
			sorter: (a, b) => a.bytesReceived - b.bytesReceived,
		},
		{
			title: 'Bytes sent',
			dataIndex: 'bytesSent',
			sorter: (a, b) => a.bytesSent - b.bytesSent,
		},
		{
			title: 'Record Received',
			dataIndex: 'recordsReceived',
			sorter: (a, b) => a.recordsReceived - b.recordsReceived,
		},
		{
			title: 'Record Sent',
			dataIndex: 'recordsSent',
			sorter: (a, b) => a.recordsSent - b.recordsSent,
		},
	];

	return (
		<Table<IDataSource>
			rowKey="jobVertexId"
			style={{ margin: '0 20px' }}
			loading={loading}
			dataSource={tableData}
			columns={columns}
			pagination={false}
		/>
	);
}
