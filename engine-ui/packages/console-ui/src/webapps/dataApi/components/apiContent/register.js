import React from 'react';

import { Table } from 'antd';

import { inputColumnsKeys } from '../../model/inputColumnModel';
import { constColumnsKeys } from '../../model/constColumnModel';
import ErrorColumnModel from '../../model/errroColumnModel';

import JsonContent from './jsonContent';
import { generateUrlQuery, generateHeader, generateBody } from './helper';
import { PARAMS_POSITION_TEXT } from '../../consts'

class RegisterContentSection extends React.Component {
    initInputColumns () {
        return [{
            dataIndex: inputColumnsKeys.NAME,
            title: '参数名称',
            width: '200px'
        }, {
            dataIndex: inputColumnsKeys.POSITION,
            title: '参数位置',
            render (text) {
                return PARAMS_POSITION_TEXT[text];
            },
            width: '150px'
        }, {
            dataIndex: inputColumnsKeys.TYPE,
            title: '数据类型',
            width: '150px'
        }, {
            dataIndex: inputColumnsKeys.ISREQUIRED,
            title: '必填',
            width: '100px',
            render (text) {
                return text ? '是' : '否'
            }
        }, {
            dataIndex: inputColumnsKeys.DESC,
            title: '说明',
            width: '250px'
        }]
    }
    initConstColumns () {
        return [{
            dataIndex: constColumnsKeys.NAME,
            title: '参数名称',
            width: '200px'
        }, {
            dataIndex: constColumnsKeys.POSITION,
            title: '参数位置',
            width: '150px'
        }, {
            dataIndex: constColumnsKeys.TYPE,
            title: '数据类型',
            width: '150px'
        }, {
            dataIndex: constColumnsKeys.VALUE,
            title: '参数值',
            width: '200px'
        }, {
            dataIndex: constColumnsKeys.DESC,
            title: '说明',
            width: '250px'
        }]
    }
    initErrorColumns () {
        return [{
            dataIndex: ErrorColumnModel.columnKeys.ERRORCODE,
            title: '错误码',
            width: '150px'
        }, {
            dataIndex: ErrorColumnModel.columnKeys.MSG,
            title: '错误信息',
            width: '250px'
        }, {
            dataIndex: ErrorColumnModel.columnKeys.SOLUTION,
            title: '解决方案',
            width: '300px'
        }]
    }
    render () {
        const { isManage, getValue, registerInfo = {} } = this.props;
        let inputParam = isManage ? registerInfo.inputParam : getValue('reqParam');
        let inputColumn = inputParam || [];
        let constColumn = inputColumn.filter((column) => {
            return column.constant
        })
        inputColumn = inputColumn.filter((column) => {
            return !column.constant
        })
        return (
            <div>
                <section className='c-content-register__section'>
                    <h1 className="title-border-l-blue">请求配置</h1>
                    <div style={{ marginTop: '16px' }} className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title'>输入参数</div>
                        <div className='c-content-register__section__card__content'>
                            <Table
                                rowKey={(record, index) => {
                                    return index
                                }}
                                className="m-table border-table"
                                pagination={false}
                                dataSource={inputColumn}
                                scroll={{ y: 160 }}
                                columns={this.initInputColumns()} />
                        </div>
                    </div>
                    {isManage && (
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title'>常量参数</div>
                            <div className='c-content-register__section__card__content'>
                                <Table
                                    rowKey="paramName"
                                    className="m-table border-table"
                                    pagination={false}
                                    dataSource={constColumn}
                                    scroll={{ y: 160 }}
                                    columns={this.initConstColumns()} />
                            </div>
                        </div>
                    )}
                </section>
                <section className='c-content-register__section'>
                    <h1 className="title-border-l-blue">请求示例</h1>
                    <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title c__section__card__title--bold'>Reuest URL</div>
                        <div className='c-content-register__section__card__content'>
                            {generateUrlQuery(inputColumn)}
                        </div>
                    </div>
                    <div className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title c__section__card__title--bold'>Headers</div>
                        <div className='c-content-register__section__card__content'>
                            {generateHeader(inputColumn)}
                        </div>
                    </div>
                    <div className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title c__section__card__title--bold'>Body</div>
                        <div className='c-content-register__section__card__content'>
                            {generateBody(inputColumn, getValue('reqMethod'))}
                        </div>
                    </div>
                </section>
                <section className='c-content-register__section'>
                    <h1 className="title-border-l-blue">返回结果</h1>
                    <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title'>正常返回示例</div>
                        <div className='c-content-register__section__card__content'>
                            <JsonContent
                                style={{ width: '470px' }}
                                json={getValue('successRespJson')}
                            />
                        </div>
                    </div>
                    <div className='c-content-register__section__card'>
                        <div className='c-content-register__section__card__title'>错误返回示例</div>
                        <div className='c-content-register__section__card__content'>
                            <JsonContent
                                style={{ width: '470px' }}
                                json={getValue('errorRespJson')}
                            />
                        </div>
                    </div>
                </section>
                <section className='c-content-register__section'>
                    <h1 className="title-border-l-blue">错误码</h1>
                    <Table
                        rowKey="errorCode"
                        className="m-table border-table"
                        style={{ marginTop: '12px' }}
                        pagination={false}
                        dataSource={getValue('errorCodeList') || []}
                        scroll={{ y: 160 }}
                        columns={this.initErrorColumns()} />
                </section>
            </div>
        )
    }
}
export default RegisterContentSection;
