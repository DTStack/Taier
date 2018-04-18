import React, { Component } from 'react'
import { connect } from "react-redux";
import { Card, Icon, Row, Col, Button, Table } from "antd"
import { Link } from "react-router";
import { apiMarketActions } from '../../../actions/apiMarket';
import TopCard from "./topCard"

const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getApiDetail(apiId) {
        dispatch(
            apiMarketActions.getApiDetail({
                apiId: apiId
            })
        )
    },
    getApiExtInfo(apiId) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIDetail extends Component {
    state = {
        apiId: ''
    }
    componentDidMount() {
        const apiId = this.props.router.params && this.props.router.params.api;
        if (apiId) {
            this.setState({
                apiId: apiId
            },()=>{
                this.props.getApiDetail(apiId);
                this.props.getApiExtInfo(apiId);
            })
        }

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
        const api=this.props.apiMarket&&this.props.apiMarket.api&&this.props.apiMarket.api[this.state.apiId];
        if(api){
            return api[key]
        }else{
            return null;
        }

    }
    render() {
        return (
            <div>
                <TopCard {...this.state} {...this.props} ></TopCard>
                <Card className="box-1" noHovering>
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
                                    columns={this.getResponseColumns()} />
                            </section>
                        </Col>
                    </Row>


                </Card>
            </div>
        )
    }
}

export default APIDetail;