import { useEffect, useState } from 'react';
import { Table } from 'antd';
import stream from '@/api';

interface IProps {
	taskId?: number;
}

interface IDataSource {
	tableName: string;
}

export default function ResultTable({ taskId }: IProps) {
	const [loading, setLoading] = useState(false);
	const [data, setData] = useState<IDataSource[]>([]);

	useEffect(() => {
		if (taskId !== undefined) {
			setLoading(true);
			stream
				.getResultTable({ taskId })
				.then((res) => {
					if (res.code === 1) {
						setData(res.data || []);
					}
				})
				.finally(() => {
					setLoading(false);
				});
		}
	}, []);

	return (
		<Table
			loading={loading}
			dataSource={data}
			pagination={false}
			columns={[
				{
					title: '表名',
					key: 'tableName',
				},
			]}
		/>
	);
}
