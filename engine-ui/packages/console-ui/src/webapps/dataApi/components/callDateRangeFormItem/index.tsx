import * as React from 'react';

import { Form } from 'antd';
import CallDateRange from '../callDateRange';

import { formItemLayout } from '../../consts'

const FormItem = Form.Item;

export default class CallCountFormItem extends React.Component<any, any> {
    render () {
        const { form, formItemLayout: customFormItemLayout, onChange, initialValue, disabled } = this.props;
        const { getFieldDecorator } = form;
        return (
            <FormItem
                label='调用周期'
                {...(customFormItemLayout || formItemLayout)}
            >
                {getFieldDecorator('callDateRange', {
                    rules: [
                        {
                            validator: function (rule: any, value: any, callback: any) {
                                if (value == null || value == undefined) {
                                    const error = '请选择调用周期';
                                    callback(error);
                                    return;
                                }
                                if (value && !value.length) {
                                    callback();
                                    return;
                                }
                                callback();
                            }
                        }
                    ],
                    initialValue: initialValue
                })(<CallDateRange disabled={disabled} onChange={onChange} />)}
            </FormItem>
        )
    }
}
