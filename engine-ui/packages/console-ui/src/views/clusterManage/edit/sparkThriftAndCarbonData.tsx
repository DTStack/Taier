import * as React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'

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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.jdbcUrl`, {
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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.username`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.password`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="driverClassName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.driverClassName`, {
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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.useConnectionPool`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="maxPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.maxPoolSize`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="minPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.minPoolSize`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="initialPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.initialPoolSize`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="jdbcIdel"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.jdbcIdel`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="maxRows"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.maxRows`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="queryTimeout"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.queryTimeout`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="checkTimeout"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER}.checkTimeout`)(
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
export class CarbonDataConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, singleButton, kerberosView } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="driverClassName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.CARBONDATA}.driverClassName`, {
                            rules: [{
                                required: true,
                                message: '请输入driverClassName'
                            }],
                            initialValue: 'org.apache.hive.jdbc.HiveDriver'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.CARBONDATA}.jdbcUrl`, {
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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.CARBONDATA}.username`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.CARBONDATA}.password`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    {kerberosView}
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
