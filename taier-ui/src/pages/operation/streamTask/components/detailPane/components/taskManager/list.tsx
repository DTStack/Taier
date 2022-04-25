import * as React from 'react';
import { Table, Breadcrumb } from 'antd';
import { IStreamTaskProps } from '@/interface';
import type { ColumnsType } from 'antd/lib/table';

const Api = {} as any

interface IState {
    taskList: ITaskList[];
}

interface IProps {
    data: IStreamTaskProps | undefined;
    toTaskDetail: (record: ITaskList) => void;
}

export interface ITaskList {
    id: number;
    dataPort: string;
    freeSlots: number;
    slotsNumber: number;
}

class TaskManagerList extends React.Component<IProps, IState> {
    state: IState = {
        taskList: []
    }

    componentDidMount () {
        this.getTaskManageList();
    }

    getTaskManageList = async () => {
        const { data } = this.props;
        const res = await Api.getTaskManageList({ taskId: data?.id });
        if (res.code == 1) {
            this.setState({
                taskList: res.data
            })
        }
    }

    toTaskDetail = (record: ITaskList) => {
        this.props.toTaskDetail(record);
    }

    columns: ColumnsType<ITaskList> = [
        {
            title: 'Task ID',
            dataIndex: 'id',
            render: (id, record) => {
                return <a onClick={this.toTaskDetail.bind(this, record)}>{id}</a>
            }
        },
        {
            title: 'Data Port',
            dataIndex: 'dataPort',
            render: (dataPort, record) => {
                return <a onClick={this.toTaskDetail.bind(this, record)}>{dataPort || '-'}</a>
            }
        },
        {
            title: 'Free /All Slots',
            dataIndex: 'freeSlots',
            render: (freeSlots, record) => {
                const freeSlot = freeSlots || freeSlots == 0 ? freeSlots : '-'
                const slotsNumber = record.slotsNumber || record.slotsNumber == 0 ? record.slotsNumber : '-'
                return `${freeSlot} / ${slotsNumber}`
            }
        },
        {
            title: '操作',
            dataIndex: 'deal',
            width: 100,
            render: (_, record) => {
                return <a onClick={this.toTaskDetail.bind(this, record)}>查看详情</a>
            }
        }
    ]

    render () {
        const { taskList } = this.state

        return (
            <div style={{ padding: '0 20px 25px' }}>
                <Breadcrumb>
                    <Breadcrumb.Item>Task List</Breadcrumb.Item>
                </Breadcrumb>
                <Table
                    columns={this.columns}
                    style={{ marginTop: 16, boxShadow: 'unset' }}
                    dataSource={taskList}
                    className="dt-table-border"
                    pagination={false}
                />
            </div>
        )
    }
}

export default TaskManagerList;
