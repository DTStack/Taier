import * as React from 'react';
import { Input, Select, Form, Tooltip } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'
const Option = Select.Option;
const FormItem = Form.Item;

export default class SparkConfig extends React.Component<any, any> {
    render () {
        const { singleButton, customView, isView, getFieldDecorator, securityStatus } = this.props;
        console.log(securityStatus)
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="版本选择"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARK}.typeName`, {
                            rules: [{
                                required: true,
                                message: '请选择Spark版本'
                            }],
                            initialValue: 'spark-yarn'
                        })(
                            <Select disabled={isView} style={{ width: '100px' }}>
                                <Option value="spark-yarn">2.X</Option>
                                <Option value="spark240-yarn">2.4.0</Option>
                                <Option value="spark300-yarn">3.X</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label="sparkYarnArchive"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARK}.sparkYarnArchive`, {
                            rules: [{
                                required: true,
                                message: '请输入sparkYarnArchive'
                            }],
                            initialValue: '/sparkjars/jars'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="sparkSqlProxyPath"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARK}.sparkSqlProxyPath`, {
                            rules: [{
                                required: true,
                                message: '请输入sparkSqlProxyPath'
                            }],
                            initialValue: '/user/spark/spark-0.0.1-SNAPSHOT.jar'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="sparkPythonExtLibPath"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARK}.sparkPythonExtLibPath`, {
                            rules: [{
                                required: true,
                                message: '请输入sparkPythonExtLibPath'
                            }],
                            initialValue: '/pythons/pyspark.zip,hdfs://ns1/pythons/py4j-0.10.4-src.zip'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="spark.yarn.appMasterEnv.PYSPARK_PYTHON">spark.yarn.appMasterEnv.PYSPARK_PYTHON</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARK}.sparkYarnAppMasterEnvPYSPARK_PYTHON`, {})(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON">spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARK}.sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON`, {})(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    {customView}
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
