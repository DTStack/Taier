import * as React from 'react'
import { Card, Table } from 'antd';

class TopCall extends React.Component<any, any> {
    render () {
        return (
            <Card
                noHovering
                style={{ height: 363 }}
            >
                <Table
                    rowKey={(record: any) => {
                        return record.userId
                    }}
                    className="m-table"
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

                    dataSource={this.props.data || []}
                    onChange={this.onTableChange}
                />

            </Card>
        )
    }
}

export default TopCall;
