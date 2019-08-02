import * as React from 'react';
import { Input, Form, Tooltip } from 'antd';

import { formItemLayout } from '../../../consts'
const FormItem = Form.Item;

export default class DtyarnShellConfig extends React.Component<any, any> {
    render () {
        const { singleButton, customView, isView, getFieldDecorator, securityStatus } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="jlogstash.root"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('dtyarnshellConf.jlogstashRoot', {
                            rules: [{
                                required: true,
                                message: '请输入jlogstash.root'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/dtstack/jlogstash" />
                        )}
                    </FormItem>
                    <FormItem
                        label="java.home"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('dtyarnshellConf.javaHome', {
                            rules: [{
                                required: false,
                                message: '请输入java.home'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/java/bin" />
                        )}
                    </FormItem>
                    <FormItem
                        label="hadoop.home.dir"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('dtyarnshellConf.hadoopHomeDir', {
                            rules: [{
                                required: true,
                                message: '请输入hadoop.home.dir'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/dtstack/hadoop-2.7.3" />
                        )}
                    </FormItem>
                    <FormItem
                        label="python2.path"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('dtyarnshellConf.python2Path', {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda3/bin/python2" />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="python3.path">python3.path</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('dtyarnshellConf.python3Path', {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                        )}
                    </FormItem>
                    {
                        securityStatus ? <div>
                            <FormItem
                                label="hdfsPrincipal"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('dtyarnshellConf.hdfsPrincipal', {
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
                                {getFieldDecorator('dtyarnshellConf.hdfsKeytabPath', {
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
                                {getFieldDecorator('dtyarnshellConf.hdfsKrb5ConfPath', {
                                    rules: [{
                                        required: true,
                                        message: '请输入hdfsKrb5ConfPath'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                        </div> : null
                    }
                    {customView}
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
