import { useEffect, useState } from 'react';
import { Table } from 'antd';
import stream from '@/api';

interface IProps {
	taskId?: number;
}

interface IDataSource {
	host: string;
	ip: string;
	port: string;
}

export default function RunCodeAddess({ taskId }: IProps) {
	const [dataSource, setDataSource] = useState<IDataSource[]>([]);

	useEffect(() => {
		if (taskId !== undefined) {
			stream
				.getContainerInfos({
					taskId,
				})
				.then((res) => {
					if (res.code == 1) {
						setDataSource(res?.data || []);
					}
				});
		}
	}, []);

	return (
		<Table<IDataSource>
			rowKey={(record) => `${record.host}:${record.ip}:${record.port}`}
			columns={[
				{
					dataIndex: 'host',
					title: '主机名',
					width: 200,
				},
				{
					dataIndex: 'ip',
					title: 'IP',
					width: 200,
				},
				{
					dataIndex: 'port',
					title: '端口',
					width: 200,
				},
			]}
			dataSource={dataSource}
			pagination={false}
			scroll={{ y: 500 }}
		/>
	);
}
