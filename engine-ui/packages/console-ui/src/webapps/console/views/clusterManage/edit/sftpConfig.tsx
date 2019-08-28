import * as React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'

const FormItem = Form.Item;

export default class SftpConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="主机名/IP"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.host`, {
                            rules: [{
                                required: true,
                                message: '请输入主机名/IP'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="端口"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.port`, {
                            rules: [{
                                required: true,
                                message: '请输入端口'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="路径"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.path`, {
                            rules: [{
                                required: true,
                                message: '请输入路径'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="用户名"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.username`, {
                            rules: [{
                                required: true,
                                message: '请输入用户名'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.password`, {
                            rules: [{
                                required: true,
                                message: '请输入密码'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
