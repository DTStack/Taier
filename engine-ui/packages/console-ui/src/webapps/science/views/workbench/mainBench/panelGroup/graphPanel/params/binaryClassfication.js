import React, { PureComponent } from 'react';
import { Form, Select, Input, InputNumber } from 'antd';
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
// 表选择
class FieldSetting extends PureComponent {
    state = {
        originalColumns: [{
            name: 'model_test_temp',
            id: 1
        }],
        sampleTags: [],
        columns: []
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { originalColumns, sampleTags, columns } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    label='原始标签列列名'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('originalColumn', {
                        rules: [{ required: true, message: '请选择原始标签列列名' }]
                    })(
                        <Select>
                            {originalColumns.map((item, index) => {
                                return <Option key={item.id} value={String(item.id)}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='分数列列名'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('scoreColumn', {
                        rules: [{ required: true, message: '请输入分数列列名' }]
                    })(
                        <Input placeholder="请输入分数列列名" />
                    )}
                </FormItem>
                <FormItem
                    label='正样本的标签值'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('sampleTags', {
                        rules: [{ required: true, message: '请输入正样本的标签值' }]
                    })(
                        <Select>
                            {sampleTags.map((item, index) => {
                                return <Option key={item.id} value={item.id}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='计算KS,PR等指标时按等频分成多少个桶'
                    {...formItemLayout}
                >
                    {getFieldDecorator('barrelCount', {
                        initialValue: 100,
                        rules: [
                            { required: true },
                            { max: 1000, message: '不可超过1000个桶', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={value => parseInt(value)}
                            formatter={value => parseInt(value)}
                            style={{ width: '100%' }} />
                    )}
                </FormItem>
                <FormItem
                    label={<div>分组列列名<span className="supplementary">仅支持string类型</span></div>}
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('columns', {
                        rules: [{ required: true, message: '请选择分组列列名' }]
                    })(
                        <Select>
                            {columns.filter(o => o.type === 'string').map((item, index) => {
                                return <Option key={item.id} value={item.id}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
            </Form>
        );
    }
}

/* main页面 */
class BinaryClassfication extends PureComponent {
    render () {
        const WrapFieldSetting = Form.create()(FieldSetting);
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    字段设置
                </div>
                <div className="params-single-tab-content">
                    <WrapFieldSetting />
                </div>
            </div>
        );
    }
}

export default BinaryClassfication;
