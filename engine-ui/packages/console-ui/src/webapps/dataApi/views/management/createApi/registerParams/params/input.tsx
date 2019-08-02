import * as React from 'react';

import { Table, Input, Form, Checkbox } from 'antd';
import { inputColumnsKeys } from '../../../../../model/inputColumnModel';
import { PARAMS_POSITION, PARAMS_POSITION_TEXT, defaultAutoSize, API_METHOD } from '../../../../../consts';
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

/**
 * 服务端对应的属性名
 */
const columnsKeys = inputColumnsKeys;

class RegisterParamsInput extends React.Component<any, any> {
    renderCell (type: any, value: any, record: any) {
        const { form, method } = this.props;
        const { getFieldDecorator } = form;
        const { [columnsKeys.POSITION]: position, id } = record;
        const formKey = generateFormItemKey(type, id);

        switch (type) {
            case columnsKeys.NAME: {
                if (position == PARAMS_POSITION.PATH) {
                    return value;
                } else {
                    return (
                        <FormItem>
                            {getFieldDecorator(formKey, formItemParamOption)(
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
                                getPositionSelect(method == API_METHOD.GET || method == API_METHOD.DELETE)
                            )}
                        </FormItem>
                    )
                }
            }
            case columnsKeys.TYPE: {
                if (position == PARAMS_POSITION.PATH) {
                    return value;
                } else {
                    return (
                        <FormItem>
                            {getFieldDecorator(formKey, {})(
                                getTypeSelect()
                            )}
                        </FormItem>
                    )
                }
            }
            case columnsKeys.ISREQUIRED: {
                if (position == PARAMS_POSITION.PATH) {
                    return value ? '是' : '否'
                } else {
                    return (
                        <FormItem>
                            {getFieldDecorator(formKey, {
                                valuePropName: 'checked'
                            })(
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
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.NAME, text, record)
            }
        }, {
            dataIndex: columnsKeys.POSITION,
            title: '参数位置',
            width: '120px',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.POSITION, text, record)
            }
        }, {
            dataIndex: columnsKeys.TYPE,
            title: '字段类型',
            width: '120px',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.TYPE, text, record)
            }
        }, {
            dataIndex: columnsKeys.ISREQUIRED,
            title: '是否必填',
            width: '100px',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.ISREQUIRED, text, record)
            }
        }, {
            dataIndex: columnsKeys.DESC,
            title: '说明',
            render: (text: any, record: any) => {
                return this.renderCell(columnsKeys.DESC, text, record)
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
    onValuesChange (props: any, values: any) {
        return onValuesChange(props, values);
    },
    mapPropsToFields (props: any) {
        return mapPropsToFields(props);
    }
})(RegisterParamsInput);
