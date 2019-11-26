import * as React from 'react';

import { Input, Select, Form, Tooltip, Checkbox } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'

const Option = Select.Option;
const FormItem = Form.Item;

export default class FlinkConfig extends React.Component<any, any> {
    render () {
        const {
            singleButton,
            customView,
            isView,
            securityStatus,
            getFieldDecorator,
            getFieldValue,
            checked,
            isZookeeper,
            onChangeZookeeper,
            changeCheckbox,
            kerberosView,
            setFieldsValue,
            resetFields
        } = this.props;
        console.log(securityStatus)
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="版本选择"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.typeName`, {
                            rules: [{
                                required: true,
                                message: '请选择flink版本'
                            }],
                            initialValue: 'flink140'
                        })(
                            <Select
                                disabled={isView}
                                style={{ width: '100px' }}
                                onChange={(value) => {
                                    let v = getFieldValue(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkSessionSlotCount`)
                                    resetFields([`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkSessionSlotCount`])
                                    setFieldsValue({
                                        [`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkSessionSlotCount`]: v
                                    })
                                }}
                            >
                                <Option value="flink140">1.4</Option>
                                <Option value="flink150">1.5</Option>
                                <Option value="flink180">1.8</Option>
                            </Select>
                        )}
                    </FormItem>
                    {
                        getFieldValue(`${COMPONEMT_CONFIG_KEYS.FLINK}.typeName`) === 'flink180'
                            ? (
                                <FormItem
                                    label="flinkSessionSlotCount"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkSessionSlotCount`, {
                                        rules: [{
                                            required: true,
                                            message: '请填写flinkSessionSlotCount'
                                        }]
                                    })(
                                        <Input
                                            disabled={isView}
                                            onChange={(e) => {
                                                setFieldsValue({
                                                    [`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkSessionSlotCount`]: e.target.value
                                                })
                                            }}
                                        />
                                    )}
                                </FormItem>
                            )
                            : (
                                <FormItem
                                    label="flinkSessionSlotCount"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkSessionSlotCount`, {})(
                                        <Input disabled={isView} />
                                    )}
                                </FormItem>
                            )
                    }
                    <FormItem
                        label="clusterMode"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.clusterMode`, {
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
                        label="high-availability"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.high-availability`, {
                            rules: [{
                                required: true,
                                message: '请输入high-availability'
                            }],
                            initialValue: 'NONE'
                        })(
                            <Select onChange={onChangeZookeeper} disabled={isView} style={{ width: '120px' }}>
                                <Option value="NONE">NONE</Option>
                                <Option value="ZOOKEEPER">ZOOKEEPER</Option>
                            </Select>
                        )}
                    </FormItem>
                    {
                        isZookeeper ? (
                            <React.Fragment>
                                <FormItem
                                    label="high-availability.cluster-id"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.high-availabilityCluster-id`, {
                                        rules: [{
                                            required: true,
                                            message: '请输入high-availability.cluster-id'
                                        }]
                                    })(
                                        <Input disabled={isView} />
                                    )}
                                </FormItem>
                                <FormItem
                                    label={<Tooltip title="high-availability.zookeeper.quorum">high-availability.zookeeper.quorum</Tooltip>}
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.high-availabilityZookeeperQuorum`, {
                                        rules: [{
                                            required: true,
                                            message: '请输入high-availability.zookeeper.quorum'
                                        }]

                                    })(
                                        <Input disabled={isView} placeholder="hostname1:port,hostname2:port，多个地址用英文逗号隔开" />
                                    )}
                                </FormItem>
                                <FormItem
                                    label={<Tooltip title="high-availability.storageDir">high-availability.storageDir</Tooltip>}
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.high-availabilityStorageDir`, {
                                        rules: [{
                                            required: true,
                                            message: '请输入high-availability.storageDir'
                                        }]
                                    })(
                                        <Input disabled={isView} placeholder="Flink高可用存储地址，例如：/flink140/ha" />
                                    )}
                                </FormItem>
                                <FormItem
                                    label="high-availability.zookeeper.path.root"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.high-availabilityZookeeperPathRoot`, {
                                        rules: [{
                                            required: true,
                                            message: '请输入high-availability.zookeeper.path.root'
                                        }]
                                    })(
                                        <Input disabled={isView} placeholder="Flink在Zookeeper的namespace，例如：/flink140" />
                                    )}
                                </FormItem>
                            </React.Fragment>
                        ) : null
                    }
                    <FormItem
                        label="flinkJarPath"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkJarPath`, {
                            rules: [{
                                required: true,
                                message: '请输入flinkJarPath'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>

                    <FormItem
                        label="historyserver.web.address"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.historyserverWebAddress`, {
                            rules: [{
                                required: true,
                                message: '请输入historyserver.web.address'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="historyserver.web.port"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.historyserverWebPort`, {
                            rules: [{
                                required: true,
                                message: '请输入historyserver.web.port'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="jarTmpDir"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.jarTmpDir`, {
                            initialValue: '../tmp140'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="flinkPluginRoot"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkPluginRoot`, {
                            initialValue: '/opt/dtstack/flinkplugin'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="remotePluginRootDir"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.remotePluginRootDir`, {
                            initialValue: '/opt/dtstack/flinkplugin'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="yarn.jobmanager.help.mb"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.yarnJobmanagerHelpMb`, {
                            initialValue: 1024
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="yarn.taskmanager.help.mb"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.yarnTaskmanagerHelpMb`, {
                            initialValue: 1024
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="yarn.taskmanager.numberOfTaskSlots">yarn.taskmanager.numberOfTaskSlots</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.yarnTaskmanagerNumberOfTaskSlots`, {
                            initialValue: 2
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="yarn.taskmanager.numberOfTaskManager">yarn.taskmanager.numberOfTaskManager</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.yarnTaskmanagerNumberOfTaskManager`, {
                            initialValue: 2
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>

                    <FormItem
                        label={<Tooltip title="state.checkpoints.dir">state.checkpoints.dir</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.stateCheckpointsDir`, {
                            rules: [{
                                required: true,
                                message: '请输入state.checkpoints.dir'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="state.checkpoints.num-retained">state.checkpoints.num-retained</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.stateCheckpointsNum-retained`, {
                            rules: [{
                                required: true,
                                message: '请输入state.checkpoints.num-retained'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="jobmanager.archive.fs.dir">jobmanager.archive.fs.dir</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.jobmanagerArchiveFsDir`, {
                            rules: [{
                                required: true,
                                message: '请输入jobmanager.archive.fs.dir'
                            }]
                        })(
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
                            label={<Tooltip title="metrics.reporter.promgateway.class">metrics.reporter.promgateway.class</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.metricsReporterPromgatewayClass`, {
                                initialValue: 'org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter'
                            })(
                                <Input disabled={true} />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="metrics.reporter.promgateway.host">metrics.reporter.promgateway.host</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.metricsReporterPromgatewayHost`, {
                                rules: [{
                                    required: true,
                                    message: '请输入metrics.reporter.promgateway.host'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="metrics.reporter.promgateway.port">metrics.reporter.promgateway.port</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.metricsReporterPromgatewayPort`, {
                                rules: [{
                                    required: true,
                                    message: '请输入metrics.reporter.promgateway.port'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="metrics.reporter.promgateway.jobName">metrics.reporter.promgateway.jobName</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.metricsReporterPromgatewayJobName`, {
                                rules: [{
                                    required: true,
                                    message: '请输入metrics.reporter.promgateway.jobName'
                                }]
                            })(
                                <Input disabled={isView} />
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="metrics.reporter.promgateway.randomJobNameSuffix">metrics.reporter.promgateway.randomJobNameSuffix</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.metricsReporterPromgatewayRandomJobNameSuffix`, {
                                rules: [{
                                    required: true,
                                    message: '请输入metrics.reporter.promgateway.randomJobNameSuffix'
                                }],
                                initialValue: 'TRUE'
                            })(
                                <Select disabled={isView} style={{ width: '100px' }} >
                                    <Option value="FALSE">FALSE</Option>
                                    <Option value="TRUE">TRUE</Option>
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label={<Tooltip title="metrics.reporter.promgateway.deleteOnShutdown">metrics.reporter.promgateway.deleteOnShutdown</Tooltip>}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.metricsReporterPromgatewayDeleteOnShutdown`, {
                                rules: [{
                                    required: true,
                                    message: '请输入metrics.reporter.promgateway.deleteOnShutdown'
                                }],
                                initialValue: 'TRUE'
                            })(
                                <Select disabled={isView} style={{ width: '100px' }}>
                                    <Option value="FALSE">FALSE</Option>
                                    <Option value="TRUE">TRUE</Option>
                                </Select>
                            )}
                        </FormItem>
                    </div>) : null
                    }
                    {/* {
                        securityStatus ? <div>
                            <FormItem
                                label="flinkPrincipal"
                                {...formItemLayout}
                            >
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkPrincipal`, {
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
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkKeytabPath`, {
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
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.flinkKrb5ConfPath`, {
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
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.zkPrincipal`, {
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
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.zkKeytabPath`, {
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
                                {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.FLINK}.zkLoginName`, {
                                    rules: [{
                                        required: true,
                                        message: '请输入zkLoginName'
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
