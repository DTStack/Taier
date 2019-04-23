import React, { PureComponent } from 'react';
import { Form, Select, Checkbox, InputNumber } from 'antd';
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
class ChooseTable extends PureComponent {
    state = {
        tables: [],
        partitionCheck: false // 选择的数据表是否为分区表
    }
    handleChange = (value) => {
        this.props.form.setFieldsValue({
            tableName: String(value)
        })
        this.setState({
            tables: [{
                name: '11111'
            }]
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { tables, partitionCheck } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    label={
                        <div style={{ display: 'inline-block' }}>
                            表名
                            <span className="supplementary">支持新建表，若要写入分区表，需提前建好分区表</span>
                        </div>}
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('tableName', {
                        rules: [{ required: true, message: '请选择表名' }]
                    })(
                        <Select
                            mode="combobox"
                            notFoundContent="未找到数据表"
                            placeholder="请选择表名"
                            defaultActiveFirstOption={false}
                            filterOption={false}
                            onChange={this.handleChange}
                        >
                            {tables.map((item, index) => {
                                return <Option key={index} value={item.name}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                    <Checkbox disabled checked={partitionCheck}>分区</Checkbox>
                </FormItem>
                <FormItem
                    colon={false}
                    label='设置表生命周期'
                    {...formItemLayout}
                >
                    {getFieldDecorator('lifeDay', {
                        initialValue: 28,
                        rules: [{ required: true, message: '请填写表生命周期' }]
                    })(
                        <InputNumber style={{ width: '100%' }} />
                    )}
                </FormItem>
            </Form>
        );
    }
}

/* main页面 */
class WriteDatabase extends PureComponent {
    render () {
        const WrapChooseTable = Form.create()(ChooseTable);
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    表选择
                </div>
                <div className="params-single-tab-content">
                    <WrapChooseTable />
                </div>
            </div>
        );
    }
}

export default WriteDatabase;
