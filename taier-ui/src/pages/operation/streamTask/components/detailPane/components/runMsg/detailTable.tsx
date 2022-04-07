import * as React from 'react';
import { Table, Tag } from 'antd';
import type { ColumnsType } from 'antd/lib/table';
import type { IDataSource } from './index';

interface Props {
    loading: boolean;
    tableData: IDataSource[];
}

class DetailTable extends React.Component<Props, any> {
    renderStatus = (status: string) => {
        switch (status) {
            case 'created':
            case 'scheduled':
            case 'running':
            case 'finished': {
                return <Tag className="c-table_tag" color="#16DE9A">{status}</Tag>
            }
            case 'deploying':
            case 'canneling':
            case 'reconciling': {
                return <Tag className="c-table_tag" color="#FFB310">{status}</Tag>
            }
            case 'canceled':
            case 'failed': {
                return <Tag className="c-table_tag" color="#FF5F5C">{status}</Tag>
            }
            default: {
                return '状态错误'
            }
        }
    }

    columns: ColumnsType<IDataSource> = [
        {
            title: 'Name',
            dataIndex: 'jobVertexName',
            width: '250px'
        }, {
            title: 'Parallelism',
            dataIndex: 'parallelism',
            sorter: (a, b) => a.parallelism - b.parallelism
        }, {
            title: 'Bytes received',
            dataIndex: 'bytesReceived',
            sorter: (a, b) => a.bytesReceived - b.bytesReceived
        }, {
            title: 'Bytes sent',
            dataIndex: 'bytesSent',
            sorter: (a, b) => a.bytesSent - b.bytesSent
        }, {
            title: 'Record Received',
            dataIndex: 'recordsReceived',
            sorter: (a, b) => a.recordsReceived - b.recordsReceived
        }, {
            title: 'Record Sent',
            dataIndex: 'recordsSent',
            sorter: (a, b) => a.recordsSent - b.recordsSent
        }
    ]

    render () {
        const { loading, tableData } = this.props

        return (
            <Table
                rowKey="jobVertexId"
                className="dt-table-border"
                style={{ margin: '0 20px' }}
                loading={loading}
                dataSource={tableData}
                columns={this.columns}
                pagination={false}
            />
        )
    }
}

export default DetailTable;
