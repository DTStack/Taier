import React, { Component } from 'react'
import {  Card, Col, Row, Table } from 'antd';

class TopCall extends Component {

    render() {
        return (
            <Card
                noHovering
                title="调用用户TOP10"
                style={{ height: 403 }}
            >
                <Table
                    rowKey={(record)=>{
                        return record.userId
                    }}
                    className="m-table"
                    rowClassName={() => {
                        return "h-33"
                    }}
                    pagination={false}
                    columns={[{
                        title: '排名',
                        dataIndex: 'rank',
                        key: 'rank',
                        className: "color-666",
                        render(text, record,index) {
                            return <span className={`rank-number rank-number_${index+1}`}>{index+1}</span>
                        }
                    }, {
                        title: '用户',
                        dataIndex: 'userName',
                        key: 'userName',
                        className: "color-666"
                    }, {
                        title: '调用次数',
                        dataIndex: 'callNum',
                        key: 'callNum',
                        className: "color-666"
                    }]}


                    dataSource={this.props.data||[]}
                    onChange={this.onTableChange}
                />


            </Card>
        )
    }
}

export default TopCall;