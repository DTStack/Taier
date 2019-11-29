import * as React from 'react';

import { Table } from 'antd';
import JsonContent from './jsonContent';

import { API_MODE, dataSourceTypes } from '../../consts';
import { generateHeader, generateTokenHeader } from './helper';

class CreateContentSection extends React.Component<any, any> {
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
            render (text: any) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',
            render (text: any) {
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
            render (text: any) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc',
            render (text: any) {
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
            apiConfig,
            reqJson,
            isGET,
            getValue,
            getRequestDataSource,
            getResponseDataSource
        } = this.props;
        return (
            <div>
                {showApiConfig && (
                    <section className='c-content-register__section'>
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
                <section className='c-content-register__section'>
                    <h1 className="title-border-l-blue">参数配置</h1>
                    <div style={{ marginTop: '16px' }} className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title'>输入参数</div>
                        <div className='c-content-register__section__card__content'>
                            <Table
                                rowKey="paramName"
                                style={{ marginTop: 18 }}
                                className="m-table border-table"
                                pagination={false}
                                dataSource={getRequestDataSource()}
                                scroll={{ y: 160 }}
                                columns={this.getRequestColumns()} />
                        </div>
                    </div>
                    <div className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title'>返回参数</div>
                        <div className='c-content-register__section__card__content'>
                            <Table
                                rowKey="paramName"
                                style={{ marginTop: 18 }}
                                className="m-table border-table"
                                pagination={false}
                                dataSource={getResponseDataSource()}
                                scroll={{ y: 160 }}
                                columns={this.getResponseColumns()} />
                        </div>
                    </div>
                </section>
                <div style={{ overflow: 'hidden' }}>
                    <section className='c-content-register__section c_left__section'>
                        <h1 className="title-border-l-blue">请求示例</h1>
                        <p className='c_title_method'>方式一：AK/SK签名加密方式</p>
                        <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Request URL</div>
                            <div className='c-content-register__section__card__content'>
                                <pre>{isGET ? reqJson : 'http(s)://调用URL'}</pre>
                            </div>
                        </div>
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Headers</div>
                            <div className='c-content-register__section__card__content'>
                                {generateHeader([])}
                            </div>
                        </div>
                        {!isGET && (
                            <div className='c-content-register__section__card'>
                                <div className='c-content-register__section__card__title c__section__card__title--bold'>Body</div>
                                <div className='c-content-register__section__card__content'>
                                    <JsonContent
                                        style={{ width: '410px' }}
                                        json={reqJson}
                                    />
                                </div>
                            </div>
                        )}
                    </section>
                    <section className='c-content-register__section c_left__section' style={{ margin: '45px 0 0 20px' }}>
                        <p className='c_title_method'>方式二：TOKEN加密方式</p>
                        <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Request URL</div>
                            <div className='c-content-register__section__card__content'>
                                <pre>{isGET ? reqJson : 'http(s)://调用URL'}</pre>
                            </div>
                        </div>
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Headers</div>
                            <div className='c-content-register__section__card__content'>
                                {generateTokenHeader([])}
                            </div>
                        </div>
                        {!isGET && (
                            <div className='c-content-register__section__card' style={{ marginTop: '80px' }}>
                                <div className='c-content-register__section__card__title c__section__card__title--bold'>Body</div>
                                <div className='c-content-register__section__card__content'>
                                    <JsonContent
                                        style={{ width: '410px' }}
                                        json={reqJson}
                                    />
                                </div>
                            </div>
                        )}
                    </section>
                </div>

                <section className='c-content-register__section'>
                    <h1 className="title-border-l-blue">返回结果</h1>
                    <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title'>返回JSON样例</div>
                        <div className='c-content-register__section__card__content'>
                            <JsonContent
                                style={{ width: '470px' }}
                                json={getValue('respJson')}
                            />
                        </div>
                    </div>
                </section>
            </div>
        )
    }
}
export default CreateContentSection;
