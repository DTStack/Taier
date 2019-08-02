import * as React from 'react';

import { Table, Form, Input } from 'antd';

import ErrorColumnModel from '../../../../../model/errroColumnModel';
import {
    generateFormItemKey,
    getCommonDelete,
    onValuesChange,
    mapPropsToFields
} from '../helper';

const columnsKeys = ErrorColumnModel.columnKeys;
const FormItem = Form.Item;

class RegisterErrorCodeForm extends React.Component<any, any> {
    renderCell (type: any, value: any, record: any) {
        const { form } = this.props;
        const { getFieldDecorator } = form;
        const { id } = record;
        const formKey = generateFormItemKey(type, id);
        const formOption: any = {
            [columnsKeys.MSG]: {
                rules: [
                    {
                        required: true,
                        message: '请输入错误信息'
                    }
                ]
            },
            [columnsKeys.ERRORCODE]: {
                rules: [
                    {
                        required: true,
                        message: '请输入错误码'
                    }
                ]
            },
            [columnsKeys.SOLUTION]: {}
        }
        switch (type) {
            case 'deal': {
                return getCommonDelete(this.props, record);
            }
            case columnsKeys.ERRORCODE:
            case columnsKeys.MSG:
            case columnsKeys.SOLUTION: {
                return <FormItem>
                    {getFieldDecorator(formKey, formOption[type])(
                        <Input />
                    )}
                </FormItem>
            }
        }
    }
    initColumns () {
        return [{
            dataIndex: columnsKeys.ERRORCODE,
            title: '错误码',
            width: '200px',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.ERRORCODE, text, record)
            }
        }, {
            dataIndex: columnsKeys.MSG,
            title: '错误信息',
            width: '120px',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.MSG, text, record)
            }
        }, {
            dataIndex: columnsKeys.SOLUTION,
            title: '解决方案',
            width: '120px',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.SOLUTION, text, record)
            }
        }, {
            dataIndex: 'deal',
            title: '操作',
            width: '60px',
            render: (text: any, record: any) => {
                return this.renderCell('deal', text, record)
            }
        }]
    }
    render () {
        const { data = [] } = this.props;
        return (
            <div>
                <Form layout='inline' className='l-form__inline-item--stretch'>
                    <Table
                        rowKey='id'
                        className='m-table border-table'
                        columns={this.initColumns()}
                        dataSource={data}
                        pagination={false}
                    />
                </Form>
            </div>
        )
    }
}
export default Form.create({
    onValuesChange (props: any, values: any) {
        return onValuesChange(props, values);
    },
    mapPropsToFields (props: any) {
        return mapPropsToFields(props);
    }
})(RegisterErrorCodeForm);
