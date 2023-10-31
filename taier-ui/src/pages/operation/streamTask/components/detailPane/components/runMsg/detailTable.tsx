import { Table } from 'antd';
import type { ColumnsType } from 'antd/lib/table';

import type { IFlinkJsonProps } from './index';

interface IProps {
    loading: boolean;
    tableData: IFlinkJsonProps[];
}

export default function DetailTable({ loading, tableData }: IProps) {
    const columns: ColumnsType<IFlinkJsonProps> = [
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
            sorter: (a, b) => Number(a.bytesReceived) - Number(b.bytesReceived),
        },
        {
            title: 'Bytes sent',
            dataIndex: 'bytesSent',
            sorter: (a, b) => Number(a.bytesSent) - Number(b.bytesSent),
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
        <Table<IFlinkJsonProps>
            rowKey="jobVertexId"
            style={{ margin: '0 20px' }}
            loading={loading}
            dataSource={tableData}
            columns={columns}
            pagination={false}
        />
    );
}
