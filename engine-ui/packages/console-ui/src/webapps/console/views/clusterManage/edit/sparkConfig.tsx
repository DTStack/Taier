import * as React from 'react';
import { Input, Select, Form, Tooltip } from 'antd';

import { formItemLayout } from '../../../consts'
const Option = Select.Option;
const FormItem = Form.Item;

export default class SparkConfig extends React.Component<any, any> {
    render () {
        const { singleButton, customView, isView, getFieldDecorator, securityStatus } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="版本选择"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('sparkConf.typeName', {
                            rules: [{
                                required: true,
                                message: '请选择Spark版本'
                            }],
                            initialValue: 'spark_yarn'
                        })(
                            <Select disabled={isView} style={{ width: '100px' }}>
                                <Option value="spark_yarn">2.X</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label="sparkYarnArchive"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('sparkConf.sparkYarnArchive', {
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
                        {getFieldDecorator('sparkConf.sparkSqlProxyPath', {
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
                        {getFieldDecorator('sparkConf.sparkPythonExtLibPath', {
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
                        {getFieldDecorator('sparkConf.sparkYarnAppMasterEnvPYSPARK_PYTHON', {})(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON">spark.yarn.appMasterEnv.PYSPARK_DRIVER_PYTHON</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('sparkConf.sparkYarnAppMasterEnvPYSPARK_DRIVER_PYTHON', {})(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    {
                        securityStatus ? <div>
                            <FormItem
                                label="sparkPrincipal"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('sparkConf.sparkPrincipal', {
                                    rules: [{
                                        required: true,
                                        message: '请输入sparkPrincipal'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                            <FormItem
                                label="sparkKeytabPath"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('sparkConf.sparkKeytabPath', {
                                    rules: [{
                                        required: true,
                                        message: '请输入sparkKeytabPath'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                            <FormItem
                                label="sparkKrb5ConfPath"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('sparkConf.sparkKrb5ConfPath', {
                                    rules: [{
                                        required: true,
                                        message: '请输入sparkKrb5ConfPath'
                                    }]
                                })(
                                    <Input disabled={isView} />
                                )}
                            </FormItem>
                            <FormItem
                                label="zkPrincipal"
                                {...formItemLayout}
                            >
                                {getFieldDecorator('sparkConf.zkPrincipal', {
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
                                {getFieldDecorator('sparkConf.zkKeytabPath', {
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
                                {getFieldDecorator('sparkConf.zkLoginName', {
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
                {singleButton}
            </React.Fragment>
        )
    }
}
