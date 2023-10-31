import { useState } from 'react';

import { IStreamJobProps } from '@/interface';
import TaskManagerList, { ITaskList } from './list';
import TaskManagerLog from './log';

interface IProps {
    data: IStreamJobProps | undefined;
}

export default function TaskManager({ data }: IProps) {
    const [taskDetail, setTaskDetail] = useState<ITaskList | null>(null);

    return (
        <div style={{ height: '100%' }}>
            {taskDetail ? (
                <TaskManagerLog data={data} taskDetail={taskDetail} toTaskDetail={(record) => setTaskDetail(record)} />
            ) : (
                <TaskManagerList onTaskDetail={(record) => setTaskDetail(record)} data={data} />
            )}
        </div>
    );
}
