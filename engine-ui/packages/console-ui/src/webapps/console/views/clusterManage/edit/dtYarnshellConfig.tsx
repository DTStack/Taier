import * as React from 'react';
import { Input, Form, Tooltip } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'
const FormItem = Form.Item;

export default class DtyarnShellConfig extends React.Component<any, any> {
    render () {
        const { singleButton, customView, isView, getFieldDecorator, securityStatus, kerberosView } = this.props;
        console.log(securityStatus)
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="jlogstash.root"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.jlogstashRoot`, {
                            rules: [{
                                required: true,
                                message: '请输入jlogstash.root'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/dtstack/jlogstash" />
                        )}
                    </FormItem>
                    <FormItem
                        label="hadoop.home.dir"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.hadoopHomeDir`, {
                            rules: [{
                                required: true,
                                message: '请输入hadoop.home.dir'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/dtstack/hadoop-2.7.3" />
                        )}
                    </FormItem>
                    <FormItem
                        label="java.home"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.javaHome`, {
                            rules: [{
                                required: false,
                                message: '请输入java.home'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/java/bin" />
                        )}
                    </FormItem>
                    <FormItem
                        label="python2.path"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.python2Path`, {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda3/bin/python2" />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="python3.path">python3.path</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.python3Path`, {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                        )}
                    </FormItem>
                    {/* {
                        securityStatus ? <div>
                            <FormItem
                                label="hdfsPrincipal"
                                {...formItemLayout}
                            >
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.hdfsPrincipal`, {
                                    rules: [{
                                        required: true,
                                        message: '请输入hdfsPrincipal'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                            <FormItem
                                label="hdfsKeytabPath"
                                {...formItemLayout}
                            >
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.hdfsKeytabPath`, {
                                    rules: [{
                                        required: true,
                                        message: '请输入hdfsKeytabPath'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                            <FormItem
                                label="hdfsKrb5ConfPath"
                                {...formItemLayout}
                            >
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.hdfsKrb5ConfPath`, {
                                    rules: [{
                                        required: true,
                                        message: '请输入hdfsKrb5ConfPath'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                        </div> : null
                    } */}
                    {customView}
                    {kerberosView}
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
