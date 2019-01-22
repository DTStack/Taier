import React from 'react';
import { Input, Select, Form, Tooltip } from 'antd';

import { formItemLayout } from '../../../consts'
const Option = Select.Option;
const FormItem = Form.Item;

export default class SparkConfig extends React.Component {
    render () {
        const { customView, isView, getFieldDecorator } = this.props;
        return (
            <div className="config-content" style={{ width: '680px' }}>
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
                {customView}
            </div>
        )
    }
}
