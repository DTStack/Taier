import React from 'react';

import { Row, Col, Table } from 'antd';
import JsonContent from './jsonContent';

import { API_MODE, dataSourceTypes } from '../../consts';

class CreateContentSection extends React.Component {
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
    render () {
        const {
            showApiConfig,
            showMarketInfo,
            apiConfig,
            reqJson,
            isGET,
            getValue,
            getValueCallInfo,
            getRequestDataSource,
            getResponseDataSource
        } = this.props;
        return (
            <div>
                {showApiConfig && (
                    <section style={{ marginTop: 19.3 }}>
                        <h1 className="title-border-l-blue">配置信息</h1>
                        <div style={{ marginTop: 10 }}>
                            <p data-title="数据源类型：" className="pseudo-title p-line">{dataSourceTypes[apiConfig.dataSrcType]}</p>
                            <p data-title="数据源名称：" className="pseudo-title p-line">{apiConfig.dataSrcName}</p>
                            {API_MODE.GUIDE == apiConfig.paramCfgType ? (
                                <p data-title="数据表名称：" className="pseudo-title p-line">{apiConfig.dataSrcTable}</p>
                            ) : null}
                            {API_MODE.SQL == apiConfig.paramCfgType ? (
                                <p data-title="SQL配置信息：" className="pseudo-title p-line">
                                    <JsonContent style={{ width: '450px', verticalAlign: 'text-top', display: 'inline-block' }} json={apiConfig.dataSrcSQL} />
                                </p>
                            ) : null}
                        </div>
                    </section>
                )}
                {showMarketInfo && <section style={{ marginTop: 19.3 }}>
                    <h1 className="title-border-l-blue">调用订购情况</h1>
                    <div style={{ marginTop: 10 }}>
                        <span data-title="累计调用次数：" className="pseudo-title p-line api_item-margin">{getValueCallInfo('apiCallNum')}</span>
                        <span data-title="订购人数：" className="pseudo-title p-line api_item-margin">{getValueCallInfo('applyNum')}</span>
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
                                dataSource={getRequestDataSource()}
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
                                dataSource={getResponseDataSource()}
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
                                <JsonContent json={reqJson} />
                            </div>
                        </section>
                    </Col>
                    <Col span={11}>
                        <section>
                            <h1 className="title-border-l-blue">返回JSON样例</h1>
                            <div style={{ marginTop: 18 }}>
                                <JsonContent json={getValue('respJson')} />
                            </div>
                        </section>
                    </Col>
                </Row>
            </div>
        )
    }
}
export default CreateContentSection;
