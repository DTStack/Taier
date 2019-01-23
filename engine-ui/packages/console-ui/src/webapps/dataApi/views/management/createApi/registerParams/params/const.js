import React from 'react';

import { Table, Input, Form } from 'antd';
import { PARAMS_POSITION, defaultAutoSize } from '../../../../../consts';
import { constColumnsKeys } from '../../../../../model/constColumnModel';
import {
    getTypeSelect,
    getCommonDelete,
    getPositionSelect,
    formItemParamOption,
    generateFormItemKey,
    mapPropsToFields,
    onValuesChange
} from '../helper';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

const columnsKeys = constColumnsKeys;

class RegisterParamsConst extends React.Component {
    renderCell (type, value, record) {
        const { form } = this.props;
        const { getFieldDecorator } = form;
        const { [columnsKeys.POSITION]: position, id } = record;
        const formKey = generateFormItemKey(type, id);
        switch (type) {
            case columnsKeys.NAME: {
                return (
                    <FormItem>
                        {getFieldDecorator(formKey, formItemParamOption)(
                            <Input />
                        )}
                    </FormItem>
                )
            }
            case columnsKeys.POSITION: {
                return (
                    <FormItem>
                        {getFieldDecorator(formKey, {})(
                            getPositionSelect(true)
                        )}
                    </FormItem>
                )
            }
            case columnsKeys.TYPE: {
                return (
                    <FormItem>
                        {getFieldDecorator(formKey, {})(
                            getTypeSelect()
                        )}
                    </FormItem>
                )
            }
            case columnsKeys.VALUE: {
                return (
                    <FormItem>
                        {getFieldDecorator(formKey, {
                            rules: [
                                {
                                    required: true,
                                    message: '请输入参数值'
                                }
                            ]
                        })(
                            <Input />
                        )}
                    </FormItem>
                )
            }
            case columnsKeys.DESC: {
                return (
                    <FormItem>
                        {getFieldDecorator(formKey, {})(
                            <TextArea autosize={defaultAutoSize} />
                        )}
                    </FormItem>
                )
            }
            case 'deal': {
                if (position == PARAMS_POSITION.PATH) {
                    return null
                } else {
                    return getCommonDelete(this.props, record);
                }
            }
            default: {
                return value;
            }
        }
    }
    initColumns () {
        return [{
            dataIndex: columnsKeys.NAME,
            title: '参数名称',
            width: '200px',
            render: (text, record) => {
                return this.renderCell(columnsKeys.NAME, text, record)
            }
        }, {
            dataIndex: columnsKeys.POSITION,
            title: '参数位置',
            width: '120px',
            render: (text, record) => {
                return this.renderCell(columnsKeys.POSITION, text, record)
            }
        }, {
            dataIndex: columnsKeys.TYPE,
            title: '字段类型',
            width: '120px',
            render: (text, record) => {
                return this.renderCell(columnsKeys.TYPE, text, record)
            }
        }, {
            dataIndex: columnsKeys.VALUE,
            title: '参数值',
            width: '200px',
            render: (text, record) => {
                return this.renderCell(columnsKeys.VALUE, text, record)
            }
        }, {
            dataIndex: columnsKeys.DESC,
            title: '说明',
            render: (text, record) => {
                return this.renderCell(columnsKeys.DESC, text, record)
            }
        }, {
            dataIndex: 'deal',
            title: '操作',
            width: '60px',
            render: (text, record) => {
                return this.renderCell('deal', text, record)
            }
        }]
    }
    render () {
        const { data = [] } = this.props;
        return (
            <Form layout='inline' className='l-form__inline-item--stretch'>
                <Table
                    rowKey='id'
                    className='m-table border-table'
                    columns={this.initColumns()}
                    dataSource={data}
                    pagination={false}
                    scroll={{ y: 300 }}
                />
            </Form>
        )
    }
}
export default Form.create({
    onValuesChange (props, values) {
        return onValuesChange(props, values);
    },
    mapPropsToFields (props) {
        return mapPropsToFields(props);
    }
})(RegisterParamsConst);
