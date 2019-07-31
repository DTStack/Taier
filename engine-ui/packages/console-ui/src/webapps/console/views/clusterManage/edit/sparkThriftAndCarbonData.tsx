import * as React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout } from '../../../consts'

const FormItem = Form.Item;

export class SparkThriftConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, customView, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.jdbcUrl', {
                            rules: [{
                                required: true,
                                message: '请输入jdbcUrl'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="用户名"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.username')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.password')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="driverClassName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.driverClassName', {
                            rules: [{
                                required: true,
                                message: '请输入driverClassName'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="useConnectionPool"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.useConnectionPool')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="maxPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.maxPoolSize')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="minPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.minPoolSize')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="initialPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.initialPoolSize')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="jdbcIdel"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.jdbcIdel')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="maxRows"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.maxRows')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="queryTimeout"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.queryTimeout')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="checkTimeout"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.checkTimeout')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    { customView }
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
export class CarbonDataConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('carbonConf.jdbcUrl', {
                            rules: [{
                                required: true,
                                message: '请输入jdbcUrl'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="用户名"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('carbonConf.username')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('carbonConf.password')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
