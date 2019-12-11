import * as React from 'react';
import { Input, Form, Radio, Tooltip, Icon } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'

const RadioGroup = Radio.Group;
const FormItem = Form.Item;
let timer: any = null;

export default class SftpConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, singleButton, getFieldValue, setFieldsValue } = this.props;
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
                        label="存储路径"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.path`, {
                            rules: [{
                                required: true,
                                message: '请输入存储路径'
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
                        label="认证方式"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.auth`, {
                            rules: [{
                                required: true, message: '认证方式不可为空！'
                            }],
                            initialValue: '1'
                        })(
                            <RadioGroup
                                disabled={isView}
                                onChange={(e: any) => {
                                    if (e.target.value === '2') {
                                        timer = setTimeout(() => {
                                            setFieldsValue({
                                                [`${COMPONEMT_CONFIG_KEYS.SFTP}.rsaPath`]: '～/.ssh/id_rsa'
                                            })
                                            clearTimeout(timer);
                                            timer = null;
                                        })
                                    }
                                }}
                            >
                                <Radio value="1">密码</Radio>
                                <Radio value="2">私钥</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {
                        getFieldValue(`${COMPONEMT_CONFIG_KEYS.SFTP}.auth`) !== '2'
                            ? (
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
                            )
                            : (
                                <FormItem
                                    label="私钥地址"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.rsaPath`, {
                                        rules: [{
                                            required: true,
                                            message: '请输入私钥地址'
                                        }]
                                    })(
                                        <Input disabled={isView} />
                                    )}
                                    <Tooltip overlayClassName="big-tooltip" title={'用户的私钥储存路径，默认为～/.ssh/id_rsa'}>
                                        <Icon style={{ position: 'absolute', right: -20, top: 10 }} type="question-circle-o" />
                                    </Tooltip>
                                </FormItem>
                            )
                    }
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
