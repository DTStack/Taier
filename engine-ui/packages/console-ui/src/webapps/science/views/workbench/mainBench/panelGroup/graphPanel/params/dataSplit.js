import React, { PureComponent } from 'react';
import { Form, Tabs, Select, InputNumber, Input } from 'antd';
import { MemorySetting as BaseMemorySetting } from './typeChange';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const formItemLayout = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
const inputStyle = {
    width: '100%'
}
/* 字段设置 */
class ParamSetting extends PureComponent {
    state = {
        tableData: [{
            title: 'name',
            type: 'string'
        }, {
            title: 'age',
            type: 'int'
        }]
    }
    validatRate = (rule, value, callback) => {
        if (value > 0 && value < 1) {
            callback()
        } else {
            callback(new Error('取值范围为（0，1）'));
        }
    }
    normalize = (value) => {
        if (parseInt(value) == value) {
            return parseInt(value)
        } else {
            return value
        }
    }
    renderFormItme = () => {
        const { tableData } = this.state;
        const { getFieldDecorator } = this.props.form;
        const selectedValue = this.props.form.getFieldValue('splitWay');
        const prefixSelector = getFieldDecorator('prefix', {
            initialValue: '0'
        })(
            <Select style={{ width: 48 }}>
                <Option value="0">{`>`}</Option>
                <Option value="1">{`>=`}</Option>
                <Option value="2">{`<`}</Option>
                <Option value="3">{`<=`}</Option>
                <Option value="4">{`=`}</Option>
            </Select>
        );
        switch (selectedValue) {
            case '0':
                return (
                    <>
                        <FormItem
                            label={<div style={{ display: 'inline-block' }}>拆分比例<span className="supplementary">表1占原数据的比例，取值范围为（0，1）</span></div>}
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('splitRate', {
                                rules: [
                                    { required: true, message: '请输入拆分比例' },
                                    { validator: this.validatRate }
                                ]
                            })(
                                <InputNumber style={inputStyle} />
                            )}
                        </FormItem>
                        <FormItem
                            label={<div style={{ display: 'inline-block' }}>随机数种子<span className="supplementary">系统可默认生成</span></div>}
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('random', {
                                rules: [
                                    { required: false },
                                    {
                                        type: 'number', min: 1
                                    }
                                ]
                            })(
                                <InputNumber
                                    parser={value => value ? parseInt(value) : value}
                                    formatter={value => value ? parseInt(value) : value}
                                    style={inputStyle} />
                            )}
                        </FormItem>
                    </>
                )
            case '1':
                return (
                    <>
                        <FormItem
                            label={<div style={{ display: 'inline-block' }}>阈值列<span className="supplementary">选择单列，不支String列</span></div>}
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('thresholdCol', {
                                rules: [
                                    { required: true, message: '请选择阈值列' }
                                ]
                            })(
                                <Select>
                                    {
                                        tableData.filter(o => o.type === 'string').map((item, index) => {
                                            return (
                                                <Option value={item.title} key={index}>{item.title}</Option>
                                            )
                                        })
                                    }
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label={<div style={{ display: 'inline-block' }}>阈值<span className="supplementary">表1数据拆分结果</span></div>}
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('threshold', {
                                rules: [
                                    { required: true, message: '请输入阈值' },
                                    { type: 'number', message: '只允许输入数字' }
                                ],
                                normalize: this.normalize
                            })(
                                <Input addonBefore={prefixSelector} style={inputStyle} />
                            )}
                        </FormItem>
                    </>
                )
            default: return <></>
        }
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                <FormItem
                    label='拆分方式'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('splitWay', {
                        initialValue: '0',
                        rules: [{ required: true, message: '请选择拆分方式' }]
                    })(
                        <Select>
                            <Option value='0'>按比例拆分</Option>
                            <Option value='1'>按阈值拆分</Option>
                        </Select>
                    )}
                </FormItem>
                {this.renderFormItme()}
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props) {
        super(props)
    }
}
class DataSplit extends PureComponent {
    state = {
        data: {}
    }
    render () {
        // const { data } = this.state;
        const WrapParamSetting = Form.create()(ParamSetting);
        const WrapMemorySetting = Form.create()(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="参数设置" key="1">
                    <WrapParamSetting />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting />
                </TabPane>
            </Tabs>
        );
    }
}

export default DataSplit;
