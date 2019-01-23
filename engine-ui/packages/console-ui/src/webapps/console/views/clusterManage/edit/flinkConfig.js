import React from 'react';

import { Input, Select, Form, Tooltip, Checkbox } from 'antd';

import { formItemLayout } from '../../../consts'

const Option = Select.Option;
const FormItem = Form.Item;

export default class FlinkConfig extends React.Component {
    render () {
        const {
            customView,
            isView,
            securityStatus,
            getFieldDecorator,
            checked,
            changeCheckbox,
            gatewayHostValue,
            gatewayPortValue,
            gatewayJobNameValue,
            deleteOnShutdownOption,
            randomJobNameSuffixOption,
            getGatewayHostValue,
            getGatewayPortValue,
            getGatewayJobNameValue,
            changeDeleteOnShutdownOption,
            changeRandomJobNameSuffixOption
        } = this.props;
        return (
            <div className="config-content" style={{ width: '680px' }}>
                <FormItem
                    label="flinkZkAddress"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkZkAddress', {
                        rules: [{
                            required: true,
                            message: '请输入flinkZkAddress'
                        }]

                    })(
                        <Input disabled={isView} placeholder="hostname1:port,hostname2:port，多个地址用英文逗号隔开" />
                    )}
                </FormItem>
                <FormItem
                    label={<Tooltip title="flinkHighAvailabilityStorageDir">flinkHighAvailabilityStorageDir</Tooltip>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkHighAvailabilityStorageDir', {
                        rules: [{
                            required: true,
                            message: '请输入flinkHighAvailabilityStorageDir'
                        }]
                    })(
                        <Input disabled={isView} placeholder="Flink高可用存储地址，例如：/flink140/ha" />
                    )}
                </FormItem>
                <FormItem
                    label="flinkZkNamespace"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkZkNamespace', {
                        rules: [{
                            required: true,
                            message: '请输入flinkZkNamespace'
                        }]
                    })(
                        <Input disabled={isView} placeholder="Flink在Zookeeper的namespace，例如：/flink140" />
                    )}
                </FormItem>
                <FormItem
                    label="flinkClusterId"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkClusterId', {})(
                        <Input disabled={isView} />
                    )}
                </FormItem>

                <div className="checkboxStyle">
                    <Checkbox
                        checked={checked}
                        onChange={changeCheckbox}
                        disabled={isView}
                    >
                        配置Prometheus Metric地址
                    </Checkbox>
                </div>

                {checked ? (<div>
                    <FormItem
                        label="reporterClass"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.reporterClass', {
                            initialValue: 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter'
                        })(
                            <Input disabled={true} />
                        )}
                    </FormItem>
                    <FormItem
                        label="gatewayHost"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.gatewayHost', {
                            rules: [{
                                required: true,
                                message: '请输入gatewayHost'
                            }],
                            initialValue: gatewayHostValue
                        })(
                            <Input disabled={isView} onChange={getGatewayHostValue} />
                        )}
                    </FormItem>
                    <FormItem
                        label="gatewayPort"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.gatewayPort', {
                            rules: [{
                                required: true,
                                message: '请输入gatewayPort'
                            }],
                            initialValue: gatewayPortValue
                        })(
                            <Input disabled={isView} onChange={getGatewayPortValue} />
                        )}
                    </FormItem>
                    <FormItem
                        label="gatewayJobName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.gatewayJobName', {
                            rules: [{
                                required: true,
                                message: '请输入gatewayJobName'
                            }],
                            initialValue: gatewayJobNameValue
                        })(
                            <Input disabled={isView} onChange={getGatewayJobNameValue} />
                        )}
                    </FormItem>
                    <FormItem
                        label="deleteOnShutdown"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.deleteOnShutdown', {
                            rules: [{
                                required: true,
                                message: 'deleteOnShutdown'
                            }],
                            initialValue: deleteOnShutdownOption
                        })(
                            <Select disabled={isView} style={{ width: '100px' }} onChange={changeDeleteOnShutdownOption} >
                                <Option value="FALSE">FALSE</Option>
                                <Option value="TRUE">TRUE</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label="randomJobNameSuffix"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.randomJobNameSuffix', {
                            rules: [{
                                required: true,
                                message: 'randomJobNameSuffix'
                            }],
                            initialValue: randomJobNameSuffixOption
                        })(
                            <Select disabled={isView} style={{ width: '100px' }} onChange={changeRandomJobNameSuffixOption}>
                                <Option value="FALSE">FALSE</Option>
                                <Option value="TRUE">TRUE</Option>
                            </Select>
                        )}
                    </FormItem>
                </div>) : null
                }
                <FormItem
                    label="版本选择"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.typeName', {
                        rules: [{
                            required: true,
                            message: '请选择flink版本'
                        }],
                        initialValue: 'flink140'
                    })(
                        <Select disabled={isView} style={{ width: '100px' }}>
                            <Option value="flink140">1.4</Option>
                            <Option value="flink150">1.5</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label="clusterMode"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.clusterMode', {
                        rules: [{
                            required: true,
                            message: '请选择clusterMode'
                        }],
                        initialValue: 'yarn'
                    })(
                        <Select disabled={isView} style={{ width: '100px' }}>
                            <Option value="standalone">standalone</Option>
                            <Option value="yarn">yarn</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label="jarTmpDir"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.jarTmpDir', {
                        initialValue: '../tmp140'
                    })(
                        <Input disabled={isView} />
                    )}
                </FormItem>
                <FormItem
                    label="flinkPluginRoot"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkPluginRoot', {
                        initialValue: '/opt/dtstack/flinkplugin'
                    })(
                        <Input disabled={isView} />
                    )}
                </FormItem>
                <FormItem
                    label="remotePluginRootDir"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.remotePluginRootDir', {
                        initialValue: '/opt/dtstack/flinkplugin'
                    })(
                        <Input disabled={isView} />
                    )}
                </FormItem>

                <FormItem
                    label="flinkJarPath"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkJarPath', {
                        rules: [{
                            required: true,
                            message: '请输入flinkJarPath'
                        }]
                        // initialValue: "/opt/dtstack/flinkplugin"
                    })(
                        <Input disabled={isView} />
                    )}
                </FormItem>

                <FormItem
                    label="flinkJobHistory"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkJobHistory', {
                        rules: [{
                            required: true,
                            message: '请输入flinkJobHistory'
                        }]
                        // initialValue: "/opt/dtstack/flinkplugin"
                    })(
                        <Input disabled={isView} />
                    )}
                </FormItem>
                {
                    securityStatus ? <div>
                        <FormItem
                            label="flinkPrincipal"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkPrincipal', {
                                rules: [{
                                    required: true,
                                    message: '请输入flinkPrincipal'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="flinkKeytabPath"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkKeytabPath', {
                                rules: [{
                                    required: true,
                                    message: '请输入flinkKeytabPath'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="flinkKrb5ConfPath"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.flinkKrb5ConfPath', {
                                rules: [{
                                    required: true,
                                    message: '请输入flinkKrb5ConfPath'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="zkPrincipal"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.zkPrincipal', {
                                rules: [{
                                    required: true,
                                    message: '请输入zkPrincipal'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="zkKeytabPath"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.zkKeytabPath', {
                                rules: [{
                                    required: true,
                                    message: '请输入zkKeytabPath'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label="zkLoginName"
                            {...formItemLayout}
                        >
                            {getFieldDecorator('flinkConf.zkLoginName', {
                                rules: [{
                                    required: true,
                                    message: '请输入zkLoginName'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                    </div> : null
                }
                {customView}

            </div>
        )
    }
}
