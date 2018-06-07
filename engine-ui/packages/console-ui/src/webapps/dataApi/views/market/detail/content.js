import React, { Component } from 'react'
import { Card, Icon, Row, Col, Button, Table } from "antd"
import { Link } from "react-router";
class Content extends Component {

    componentDidMount() {

    }

    getRequestDataSource() {
        return this.getValue('reqParam')||[];
    }
    getRequestColumns() {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName',
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType',
        }, {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render(text) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc'
        }];
    }
    getResponseDataSource() {
        return this.getValue('respParam')||[];
    }
    getResponseColumns() {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName',
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType',
        }, {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render(text) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',

        }];
    }
    getValue(key){
        const api=this.props.apiMarket&&this.props.apiMarket.api&&this.props.apiMarket.api[this.props.apiId];
        if(api){
            return api[key]
        }else{
            return null;
        }

    }
    render() {
        
        return (
            <div>
                <section>
                    <h1 className="title-border-l-blue">基本信息</h1>
                    <div style={{ marginTop: 10 }}>
                        <p data-title="支持格式：" className="pseudo-title p-line">{this.getValue('supportType')}</p>
                        <p data-title="请求协议：" className="pseudo-title p-line">{this.getValue('reqProtocol')}</p>
                        <p data-title="请求方式：" className="pseudo-title p-line">{this.getValue('reqMethod')}</p>
                        
                    </div>
                </section>
                <Row gutter={30} style={{ marginTop: 19.3 }}>
                    <Col span={11}>
                        <section>
                            <h1 className="title-border-l-blue">请求参数</h1>
                            <Table
                                rowKey="paramName"
                                style={{ marginTop: 18 }}
                                className="m-table border-table"
                                pagination={false}
                                dataSource={this.getRequestDataSource()}
                                scroll={{ y: 245 }}
                                columns={this.getRequestColumns()} />
                        </section>
                    </Col>
                    <Col span={11}>
                        <section>
                            <h1 className="title-border-l-blue">返回参数</h1>
                            <Table
                                rowKey="paramName"
                                style={{ marginTop: 18 }}
                                className="m-table border-table"
                                pagination={false}
                                dataSource={this.getResponseDataSource()}
                                scroll={{ y: 245 }}
                                columns={this.getResponseColumns()} />
                        </section>
                    </Col>
                </Row>
                <section style={{ marginTop: 18 }}>
                    <h1 className="title-border-l-blue">JSON样例</h1>
                    <div style={{ marginTop: 18 }}>
                    <pre>
                    {JSON.stringify({
                            "inFields":{
                                id:175,
                                date:"2017-09-08 12:33:23",
                                in:"12,34,45"
                            }
                        },null,"    \r")}
                    </pre>
                    </div>
                </section>
            </div>
        )
    }
}

export default Content;