import * as React from 'react'
import { connect } from 'react-redux';
import { Card, Row, Col, Table } from 'antd'
import { apiMarketActions } from '../../../actions/apiMarket';
import TopCard from './topCard'

const mapStateToProps = (state: any) => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = (dispatch: any) => ({
    getApiDetail(apiId: any) {
        dispatch(
            apiMarketActions.getApiDetail({
                apiId: apiId
            })
        )
    },
    getApiExtInfo(apiId: any) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class APIDetail extends React.Component<any, any> {
    state: any = {
        apiId: ''
    }
    componentDidMount () {
        const apiId = this.props.router.params && this.props.router.params.api;
        if (apiId) {
            this.setState({
                apiId: apiId
            }, () => {
                this.props.getApiDetail(apiId);
                this.props.getApiExtInfo(apiId);
            })
        }
    }

    getRequestDataSource () {
        return this.getValue('reqParam') || [];
    }
    getRequestColumns () {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName'
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType'
        }, {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render(text: any) {
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

    getResponseDataSource () {
        return this.getValue('respParam') || [];
    }

    getResponseColumns () {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName'
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType'
        }, {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render(text: any) {
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
    getValue(key: any) {
        const api = this.props.apiMarket && this.props.apiMarket.api && this.props.apiMarket.api[this.state.apiId];
        if (api) {
            return api[key]
        } else {
            return null;
        }
    }
    render () {
        return (
            <div>
                <TopCard {...this.state} {...this.props} ></TopCard>
                <Card className="box-1" noHovering>
                    <div>
                        <h1 className="title-border-l-blue">基本信息</h1>
                        <div style={{ marginTop: 15 }}>
                            <p data-title="支持格式：" className="pseudo-title p-line">{this.getValue('supportType')}</p>
                            <p data-title="请求协议：" className="pseudo-title p-line">{this.getValue('reqProtocol')}</p>
                            <p data-title="请求方式：" className="pseudo-title p-line">{this.getValue('reqMethod')}</p>
                        </div>
                    </div>
                    <Row gutter={30} style={{ marginTop: 20 }}>
                        <Col span={12}>
                            <section>
                                <h1 className="title-border-l-blue">请求参数</h1>
                                <Table
                                    rowKey="paramName"
                                    style={{ marginTop: 15 }}
                                    className="m-table border-table"
                                    pagination={false}
                                    dataSource={this.getRequestDataSource()}
                                    columns={this.getRequestColumns()} />
                            </section>
                        </Col>
                        <Col span={12}>
                            <section>
                                <h1 className="title-border-l-blue">返回参数</h1>
                                <Table
                                    rowKey="paramName"
                                    style={{ marginTop: 15 }}
                                    className="m-table border-table"
                                    pagination={false}
                                    dataSource={this.getResponseDataSource()}
                                    columns={this.getResponseColumns()} />
                            </section>
                        </Col>
                    </Row>
                    <div style={{ marginTop: 20 }}>
                        <h1 className="title-border-l-blue">JSON样例</h1>
                        <div style={{ marginTop: 15 }}>
                            <pre>
                                {JSON.stringify({
                                    'inFields': {
                                        id: 175,
                                        date: '2017-09-08 12:33:23',
                                        in: '12,34,45'
                                    }
                                }, null, '    \r')}
                            </pre>
                        </div>
                    </div>
                </Card>
            </div>
        )
    }
}

export default APIDetail;
