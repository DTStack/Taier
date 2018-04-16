import React, { Component } from 'react'
import {  Card, Col, Row, Table } from 'antd';
import {Link} from "react-router"
class TopFail extends Component {
    getDataSource(){
       return this.props.data||[];
    }
    openNewDetail(text){
        this.props.router.push({
            pathname:'/api/manage/detail/'+text
        })
    }

    render() {
        return (
            <Card
                noHovering
                title={this.props.noTitle?'':"失败率TOP10"}
                style={{ height: this.props.cardHeight||403 }}
            >
                <Table
                    rowKey={(record)=>{
                        return record.id
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
                            return <span className={`rank-number rank-number-fail_${index+1}`}>{index+1}</span>
                        }
                    }, {
                        title: '接口',
                        dataIndex: 'apiName',
                        key: 'apiName',
                        className: "color-666",
                        render:(text,record)=> {
                            return <a onClick={this.openNewDetail.bind(this,record.id)}>{text}</a>
                        }
                    }, {
                        title: '调用次数',
                        dataIndex: 'callNum',
                        key: 'callNum',
                        className: "color-666"
                    },
                    {
                        title: '失败率',
                        dataIndex: 'failRate',
                        key: 'failRate',
                        className: "color-666",
                        render(text){
                            return text+"%"
                        }
                    }]}


                    dataSource={this.getDataSource()}
                    onChange={this.onTableChange}
                />


            </Card>
        )
    }
}

export default TopFail;