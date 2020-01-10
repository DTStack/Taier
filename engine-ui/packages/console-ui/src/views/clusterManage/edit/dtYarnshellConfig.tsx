import * as React from 'react';
import { Input, Form, Tooltip, Select } from 'antd';
import CommTitle from '../../../components/commTitle';
import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'
const FormItem = Form.Item;
const Option = Select.Option;

export default class DtyarnShellConfig extends React.Component<any, any> {
    render () {
        const { singleButton, customCommView, customPythonView, customJupyterView,
            isView, getFieldDecorator, securityStatus, kerberosView } = this.props;
        console.log(securityStatus)
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    {/* 公共参数 */}
                    <CommTitle titleName='公共参数'/>
                    <FormItem
                        label="java.home"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.javaHome`, {
                            rules: [{
                                required: true,
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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.hadoopHomeDir`, {
                            rules: [{
                                required: true,
                                message: '请输入hadoop.home.dir'
                            }]
                        })(
                            <Input disabled={isView} placeholder="/opt/dtstack/hadoop-2.7.3" />
                        )}
                    </FormItem>
                    {customCommView}
                    {/* Python */}
                    <CommTitle titleName='Python'/>
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
                    {customPythonView}
                    {/* Jupyter */}
                    <CommTitle titleName='Jupyter Notebook'/>
                    <FormItem
                        label={<Tooltip title="jupyter.path">jupyter.path</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.jupyterPath`, {
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="c.NotebookApp.open_browser">c.NotebookApp.open_browser</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.cNotebookAppOpen_browser`, {
                            initialValue: 'FALSE'
                        })(
                            <Select disabled={isView} style={{ width: '100px' }} >
                                <Option value="TRUE">TRUE</Option>
                                <Option value="FALSE">FALSE</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="c.NotebookApp.allow_remote_access">c.NotebookApp.allow_remote_access</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.cNotebookAppAllow_remote_access`, {
                            initialValue: 'TRUE'
                        })(
                            <Select disabled={isView} style={{ width: '100px' }} >
                                <Option value="TRUE">TRUE</Option>
                                <Option value="FALSE">FALSE</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="c.NotebookApp.ip">c.NotebookApp.ip</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.cNotebookAppIp`, {
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="c.NotebookApp.token">c.NotebookApp.token</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.cNotebookAppToken`, {
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="c.NotebookApp.default_url">c.NotebookApp.default_url</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.DTYARNSHELL}.cNotebookAppDefault_url`, {
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>

                    {/* <FormItem
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
                    </FormItem> */}
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
                    {customJupyterView}
                    {kerberosView}
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
