import * as React from 'react';

import { Form, Tooltip, Icon } from 'antd';
import CallCountInput from '../callCountInput';

import { formItemLayout } from '../../consts'

const FormItem = Form.Item;

export default class CallCountFormItem extends React.Component<any, any> {
    render () {
        const { form, formItemLayout: customFormItemLayout, onChange, initialValue, disabled } = this.props;
        const { getFieldDecorator } = form;
        return (
            <FormItem
                label='调用次数'
                {...(customFormItemLayout || formItemLayout)}
            >
                {getFieldDecorator('callCount', {
                    rules: [
                        { required: true, message: '请输入调用次数' },
                        {
                            validator: function (rule: any, value: any, callback: any) {
                                if (value == -1) {
                                    callback();
                                    return;
                                }
                                if ((value || value === 0) && value < 1) {
                                    const error = '次数不能小于1'
                                    callback(error)
                                    return;
                                }
                                if (value > 999999999) {
                                    const error = '次数须小于10亿'
                                    callback(error)
                                    return;
                                }
                                callback();
                            }
                        }
                    ],
                    initialValue: initialValue
                })(<CallCountInput disabled={disabled} onChange={onChange} />)}
                <Tooltip title="当服务端多机部署时，可能会存在实际调用次数略大于申请次数的情况，但不会少于申请次数。">
                    <Icon type="question-circle" />
                </Tooltip>
            </FormItem>
        )
    }
}
