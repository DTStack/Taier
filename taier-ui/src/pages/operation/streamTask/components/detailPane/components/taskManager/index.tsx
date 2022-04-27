import { useState } from 'react';
import { IStreamTaskProps } from '@/interface';
import TaskManagerList, { ITaskList } from './list';
import TaskManagerLog from './log';

interface IProps {
	data: IStreamTaskProps | undefined;
}

export default function TaskManager({ data }: IProps) {
	const [taskDetail, setTaskDetail] = useState<ITaskList | null>(null);

	return (
		<div style={{ height: '100%' }}>
			{taskDetail ? (
				<TaskManagerLog
					data={data}
					taskDetail={taskDetail}
					toTaskDetail={(record) => setTaskDetail(record)}
				/>
			) : (
				<TaskManagerList onTaskDetail={(record) => setTaskDetail(record)} data={data} />
			)}
		</div>
	);
}
