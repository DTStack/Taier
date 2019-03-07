import React, { Component } from 'react'
import { Card, Table } from 'antd';

class TopCall extends Component {
    getDataSource () {
        return this.props.data || [];
    }
    openNewDetail (text, apiName) {
        if (this.props.idAdmin) {
            this.props.router.push({
                pathname: '/api/manage',
                state: {
                    apiName: apiName,
                    apiId: text
                }
            })
            return;
        }
        this.props.router.push({
            pathname: '/api/mine/myApi/approved',
            query: {
                apiName: apiName,
                apiId: text
            }
        })
    }

    render () {
        return (
            <Card
                noHovering

                style={{ height: 363 }}
            >
                <Table
                    rowKey={(record) => {
                        return record.id
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
                        render (text, record, index) {
                            return <span className={`rank-number rank-number_${index + 1}`}>{index + 1}</span>
                        }
                    }, {
                        title: '接口',
                        dataIndex: 'apiName',
                        key: 'apiName',
                        className: 'color-666',
                        render: (text, record) => {
                            return <a onClick={this.openNewDetail.bind(this, record.id, record.apiName)}>{text}</a>
                        }
                    }, {
                        title: '调用次数',
                        dataIndex: 'callNum',
                        key: 'callNum',
                        className: 'color-666'
                    },
                    {
                        title: '失败率',
                        dataIndex: 'failRate',
                        key: 'failRate',
                        className: 'color-666',
                        render (text) {
                            return text + '%'
                        }
                    }]}

                    dataSource={this.getDataSource()}
                    onChange={this.onTableChange}
                />

            </Card>
        )
    }
}

export default TopCall;
