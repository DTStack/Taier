import React, { Component } from 'react'
import { Row, Col, Table } from 'antd'
import moment from 'moment';

import SecurityDetailModal from '../../components/securityDetailModal';

import { API_METHOD, API_METHOD_KEY } from '../../consts';
import { getApiMarketValue } from '../../utils';

class Content extends Component {
    state = {
        securityModalVisible: false,
        securityData: {}
    }

    getRequestDataSource () {
        return this.getValue('reqParam') || [];
    }
    getRequestColumns () {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName',
            width: 120
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType',
            width: 100
        }, {
            title: '必填',
            dataIndex: 'required',
            key: 'required',
            width: 60,
            render (text) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',
            render (text) {
                if (text && text.length > 10) {
                    return <span title={text}>text</span>
                } else {
                    return text
                }
            }
        }];
    }
    getResponseDataSource () {
        return this.getValue('respParam') || [];
    }
    getResponseColumns () {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName',
            width: 120
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType',
            width: 100
        }, {
            title: '必填',
            dataIndex: 'required',
            key: 'required',
            width: 60,
            render (text) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',
            render (text) {
                if (text && text.length > 10) {
                    return <span title={text}>text</span>
                } else {
                    return text
                }
            }

        }];
    }
    getValue (key) {
        const { apiMarket, apiId } = this.props;
        return getApiMarketValue(key, apiMarket, apiId)
    }
    getValueCallInfo (key) {
        const { apiMarket, apiId } = this.props;
        const api = apiMarket && apiMarket.api && apiMarket.apiCallInfo[apiId];

        if (api) {
            return api[key]
        } else {
            return null;
        }
    }
    showSecurityDetail (security) {
        this.setState({
            securityModalVisible: true,
            securityData: security
        })
    }
    renderSecurityListView (securityList) {
        return securityList.map((security) => {
            return <a onClick={this.showSecurityDetail.bind(this, security)} key={security.id}>{security.name}</a>
        }).reduce((arrs, currentArr) => {
            if (arrs.length) {
                return arrs.concat(['，', currentArr])
            } else {
                return arrs.concat(currentArr)
            }
        }, [])
    }
    render () {
        const { callUrl, callLimit,
            beginTime, endTime,
            mode, showRecord,
            showMarketInfo, showUserInfo, showSecurity,
            securityList
        } = this.props;
        const { securityData, securityModalVisible } = this.state;
        const showExt = mode == 'manage';
        const isGET = this.getValue('reqMethod') == API_METHOD.GET
        let reqJson = this.getValue('reqJson');

        if (isGET) {
            reqJson = Object.entries(reqJson).map(
                ([key, value]) => {
                    return `${key}=${value}`
                }
            ).join('&')
            reqJson = reqJson ? ('?' + reqJson) : '无'
        } else {
            reqJson = JSON.stringify(reqJson, null, '    \r')
        }
        return (
            <div>
                <section>
                    <h1 className="title-border-l-blue">基本信息</h1>
                    <div style={{ marginTop: 10 }}>
                        <span data-title="支持格式：" className="pseudo-title p-line api_item-margin">{this.getValue('supportType')}</span>
                        <span data-title="请求协议：" className="pseudo-title p-line api_item-margin">{this.getValue('reqProtocol')}</span>
                        <span data-title="请求方式：" className="pseudo-title p-line api_item-margin">{API_METHOD_KEY[this.getValue('reqMethod')]}</span>
                        <p data-title="API path：" className="pseudo-title p-line">{this.getValue('apiPath')}</p>
                        {!showExt && (
                            <p data-title="调用次数限制：" className="pseudo-title p-line">{this.getValue('reqLimit')} 次/秒</p>
                        )}
                        {showSecurity && (
                            <p data-title="安全组：" className="pseudo-title p-line">{this.renderSecurityListView(securityList)}</p>
                        )}
                        {showUserInfo && <div>
                            <p data-title="调用URL：" className="pseudo-title p-line">{callUrl}</p>
                            <p data-title="申请调用次数：" className="pseudo-title p-line">{callLimit == -1 ? '无限制' : callLimit}</p>
                            <p data-title="申请调用周期：" className="pseudo-title p-line">{beginTime ? `${moment(beginTime).format('YYYY-MM-DD')} ~ ${moment(endTime).format('YYYY-MM-DD')}` : '无限制'}</p>
                        </div>
                        }

                        <p data-title="API描述：" className="pseudo-title p-line">{this.getValue('desc')}</p>
                        <p data-title="创建人：" className="pseudo-title p-line">{this.getValue('createUser')}</p>
                        {showExt && (
                            <div>
                                <p data-title="最近修改人：" className="pseudo-title p-line">{showRecord.modifyUser}</p>
                                <p data-title="最近修改时间：" className="pseudo-title p-line">{moment(showRecord.gmtModified).format('YYYY-MM-DD HH:mm:ss')}</p>
                            </div>
                        )}
                    </div>
                </section>
                {showMarketInfo && <section style={{ marginTop: 19.3 }}>
                    <h1 className="title-border-l-blue">调用订购情况</h1>
                    <div style={{ marginTop: 10 }}>
                        <span data-title="累计调用次数：" className="pseudo-title p-line api_item-margin">{this.getValueCallInfo('apiCallNum')}</span>
                        <span data-title="订购人数：" className="pseudo-title p-line api_item-margin">{this.getValueCallInfo('applyNum')}</span>
                    </div>
                </section>}
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
                                scroll={{ y: 160 }}
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
                                scroll={{ y: 160 }}
                                columns={this.getResponseColumns()} />
                        </section>
                    </Col>
                </Row>
                <Row gutter={30} style={{ marginTop: 19.3 }}>
                    <Col span={11}>
                        <section>
                            <h1 className="title-border-l-blue">请求{isGET ? 'URL' : 'JSON'}样例</h1>
                            <div style={{ marginTop: 18 }}>
                                <pre style={{ maxHeight: '150px', overflow: 'auto' }}>
                                    {reqJson}
                                </pre>
                            </div>
                        </section>
                    </Col>
                    <Col span={11}>
                        <section>
                            <h1 className="title-border-l-blue">返回JSON样例</h1>
                            <div style={{ marginTop: 18 }}>
                                <pre style={{ maxHeight: '150px', overflow: 'auto' }}>
                                    {this.getValue('respJson') ? JSON.stringify(this.getValue('respJson'), null, '    \r') : '暂无返回样例'}
                                </pre>
                            </div>
                        </section>
                    </Col>
                </Row>
                <SecurityDetailModal
                    data={securityData}
                    visible={securityModalVisible}
                    closeModal={() => {
                        this.setState({
                            securityModalVisible: false
                        })
                    }}
                />
            </div>
        )
    }
}

export default Content;
