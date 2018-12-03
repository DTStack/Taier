import React from 'react';
import { Table, Input, Button, Checkbox, Form, InputNumber } from 'antd'

import { API_METHOD_KEY } from '../../../consts';
import ColumnsModel from '../../../model/columnsModel'

const TextArea = Input.TextArea;
const FormItem = Form.Item;

const apiTestStatus = {
    NOTHING: 0,
    SUCCESS: 1,
    ERROR: 2
}
const pageInput = [new ColumnsModel({
    key: 'pageNo',
    type: 'INT',
    required: false
}), new ColumnsModel({
    key: 'pageSize',
    type: 'INT',
    required: false
})];
class TestApi extends React.Component {
    state = {
        results: [],
        status: apiTestStatus.NOTHING
    }
    prev () {
        this.props.prev();
    }

    initColumns () {
        const { getFieldDecorator } = this.props.form;
        return [
            {
                title: '参数名称',
                dataIndex: 'paramsName',
                width: '150px'
            },
            {
                title: '字段类型',
                dataIndex: 'type',
                width: '80px'
            },
            {
                title: '操作符',
                dataIndex: 'operator',
                width: '55px'
            },
            {
                title: '必填',
                dataIndex: 'required',
                render: (text, record) => {
                    return text ? '是' : '否'
                },
                width: '40px'
            },
            {
                title: '值',
                render: (text, record) => {
                    const { inFields } = this.props;
                    let data = inFields && inFields.inFields;
                    let initialValue;
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
                            {getFieldDecorator(record.paramsName, {
                                rules: [{
                                    required: record.required,
                                    message: '该参数为必填项'
                                }],
                                initialValue: initialValue
                            })(<Input />)}
                        </FormItem>)
                    }
                }
            }
        ]
    }
    testApi () {
        const { validateFieldsAndScroll } = this.props.form;
        validateFieldsAndScroll({}, (err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                this.props.apiTest(values)
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
        const arr = [];
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
        let status;
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
        const { inputParam, resultPageChecked } = this.props.paramsConfig;

        if (resultPageChecked) {
            return [...pageInput, ...inputParam];
        }
        return inputParam;
    }
    pass () {
        this.props.dataChange();
    }
    render () {
        const { loading } = this.state;
        const { basicProperties, respJson: testResult } = this.props;
        const wrapInputParams = this.wrapInputParams();
        const inputTableColumns = this.initColumns();
        const { outputResultColumns, x } = this.initOutColumns();
        return (
            <div>
                <div className="steps-content">
                    <div className="testApi_box">
                        <div className="left_box">
                            <p style={{ color: '#151515' }} className="required-tip middle-title">API({basicProperties.APIName})测试</p>
                            <div>
                                <p style={{ fontSize: '18px', marginTop: '2px' }}>
                                    <span className="shadowtext">请求方式：{API_METHOD_KEY[basicProperties.method]}</span>
                                    <span className="shadowtext" style={{ marginLeft: '8px' }}>返回类型：JSON</span>
                                </p>
                                <p style={{ marginTop: '10px', marginBottom: '6px' }} className="middle-title">输入参数：</p>
                                <Table
                                    className="m-table shadow"
                                    style={{ background: '#fff' }}
                                    rowKey="id"
                                    columns={inputTableColumns}
                                    dataSource={wrapInputParams}
                                    pagination={false}
                                    scroll={{ y: 286 }}
                                />
                                <Button loading={loading} style={{ marginTop: 12, float: 'right' }} onClick={() => this.testApi()}>开始测试</Button>
                            </div>
                        </div>
                        <div className="right_box">
                            <p style={{ color: '#151515' }} className="middle-title">测试结果：</p>
                            <div style={{ marginTop: '5px' }}>
                                <p className="small-title small-title-box">返回结果</p>
                                <TextArea className="textarea_white_disable" value={testResult ? JSON.stringify(testResult, null, 4) : null} disabled autosize={{ minRows: 8, maxRows: 20 }} />
                                <p style={{ marginTop: '20px' }} className="small-title small-title-box">输出结果</p>
                                <Table
                                    className="m-table table-border-without-top"
                                    style={{ background: '#fff' }}
                                    rowKey={(record, index) => {
                                        return index;
                                    }}
                                    columns={outputResultColumns}
                                    dataSource={testResult && testResult.data}
                                    pagination={false}
                                    scroll={{ y: 300, x: x }}
                                />
                                {this.getSaveMsg()}
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

export default Form.create()(TestApi);
