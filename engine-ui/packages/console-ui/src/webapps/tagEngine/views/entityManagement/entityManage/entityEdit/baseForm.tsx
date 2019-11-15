import * as React from 'react';
import { Form, Input, Select } from 'antd';

const FormItem = Form.Item;
const { Option } = Select;

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    }
};

class BaseForm extends React.Component<any, any> {
    state: any = {
        tableOption: [
            { label: 'table1', value: '1' },
            { label: 'table2', value: '2' },
            { label: 'table3', value: '3' }
        ],
        primaryKeyOption: [
            { label: 'pk1', value: '1' },
            { label: 'pk2', value: '2' },
            { label: 'pk3', value: '3' }
        ]

    }

    componentDidMount () {
        const { infor = {}, form } = this.props;
        form.setFieldsValue({
            ...infor
        })
    }

    render () {
        const { tableOption, primaryKeyOption } = this.state;
        const { getFieldDecorator } = this.props.form;
        return (<Form style={{ padding: '40px 0px 20px' }}>
            <FormItem {...formItemLayout} label="实体名称" >
                {getFieldDecorator('name', {
                    rules: [{
                        required: true,
                        message: '实体名称不可为空！'
                    }]
                })(
                    <Input placeholder="请输入实体中文名称，20字以内的中文字符" />
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="选择数据源" >
                {getFieldDecorator('source', {
                    rules: [{
                        required: true,
                        message: '数据源不可为空！'
                    }],
                    initialValue: 'ES'
                })(
                    <Select disabled>
                        <Option value="ES">ES</Option>
                    </Select>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="选择数据表" >
                {getFieldDecorator('table', {
                    rules: [{
                        required: true,
                        message: '数据表不可为空！'
                    }]
                })(
                    <Select disabled={false} placeholder="请选择数据表">
                        {tableOption.map((item: any) => (
                            <Option value={item.value} key={item.value}>{item.label}</Option>
                        ))}
                    </Select>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="选择主键" >
                {getFieldDecorator('primaryKey', {
                    rules: [{
                        required: true,
                        message: '主键不可为空！'
                    }]
                })(
                    <Select disabled={false} placeholder="请选择维度作为实体主键">
                        {primaryKeyOption.map((item: any) => (
                            <Option value={item.value} key={item.value}>{item.label}</Option>
                        ))}
                    </Select>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="实体描述" >
                {getFieldDecorator('desc', {
                    rules: [{
                        max: 20,
                        message: '描述不得超过20个字符！'
                    }]
                })(
                    <Input type="textarea" placeholder="请输入实体描述信息，长度限制在20个字符以内" />
                )}
            </FormItem>
        </Form>)
    }
}

export default Form.create<any>({})(BaseForm)
