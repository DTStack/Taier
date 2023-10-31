import { useEffect, useMemo,useState } from 'react';
import { Breadcrumb,Table } from 'antd';
import type { ColumnsType } from 'antd/lib/table';

import stream from '@/api';
import { IStreamJobProps } from '@/interface';

interface IProps {
    data: IStreamJobProps | undefined;
    onTaskDetail: (record: ITaskList) => void;
}

export interface ITaskList {
    id: number;
    dataPort: string;
    freeSlots: number;
    slotsNumber: number;
}

export default function TaskManagerList({ data, onTaskDetail }: IProps) {
    const [taskList, setTaskList] = useState<ITaskList[]>([]);
    const [loading, setLoading] = useState(false);

    const getTaskManageList = async () => {
        setLoading(true);
        const res = await stream.listTaskManager({ taskId: data?.id });
        if (res.code == 1) {
            setTaskList(res.data || []);
        }
        setLoading(false);
    };

    useEffect(() => {
        getTaskManageList();
    }, []);

    const columns = useMemo<ColumnsType<ITaskList>>(() => {
        return [
            {
                title: 'Task ID',
                dataIndex: 'id',
                render: (id, record) => {
                    return <a onClick={() => onTaskDetail(record)}>{id}</a>;
                },
            },
            {
                title: 'Data Port',
                dataIndex: 'dataPort',
                render: (dataPort, record) => {
                    return <a onClick={() => onTaskDetail(record)}>{dataPort || '-'}</a>;
                },
            },
            {
                title: 'Free /All Slots',
                dataIndex: 'freeSlots',
                render: (freeSlots, record) => {
                    const freeSlot = freeSlots || freeSlots == 0 ? freeSlots : '-';
                    const slotsNumber = record.slotsNumber || record.slotsNumber == 0 ? record.slotsNumber : '-';
                    return `${freeSlot} / ${slotsNumber}`;
                },
            },
            {
                title: '操作',
                dataIndex: 'deal',
                width: 100,
                render: (_, record) => {
                    return <a onClick={() => onTaskDetail(record)}>查看详情</a>;
                },
            },
        ];
    }, []);
    return (
        <div style={{ padding: '0 20px 25px' }}>
            <Breadcrumb>
                <Breadcrumb.Item>Task List</Breadcrumb.Item>
            </Breadcrumb>
            <Table<ITaskList>
                loading={loading}
                columns={columns}
                style={{ marginTop: 16 }}
                dataSource={taskList}
                className="dt-table-border"
                pagination={false}
            />
        </div>
    );
}
