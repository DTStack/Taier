import * as React from 'react'
import { Table } from 'antd';

class ManageTopCall extends React.Component<any, any> {
    getSource () {
        let data = this.props.data;

        return data || [];
    }
    render () {
        return (

            <Table
                rowKey={(record: any) => {
                    return record.userId
                }}

                style={{ marginTop: '20px', marginBottom: '20px' }}
                className="m-table border-table table-p-l20"
                rowClassName={() => {
                    return 'h-33'
                }}
                pagination={false}
                columns={[{
                    title: '排名',
                    dataIndex: 'rank',
                    key: 'rank',
                    className: 'color-666',
                    render (text: any, record: any, index: any) {
                        return <span className={`rank-number rank-number_${index + 1}`}>{index + 1}</span>
                    }
                }, {
                    title: '用户',
                    dataIndex: 'userName',
                    key: 'userName',
                    className: 'color-666'

                }, {
                    title: '调用次数',
                    dataIndex: 'callNum',
                    key: 'callNum',
                    className: 'color-666'
                }]}

                dataSource={this.getSource()}
                onChange={this.onTableChange}
            />

        )
    }
}

export default ManageTopCall;
