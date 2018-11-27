import React from "react";
import utils from "utils";
import { Input, Select, Form, Tooltip, Checkbox } from "antd";

import { formItemLayout } from "../../../consts"

const Option = Select.Option;
const FormItem = Form.Item;

export default class FlinkConfig extends React.Component {

    render() {
        const {
            customView,
            isView,
            getFieldDecorator,
            checked,
            changeCheckbox,
            firstIptValue,
            secondIptValue,
            thirdIptValue,
            firstOption,
            secondOption,
            getFirstInputValue,
            getSecondInputValue,
            getThirdInputValue,
            changeFirstOption,
            changeSecondOption
        } = this.props;
        return (
            <div className="config-content" style={{ width: "680px" }}>
                <FormItem
                    label="flinkZkAddress"
                    {...formItemLayout}
                >
                    {getFieldDecorator('flinkConf.flinkZkAddress', {
                        rules: [{
                            required: true,
                            message: "请输入flinkZkAddress"
                        }],

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
                            message: "请输入flinkHighAvailabilityStorageDir"
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
                            message: "请输入flinkZkNamespace"
                        }]
                    })(
                        <Input disabled={isView} placeholder="Flink在Zookeeper的namespace，例如：/flink140" />
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
                            initialValue: "org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter"
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
                                message: "请输入gatewayHost"
                            }],
                            initialValue: firstIptValue
                        })(
                            <Input disabled={isView} onChange={getFirstInputValue} />
                        )}
                    </FormItem>
                    <FormItem
                        label="gatewayPort"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.gatewayPort', {
                            rules: [{
                                required: true,
                                message: "请输入gatewayPort"
                            }],
                            initialValue: secondIptValue
                        })(
                            <Input disabled={isView} onChange={getSecondInputValue} />
                        )}
                    </FormItem>
                    <FormItem
                        label="gatewayJobName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.gatewayJobName', {
                            rules: [{
                                required: true,
                                message: "请输入gatewayJobName"
                            }],
                            initialValue: thirdIptValue
                        })(
                            <Input disabled={isView} onChange={getThirdInputValue} />
                        )}
                    </FormItem>
                    <FormItem
                        label="deleteOnShutdown"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('flinkConf.deleteOnShutdown', {
                            rules: [{
                                required: true,
                                message: "deleteOnShutdown"
                            }],
                            initialValue: firstOption
                        })(
                            <Select disabled={isView} style={{ width: "100px" }} onChange={changeFirstOption} >
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
                                message: "randomJobNameSuffix"
                            }],
                            initialValue: secondOption
                        })(
                            <Select disabled={isView} style={{ width: "100px" }} onChange={changeSecondOption}>
                                <Option value="FALSE">FALSE</Option>
                                <Option value="TRUE">TRUE</Option>
                            </Select>
                        )}
                    </FormItem>
                </div>) : null
                }
                {customView}

            </div>
        )
    }
}