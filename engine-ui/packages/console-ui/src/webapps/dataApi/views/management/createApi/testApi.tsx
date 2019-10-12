import * as React from 'react';
import { Table, Input, Button, Checkbox, Form, InputNumber } from 'antd'

import { API_METHOD_KEY, PARAMS_POSITION } from '../../../consts';
import ColumnsModel from '../../../model/columnsModel'
import { inputColumnsKeys } from '../../../model/inputColumnModel';
import ConstColumnModel, { constColumnsKeys } from '../../../model/constColumnModel';

import Editor from 'widgets/editor'
import utils from 'utils';
import { expandJSONObj } from 'funcs';

const TextArea = Input.TextArea;
const FormItem = Form.Item;

const apiTestStatus: any = {
    NOTHING: 0,
    SUCCESS: 1,
    ERROR: 2
}
const pageInput: any = [new ColumnsModel({
    key: 'pageNo',
    type: 'INT',
    required: false
}), new ColumnsModel({
    key: 'pageSize',
    type: 'INT',
    required: false
})];
class TestApi extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            results: [],
            status: apiTestStatus.NOTHING,
            sync: true,
            testBody: props.registerParams.bodyDesc
        }
    }
    prev () {
        this.props.prev();
    }
    getValueCell (record: any) {
        const { inFields, isRegister } = this.props;
        const { getFieldDecorator } = this.props.form;
        /**
         * 假如是常量类型，直接显示值
         */
        if (isRegister && record instanceof ConstColumnModel) {
            return (record as any)[constColumnsKeys.VALUE];
        }
        let data = inFields && inFields.inFields;
        let initialValue: any;
        if (record.paramsName == 'pageNo' || record.paramsName == 'pageSize') {
            initialValue = inFields && inFields[record.paramsName];
            return <FormItem
                style={{ marginBottom: '0px' }}
            >
                {getFieldDecorator(record.paramsName, {
                    rules: [{
                        required: record.required,
                        message: '该参数为必填项'
                    }, {
                        pattern: /^\d*$/,
                        message: '请输入数字'
                    }],
                    initialValue: initialValue
                })(<InputNumber min={1} style={{ width: '100%' }} />)}
            </FormItem>
        } else {
            initialValue = data && data[record.paramsName];
            return (<FormItem
                style={{ marginBottom: '0px' }}
            >
                {getFieldDecorator(isRegister ? record[inputColumnsKeys.NAME] : record.paramsName, {
                    rules: [{
                        required: isRegister ? record[[inputColumnsKeys.ISREQUIRED] as any] : record.required,
                        message: '该参数为必填项'
                    }],
                    initialValue: initialValue
                })(<Input />)}
            </FormItem>)
        }
    }
    initColumns () {
        const { isRegister } = this.props;
        let columns: any = [
            {
                title: '参数名称',
                dataIndex: isRegister ? inputColumnsKeys.NAME : 'paramsName',
                width: '150px'
            },
            {
                title: '字段类型',
                dataIndex: isRegister ? inputColumnsKeys.TYPE : 'type',
                width: '80px'
            },
            {
                title: '必填',
                dataIndex: isRegister ? inputColumnsKeys.ISREQUIRED : 'required',
                render: (text: any, record: any) => {
                    return text ? '是' : '否'
                },
                width: '40px'
            },
            {
                title: '值',
                dataIndex: 'value',
                render: (text: any, record: any) => {
                    return this.getValueCell(record);
                }
            }
        ]
        if (!isRegister) {
            columns.splice(1, 0, {
                title: '操作符',
                dataIndex: 'operator',
                width: '55px'
            })
        }
        return columns;
    }
    initConstColumns () {
        return [
            {
                title: '参数名称',
                dataIndex: constColumnsKeys.NAME,
                width: '150px'
            },
            {
                title: '字段类型',
                dataIndex: constColumnsKeys.TYPE,
                width: '110px'
            },
            {
                title: '值',
                dataIndex: constColumnsKeys.VALUE,
                width: '150px'
            }
        ]
    }
    testApi () {
        const { validateFieldsAndScroll, getFieldsValue } = this.props.form;
        const { testBody } = this.state;
        const values = expandJSONObj(getFieldsValue());
        console.log('testApi values:', values);
        validateFieldsAndScroll({}, (err: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                this.props.apiTest(values, { bodyDesc: testBody })
                    .then(() => {
                        this.setState({
                            loading: false
                        })
                    });
            }
        })
    }
    initOutColumns () {
        let x = 100;
        const arr: any = [];
        const { outputParam } = this.props.paramsConfig;
        for (let key in outputParam) {
            let value = outputParam[key].paramsName;
            arr.push({
                title: value,
                dataIndex: value,
                width: Math.max(value.length * 8 + 20, 50) + 'px'
            })
            x = x + Math.max(value.length * 8 + 20, 50);
        }
        return { outputResultColumns: arr, x };
    }
    getSaveMsg () {
        const { respJson } = this.props;
        let status: any;
        if (respJson) {
            status = respJson.success ? apiTestStatus.SUCCESS : apiTestStatus.ERROR;
        } else {
            status = apiTestStatus.NOTHING;
        }
        switch (status) {
            case apiTestStatus.NOTHING: {
                return null;
            }
            case apiTestStatus.SUCCESS: {
                return <Checkbox onChange={this.props.saveResult} checked={this.props.isSaveResult} style={{ marginTop: '8px' }}>将测试结果作为JSON样例保存</Checkbox>;
            }
            case apiTestStatus.ERROR: {
                return '测试失败!请查看返回结果错误信息'
            }
        }
    }
    wrapInputParams () {
        const { isRegister, registerParams } = this.props;
        const { inputParam, resultPageChecked } = this.props.paramsConfig;
        const { inputParam: registerInputParams } = this.props.registerParams;
        if (!isRegister) {
            if (resultPageChecked) {
                return [...pageInput, ...inputParam];
            }
            return inputParam || [];
        } else {
            /**
             * 存在bodyDesc，则不现实body参数
             */
            if (registerParams.bodyDesc) {
                return [].concat(registerInputParams || []).filter((param: any) => {
                    if (param[inputColumnsKeys.POSITION] == PARAMS_POSITION.BODY) {
                        return false
                    }
                    return true;
                });
            }
            return [].concat(registerInputParams || []);
        }
    }
    getConstColumnData () {
        const { constParam } = this.props.registerParams;
        return constParam;
    }
    editorChange (value: any) {
        this.setState({
            sync: false,
            testBody: value
        })
    }
    pass () {
        this.props.dataChange();
    }
    renderRequest (request: any) {
        if (!request) {
            return null;
        }
        function renderHeaders (headers = {}) {
            if (!headers) {
                return '';
            }
            let keyAndValus = Object.entries(headers);
            return keyAndValus.map(([key, value]) => {
                return `\t${key}: ${value}`;
            }).join('\n')
        }
        function renderBody (body: any) {
            return body.split('\n').join('\n\t');
        }
        const { requestUrl, headers, body, method } = request;
        return (`Request URL: ${requestUrl}\n\n` +
            `Request Method: ${method}\n\n` +
            `Headers:\n${renderHeaders(headers)}\n\n` +
            `Body:\n\t${renderBody(body)}\n\n`
        );
    }
    render () {
        const { loading, sync, testBody } = this.state;
        const { basicProperties, registerParams, respJson: testResult = {}, isRegister } = this.props;
        const wrapInputParams = this.wrapInputParams();
        const inputTableColumns = this.initColumns();
        const { outputResultColumns, x } = this.initOutColumns();
        const { bodyDesc } = registerParams;
        let resultText: any;
        let requestInfo: any;
        if (isRegister) {
            let data = testResult ? testResult.data : null;
            if (data) {
                resultText = utils.jsonFormat(data.result) || data.result;
                requestInfo = this.renderRequest(data.httpInfo);
            }
        } else {
            resultText = testResult ? JSON.stringify(testResult, null, 4) : null
        }
        return (
            <div>
                <div className="steps-content">
                    <div className="testApi_box">
                        <div className="left_box">
                            <p style={{ color: '#151515' }} className="required-tip middle-title">API({basicProperties.APIName})测试</p>
                            <div>
                                <p style={{ fontSize: '18px', marginTop: '2px' }}>
                                    <span className="shadowtext">请求方式：{API_METHOD_KEY[basicProperties.method]}</span>
                                    {!isRegister && (
                                        <span className="shadowtext" style={{ marginLeft: '8px' }}>返回类型：JSON</span>
                                    )}
                                </p>
                                <p style={{ marginTop: '10px', marginBottom: '6px' }} className="middle-title">输入参数：</p>
                                <Table
                                    className="m-table m-table--border shadow"
                                    style={{ background: '#fff' }}
                                    rowKey="id"
                                    columns={inputTableColumns}
                                    dataSource={wrapInputParams}
                                    pagination={false}
                                    scroll={{ y: 286 }}
                                />
                                {isRegister && (
                                    <React.Fragment>
                                        <p style={{ marginTop: '10px', marginBottom: '6px' }} className="middle-title">常量参数：</p>
                                        <Table
                                            className="m-table m-table--border shadow"
                                            style={{ background: '#fff' }}
                                            rowKey="id"
                                            columns={this.initConstColumns()}
                                            dataSource={this.getConstColumnData()}
                                            pagination={false}
                                            scroll={{ y: 286 }}
                                        />
                                    </React.Fragment>
                                )}
                                {!!bodyDesc && (
                                    <React.Fragment>
                                        <p style={{ marginTop: '10px', marginBottom: '6px' }} className="middle-title">body参数：</p>
                                        <Editor
                                            sync={sync}
                                            onChange={this.editorChange.bind(this)}
                                            language='plaintext'
                                            style={{
                                                height: '218px',
                                                minHeight: '218px',
                                                border: '1px solid #DDDDDD'
                                            }}
                                            options={{ minimap: { enabled: false } }}
                                            value={testBody}
                                        />
                                    </React.Fragment>
                                )}
                                <Button loading={loading} style={{ marginTop: 12, float: 'right' }} onClick={() => this.testApi()}>开始测试</Button>
                            </div>
                        </div>
                        <div className="right_box">
                            <p style={{ color: '#151515' }} className="middle-title">测试结果：</p>
                            {isRegister && (
                                <div style={{ marginTop: '5px', marginBottom: '20px' }}>
                                    <p className="small-title small-title-box">请求详情</p>
                                    <TextArea className="textarea_white_disable" value={requestInfo} disabled autosize={{ minRows: isRegister ? 12 : 8, maxRows: 20 }} />
                                </div>
                            )}
                            <div style={{ marginTop: '5px' }}>
                                <p className="small-title small-title-box">返回结果</p>
                                <TextArea className="textarea_white_disable" value={resultText} disabled autosize={{ minRows: isRegister ? 12 : 8, maxRows: 20 }} />
                                {!isRegister && (
                                    <React.Fragment>
                                        <p style={{ marginTop: '20px' }} className="small-title small-title-box">输出结果</p>
                                        <Table
                                            className="m-table table-border-without-top"
                                            style={{ background: '#fff' }}
                                            rowKey={(record: any, index: any) => {
                                                return index;
                                            }}
                                            columns={outputResultColumns}
                                            dataSource={testResult && testResult.data}
                                            pagination={false}
                                            scroll={{ y: 300, x: x }}
                                        />
                                        {this.getSaveMsg()}
                                    </React.Fragment>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
                <div
                    className="steps-action"
                >
                    {
                        <Button style={{ marginLeft: 8 }} onClick={() => this.prev()}>上一步</Button>
                    }
                    {
                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>完成</Button>
                    }

                </div>
            </div>
        )
    }
}

export default Form.create<any>()(TestApi);
