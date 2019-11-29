import * as React from 'react';
import { Form, Input, Select } from 'antd';
import { isEqual } from 'lodash';

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

    }

    componentDidMount () {
        const { infor = {}, form } = this.props;
        if (infor.id) {
            form.setFieldsValue({
                entityName: infor.entityName,
                dataSourceId: infor.dataSourceId,
                dataSourceTable: infor.dataSourceTable,
                entityPrimaryKey: infor.entityPrimaryKey,
                entityDesc: infor.entityDesc
            })
        }
    }

    componentDidUpdate (preProps: any) {
        const { infor = {}, form } = this.props;
        if (!isEqual(infor, preProps.infor)) {
            form.setFieldsValue({
                entityName: infor.entityName,
                dataSourceId: infor.dataSourceId,
                dataSourceTable: infor.dataSourceTable,
                entityPrimaryKey: infor.entityPrimaryKey,
                entityDesc: infor.entityDesc
            })
        }
    }

    handleSelectChange = (type: string, value) => {
        const { form, getDataTableList, getColumnList } = this.props;
        if (type == 'dataSourceId') {
            getDataTableList(false, value)
            getColumnList(true);
            form.setFieldsValue({
                dataSourceTable: undefined,
                entityPrimaryKey: undefined
            })
        } else {
            getColumnList(false, form.getFieldValue('dataSourceId'), value);
            form.setFieldsValue({
                entityPrimaryKey: undefined
            })
        }
    }

    render () {
        const { tableOptions, tableColOptions, dsOptions, infor = {} } = this.props;
        const { getFieldDecorator } = this.props.form;
        let isEdit = false;
        if (infor.id) {
            isEdit = true;
        }
        return (<Form style={{ padding: '40px 0px 20px' }}>
            <FormItem {...formItemLayout} label="实体名称" >
                {getFieldDecorator('entityName', {
                    rules: [{
                        required: true,
                        message: '实体名称不可为空！'
                    }, {
                        max: 20,
                        message: '实体名称不得超过20个字符！'
                    }]
                })(
                    <Input placeholder="请输入实体中文名称，20字以内" />
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="选择数据源" >
                {getFieldDecorator('dataSourceId', {
                    rules: [{
                        required: true,
                        message: '数据源不可为空！'
                    }]
                })(
                    <Select
                        showSearch
                        onChange={this.handleSelectChange.bind(this, 'dataSourceId')}
                        disabled={isEdit}
                        placeholder="请选择数据源"
                        filterOption={(input, option) => {
                            let temStr = option.props.children + '';
                            return temStr.indexOf(input) >= 0;
                        }}
                    >
                        {dsOptions.map((item: any) => (
                            <Option value={item.value} key={item.value}>{item.label}</Option>
                        ))}
                    </Select>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="选择数据表" >
                {getFieldDecorator('dataSourceTable', {
                    rules: [{
                        required: true,
                        message: '数据表不可为空！'
                    }]
                })(
                    <Select
                        showSearch
                        filterOption={(input, option) => {
                            let temStr = option.props.children + '';
                            return temStr.indexOf(input) >= 0;
                        }}
                        onChange={this.handleSelectChange.bind(this, 'dataSourceTable')}
                        disabled={isEdit}
                        placeholder="请选择数据表"
                    >
                        {tableOptions.map((item: any) => (
                            <Option value={item.value} key={item.value}>{item.label}</Option>
                        ))}
                    </Select>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="选择主键" >
                {getFieldDecorator('entityPrimaryKey', {
                    rules: [{
                        required: true,
                        message: '主键不可为空！'
                    }]
                })(
                    <Select
                        showSearch
                        filterOption={(input, option) => {
                            let temStr = option.props.children + '';
                            return temStr.indexOf(input) >= 0;
                        }}
                        disabled={isEdit}
                        placeholder="请选择维度作为实体主键"
                    >
                        {tableColOptions.map((item: any) => (
                            <Option value={item.value} key={item.value}>{item.label}</Option>
                        ))}
                    </Select>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="实体描述" >
                {getFieldDecorator('entityDesc', {
                    rules: [{
                        max: 255,
                        message: '描述不得超过255个字符！'
                    }]
                })(
                    <Input type="textarea" placeholder="请输入实体描述信息，长度限制在255个字符以内" />
                )}
            </FormItem>
        </Form>)
    }
}

export default Form.create<any>({})(BaseForm)
