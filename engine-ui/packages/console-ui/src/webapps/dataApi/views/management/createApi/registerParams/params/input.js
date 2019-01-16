import React from 'react';

import { Table, Input, Form, Checkbox } from 'antd';
import { inputColumnsKeys } from '../../../../../model/inputColumnModel';
import { PARAMS_POSITION, PARAMS_POSITION_TEXT, defaultAutoSize } from '../../../../../consts';
import { getTypeSelect, getPositionSelect, generateFormItemKey, mapPropsToFields, onValuesChange } from './helper';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

/**
 * 服务端对应的属性名
 */
const columnsKeys = inputColumnsKeys;

class RegisterParamsInput extends React.Component {
    renderCell (type, value, record) {
        const { form } = this.props;
        const { getFieldDecorator } = form;
        const { [columnsKeys.POSITION]: position, id } = record;
        const formKey = generateFormItemKey(type, id);

        switch (type) {
            case columnsKeys.NAME: {
                if (position == PARAMS_POSITION.PATH) {
                    return value
                } else {
                    return (
                        <FormItem>
                            {getFieldDecorator(formKey, {})(
                                <Input />
                            )}
                        </FormItem>
                    )
                }
            }
            case columnsKeys.POSITION: {
                if (position == PARAMS_POSITION.PATH) {
                    return PARAMS_POSITION_TEXT[value]
                } else {
                    return (
                        <FormItem>
                            {getFieldDecorator(formKey, {})(
                                getPositionSelect()
                            )}
                        </FormItem>
                    )
                }
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
            case columnsKeys.ISREQUIRED: {
                if (position == PARAMS_POSITION.PATH) {
                    return value ? '是' : '否'
                } else {
                    return (
                        <FormItem>
                            {getFieldDecorator(formKey, {})(
                                <Checkbox />
                            )}
                        </FormItem>
                    )
                }
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
            default: {
                return value;
            }
        }
    }
    initColumns () {
        return [{
            dataIndex: columnsKeys.NAME,
            title: '参数名称',
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
            dataIndex: columnsKeys.ISREQUIRED,
            title: '是否必填',
            render: (text, record) => {
                return this.renderCell(columnsKeys.ISREQUIRED, text, record)
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
                return <a>删除</a>
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
                    scroll={{ y: 500 }}
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
})(RegisterParamsInput);
